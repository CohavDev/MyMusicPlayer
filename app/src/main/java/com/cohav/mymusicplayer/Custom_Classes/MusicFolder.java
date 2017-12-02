package com.cohav.mymusicplayer.Custom_Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.support.v4.content.ContextCompat;

import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by shaulcohav on 18/07/17.
 */

public class MusicFolder implements Serializable {
    private static Context context;
    private String songname, authorName, duration;
    private int durationInt;
    //need to do this:
    private File thumbnail;
    private File file;
    public MusicFolder(File file){
        this.file = file;
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());
        this.songname = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        this.authorName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if(this.songname == null||this.songname==""){
            this.songname = file.getName();
        }
        Integer durationNum = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        this.durationInt = durationNum;
        this.duration="";
        //------------set duration
        final int HOUR = 60*60*1000;
        final int MINUTE = 60*1000;
        final int SECOND = 1000;
        int hourInt = durationNum /HOUR;
        int minutesInt = durationNum /MINUTE%60;
        int secondsInt = durationNum / SECOND %60;
        String hour = String.valueOf(hourInt);
        String minutes = String.valueOf(minutesInt);
        String seconds = String.valueOf(secondsInt);
        if(hourInt!=0 ){
            if(hourInt<10){
                hour = "0"+hour;
            }
            if(minutesInt<10){
                minutes = "0"+minutes;
            }
            this.duration+=hour+":";
        }
        if(secondsInt<10){
            seconds="0"+seconds;
        }
        this.duration+=minutes+":"+seconds;
        System.out.println(songname);

        setThumbnail();
    }
    public String getSongName(){
        return songname;
    }
    public String getAuthorName(){
        return authorName;
    }
    public String getDuration(){
        return duration;
    }
    public int getDurationInt(){return durationInt;}
    public File getFile(){
        return file;
    }
    public File getThumbnail(){
        return this.thumbnail;
    }
    public static void setContextMusicFolder(Context context){
        MusicFolder.context = context;
    }
    private void setThumbnail(){
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());
        byte[] data = metadataRetriever.getEmbeddedPicture();

        //creating files
        File cachFolder = new File(context.getCacheDir(),File.separator+"cachThumbnails");
        if(!cachFolder.exists()){
            cachFolder.mkdirs();
        }
        File tempFile = new File(cachFolder.getAbsolutePath(),file.getName());
        try {
            FileOutputStream fos;
            BufferedOutputStream bfs;
            if (!tempFile.exists()) {
                if(data!=null && data.length!=0) {
                    tempFile.createNewFile();
                    fos = new FileOutputStream(tempFile);
                    bfs = new BufferedOutputStream(fos);
                    bfs.write(data);
                    bfs.flush();
                    bfs.close();
                    fos.close();
                    this.thumbnail = tempFile;
                }
                else{
                    this.thumbnail = null;
                }

            }
            else{
                if(data!=null){

                    this.thumbnail = tempFile;
                }

                else
                    this.thumbnail = null;
            }

            System.out.println("--------------------------\n-----------"+this.thumbnail);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setThumbnailFile(File file){
        this.thumbnail = file;
    }
}
