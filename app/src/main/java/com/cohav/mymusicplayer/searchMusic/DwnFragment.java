package com.cohav.mymusicplayer.searchMusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cohav.mymusicplayer.Custom_Classes.AlertDialogMsg;
import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.MyMusic.MyMusicActivity;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.scraping_websites.Scraping;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
/**
 * Created by shaulcohav on 04/07/17.
 */
public class DwnFragment extends Fragment {
    private String vidID = "";
    private String dwnURL = "";
    private ProgressBar progressBar;
    private TextView dwnText;
    private SearchAdapter.SearchViewHolder myViewHolder;
    private VideoItem item;
    private boolean isDwn = false;


    public void setText(){
        progressBar = (ProgressBar) (myViewHolder.dwnLayout).findViewById(R.id.pBar2);
        progressBar.setIndeterminate(false);
        dwnText = (TextView)(myViewHolder.dwnLayout).findViewById(R.id.dwn_text2);
        myViewHolder.dwnLayout.setVisibility(View.VISIBLE);
    }

    public void handleDwn(VideoItem item,SearchAdapter.SearchViewHolder myViewHolder, String id){
        if(isDwn){
            AlertDialogMsg.showMsg(getContext(),"ERROR","Another download is in progress\nPlease wait and try again later");
        }
        else {
            vidID = id;
            this.myViewHolder = myViewHolder;
            this.item = item;
            setText();
            Scraping myscraping = new Scraping("DwnFragment",getActivity(),getContext(),"https://www.youtube.com/watch?v="+vidID);
            myscraping.createWebView();
        }


    }
    public void startDwn(String url){
        dwnURL = url;
        if(dwnURL !=null && dwnURL.length()>5) {
            new DownloadSrc().execute();
        }
        else{
            AlertDialogMsg.showMsg(getActivity(),"Error","Wrong url was scraped");
        }
    }
    public void setMetaData(File file, String title, String author, final String thumbnailUrl){
        System.out.println("------URL of thumbnail:   "+thumbnailUrl);
        try {
            MP3File f = (MP3File) AudioFileIO.read(file);
            Tag tag;
            if (!f.hasID3v2Tag()) {
                //tag = new ID3v24Tag();
                //f.setID3v2Tag(tag);
            } else {
                //tag = f.getID3v2TagAsv24();

            }
            tag = f.getTagAndConvertOrCreateAndSetDefault();
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ARTIST, author);
            tag.setField(FieldKey.ALBUM, title);
            Wrapper w = new Wrapper(f, tag, thumbnailUrl);
            //async task
            new ThumbnailTask().execute(w);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }
    private static class ThumbnailTask extends AsyncTask<Object, Void, Wrapper>{
            @Override
            protected Wrapper doInBackground(Object... thumbnailUrl) {
                byte[]array = null;
                Wrapper wrapper = (Wrapper) thumbnailUrl[0];
                try {
                    URL url = new URL(wrapper.url);
                    URLConnection con = url.openConnection();
                    InputStream stream = con.getInputStream();
                    int imglength = con.getContentLength();
                    array = new byte[imglength];
                    int bytesRead = 0;
                    while(bytesRead < imglength){
                        int n = stream.read(array,bytesRead,imglength - bytesRead);
                        if(n<=0){
                            System.out.println("Error in the size mabye??");
                        }
                        bytesRead+=n;
                    }

                }
                catch(MalformedURLException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                if(array != null){
                    wrapper.arr = array;
                    return wrapper;
                }
                return null;
            }

            @Override
            protected void onPostExecute (Wrapper wrapper){
                if(wrapper.arr!=null) {
                    try {
                        Artwork artwork = ArtworkFactory.getNew();
                        artwork.setBinaryData(wrapper.arr);
                        wrapper.tag.deleteArtworkField();
                        wrapper.tag.setField(artwork);
                    }
                    catch (Exception e){
                        throw new RuntimeException(e);
                    }

                }
                try {
                    wrapper.audioFile.commit();
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }
                //tell gallery
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(wrapper.audioFile.getFile()));
                wrapper.activity.sendBroadcast(intent);
                //refresh
                MainActivity activity = (MainActivity) wrapper.activity; //need to be changed and refresh music on its fragment, not here.
                MyMusicActivity fragment = activity.getMyMusicFrag();
                fragment.updateDwn();
            }
    }
    public class Wrapper{
        public String url;
        public MP3File audioFile;
        public Tag tag;
        public byte[]arr;
        public Activity activity;
        public Wrapper(MP3File f, Tag tag, String url){
            this.url = url;
            this.tag = tag;
            this.audioFile = f;
            this.activity = getActivity();
        }
    }
    private class DownloadSrc extends AsyncTask<Integer, Integer, Integer> {
        private String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
        private String fileName = "";
        private File file;
        private Runnable runnable;
        private Handler handler;
        private boolean dwnState = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isDwn){
                cancel(true);
            }
            else{
                isDwn = true;
            }
            System.out.println("starting download");
            MusicPlayerView fragment = (MusicPlayerView) getActivity().getSupportFragmentManager().findFragmentById(R.id.musicPlayerView);
            if(fragment!=null) {
                fragment.destroyMediaPlayer();
            }
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println(dwnState);
                    if(!dwnState){
                        cancel(true);
                    }
                }
            };
            handler.postDelayed(runnable,1000*5);

        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int count;
            try {
                URL url = new URL(dwnURL);
                System.out.println(url.toString());
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();
                String depo = connection.getHeaderField("Content-Disposition");
                int countTry = 0;
                while(depo==null && countTry<10){
                    connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                    depo = connection.getHeaderField("Content-Disposition");
                    countTry++;

                }
                System.out.println("Count Try = "+countTry);

                if(depo==null ||depo ==""){
                    //cancel asyncTask
                    cancel(true);
                }
                else{
                    String depoSplit[] = depo.split("filename=");
                    fileName = depoSplit[1].replace("filename=", "").replace("\"", "").trim();
                }

                dwnState = true;
                //file length
                int lengthOfFile = connection.getContentLength();
                //intput - read file
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                file = new File(root,fileName);
                //output - write file

                //File file = new File(root,fileName);
                //System.out.println(file.getPath());
                //output if directory or not
                //change statement
                if(!file.exists()) {
                    System.out.println("Downloading");
                    OutputStream outputStream = new FileOutputStream(file.getPath());
                    //System.out.print(file.getPath());
                    System.out.println(file.getPath());
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = inputStream.read(data)) != -1) {
                        total += count;
                        //progress
                        publishProgress((int) ((total * 100) / lengthOfFile));

                        //writing to file
                        outputStream.write(data,0, count);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    //major part of the file wasnt written.
                    if(total<(lengthOfFile/2)){
                        return -1;
                    }

                    return 100;
                }
                else{
                    System.out.println("aborting  download, its directory or already exists");
                    return 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            isDwn = false;
            switch (result){
                case 0:
                    System.out.println("File not download");
                    dwnText.setText("Aborted\nFile is already exist");
                    AlertDialogMsg.showMsg(getContext(),"Download Failed","File not downloaded !\nIt already exists");
                    break;
                case 100:
                    System.out.println("Downloaded - finished");
                    dwnText.setText("Downloaded File Successfuly");
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    getActivity().sendBroadcast(intent);
                    //refresh myMusic
                    updateMyMusic();

                    break;
                default:System.out.println("an error occured while downloading file");
                    dwnText.setText("Error - try again later");
                    AlertDialogMsg.showMsg(getContext(),"ERROR","an Error occured while trying to download the file\nPlease try again later");
                    //clean
                    if(file.exists()){
                        file.delete();
                    }
                    break;
            }


        }
        @Override
        public void onCancelled(Integer result){
            isDwn = false;
            System.out.println("Error Server \nan error occured while downloading file");
            AlertDialogMsg.showMsg(getContext(),"Server ERROR","an Error in the Server occured while trying to download the file.\n\nPlease try again later");
            //clean
            if(file!=null&&file.exists()){
                file.delete();
            }
            dwnText.setText("Error");
        }
        @Override
        protected void onProgressUpdate(Integer... values){

            dwnText.setText("Downloading FIle....");
            progressBar.setProgress(values[0]);
        }
        public void updateMyMusic(){
            //setting metadata
            System.out.println(item.getInfo()+" "+item.getHighThumbnail());
            setMetaData(file,item.getTitle(),item.getInfo(),item.getHighThumbnail());

        }

    }
}