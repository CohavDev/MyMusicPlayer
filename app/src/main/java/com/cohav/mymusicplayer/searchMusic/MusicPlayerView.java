package com.cohav.mymusicplayer.searchMusic;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cohav.mymusicplayer.AlertDialogMsg;
import com.cohav.mymusicplayer.Custom_Classes.CircleTransform;
import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.Custom_Classes.MusicFolder;
import com.cohav.mymusicplayer.MyMusic.MyMusicActivity;
import com.cohav.mymusicplayer.MyMusic.myMusicAdapter;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.scraping_websites.Scraping;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaulcohav on 14/07/17.
 */

public class MusicPlayerView extends Fragment {
    private View bottomSheet;
    private TextView songName, authorName, length,timeCurrent;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton play_Hide,pause_Hide, play_Extend,pause_Extend,skip_next,skip_prev;
    private ImageView thumbnail_hide,thumbnail_show;
    private ConstraintLayout noSongPlaying;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private final int HOUR = 60*60*1000;
    private final int MINUTE = 60*1000;
    private final int SECOND = 1000;
    private String urlVid;
    private MyTask streamingTask;
    private myMusicAdapter adapter;
    //private ProgressBar loadingPB;
    private VideoItem item;
    private List<MusicFolder> mList;
    private int pos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.bottom_sheet,parent,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //layout
//        getActivity().findViewById(R.id.bottomSheetSpace).setVisibility(View.GONE);
        //buttons
        play_Hide = (ImageButton) view.findViewById(R.id.play_hide);
        pause_Hide=(ImageButton)view.findViewById(R.id.pause_hide);
        play_Extend =(ImageButton)view.findViewById(R.id.playBtn);
        pause_Extend=(ImageButton)view.findViewById(R.id.pauseBtn);
        skip_next = (ImageButton)view.findViewById(R.id.skipNext);
        skip_prev = (ImageButton)view.findViewById(R.id.SkipPrev);
        //textViews
        songName =(TextView) view.findViewById(R.id.songName_hide);
        authorName = (TextView) view.findViewById(R.id.authorName_hide);
        songName.setSelected(true);
        authorName.setSelected(true);
        length = (TextView) view.findViewById(R.id.time_end);
        timeCurrent = (TextView)view.findViewById(R.id.time_current);
        //image view
        thumbnail_hide = (ImageView)view.findViewById(R.id.song_thumbnail_hide);
        thumbnail_show = (ImageView)view.findViewById(R.id.imageView3);
        //layout
        noSongPlaying = (ConstraintLayout)view.findViewById(R.id.noSongContainer);
        noSongPlaying.setVisibility(View.VISIBLE);
        //onclick
        playBtnHide();
        //bottom sheet
        bottomSheet = view.findViewById(R.id.music_bottomsheet);
        //setGone
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        seekBar = (SeekBar)view.findViewById(R.id.music_progressBar);
        //runnable
        setRunnable();
        setSeekBar();

    }
    //click listeners for buttons + handle music player
    public void playBtnHide(){
        play_Hide.setVisibility(View.VISIBLE);
        pause_Hide.setVisibility(View.GONE);
        play_Extend.setVisibility(View.VISIBLE);
        pause_Extend.setVisibility(View.GONE);
        play_Hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_Hide.setVisibility(View.GONE);
                pause_Hide.setVisibility(View.VISIBLE);
                play_Extend.setVisibility(View.GONE);
                pause_Extend.setVisibility(View.VISIBLE);
                //handle music player
                if(mediaPlayer!=null &&!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }

                //add animation for container color when playing music
            }
        });
        pause_Hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_Hide.setVisibility(View.VISIBLE);
                pause_Hide.setVisibility(View.GONE);
                play_Extend.setVisibility(View.VISIBLE);
                pause_Extend.setVisibility(View.GONE);
                //handle music player
                if(mediaPlayer!=null &&mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });
        play_Extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_Extend.setVisibility(View.GONE);
                pause_Extend.setVisibility(View.VISIBLE);
                play_Hide.setVisibility(View.GONE);
                pause_Hide.setVisibility(View.VISIBLE);
                //handle music player
                if(mediaPlayer!=null &&!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });
        pause_Extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_Extend.setVisibility(View.VISIBLE);
                pause_Extend.setVisibility(View.GONE);
                play_Hide.setVisibility(View.VISIBLE);
                pause_Extend.setVisibility(View.GONE);

                //handle music player
                if(mediaPlayer!=null &&mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });
        skip_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                Fragment fragment = activity.getActiveFrag();
                if(fragment instanceof MyMusicActivity) {
                    if(mList!=null && pos+1< mList.size()){
                        pos++;
                        handleMusic(mList,pos);
                    }
                }
            }
        });
        skip_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                Fragment fragment = activity.getActiveFrag();
                if(fragment instanceof MyMusicActivity) {
                    if(mList!=null && pos-1 >=0){
                        pos--;
                        handleMusic(mList,pos);
                    }
                }
            }
        });

    }
    //called from activity, onbackpressed
    public void setBottomSheetState(){
        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else{
            getActivity().finish();
        }
    }
    //music player
    public void streamMusic(final SearchAdapter adapter,VideoItem item){
        this.item = item;
        setText(item.getTitle(),item.getInfo(),"",item.getThumbnailURL());
        //Picasso.with(getContext()).load(item.getThumbnailURL()).fit().transform(new CircleTransform()).into(thumbnail_hide);
        //Picasso.with(getContext()).load(item.getThumbnailURL()).fit().into(thumbnail_show);
        String vidID = item.getId();
        Scraping myscraping = new Scraping("MusicPlayer",getActivity(),getContext(),"https://www.youtube.com/watch?v="+vidID);
        myscraping.createWebView();
    }
    public void streamMusic(String finalUrl){
        urlVid = finalUrl;
        if(mediaPlayer==null){
            mediaPlayer = new MediaPlayer();
        }
        streamingTask = new MyTask();
        streamingTask.execute();
        //set Text
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play_Hide.setVisibility(View.GONE);
                pause_Hide.setVisibility(View.VISIBLE);
                play_Extend.setVisibility(View.GONE);
                pause_Extend.setVisibility(View.VISIBLE);
                songName.setText(item.getTitle());
                String finalText = "";
                int hourInt = mediaPlayer.getDuration() /HOUR;
                int minutesInt = mediaPlayer.getDuration() /MINUTE%60;
                int secondsInt = mediaPlayer.getDuration() / SECOND %60;
                String hour = String.valueOf(hourInt);
                String minutes = String.valueOf(minutesInt);
                String seconds = String.valueOf(secondsInt);
                if(hourInt!=0){
                    if(hourInt<10){
                        hour = "0"+hour;
                    }
                    if(minutesInt<10){
                        minutes = "0"+minutes;
                    }
                    finalText+=hour+":";
                }
                if(secondsInt<10){
                    seconds="0"+seconds;
                }
                finalText+=minutes+":"+seconds;
                length.setText(finalText);
                seekBar.setMax(mediaPlayer.getDuration());
                runnable.run();

            }
        });
    }
    public void handleMusic(List<MusicFolder> mList,int pos){
        this.pos=pos;
        this.mList = mList;
        final MusicFolder folder = mList.get(pos);
        File file = folder.getFile();
        setText(folder.getSongName(),folder.getAuthorName(),folder.getDuration(),file.getAbsolutePath());
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play_Hide.setVisibility(View.GONE);
                pause_Hide.setVisibility(View.VISIBLE);
                play_Extend.setVisibility(View.GONE);
                pause_Extend.setVisibility(View.VISIBLE);
                length.setText(folder.getDuration());
                songName.setText(folder.getSongName());
                authorName.setText(folder.getAuthorName());
                Picasso.with(getContext()).load(folder.getThumbnail()).fit().into(thumbnail_hide);
                Picasso.with(getContext()).load(folder.getThumbnail()).fit().into(thumbnail_show);
                seekBar.setMax(mediaPlayer.getDuration());
                runnable.run();

            }
        });
        try {
                mediaPlayer.setDataSource(getContext(), Uri.fromFile(file));
                mediaPlayer.prepare();
                mediaPlayer.start();
        }
        catch (IOException exception){
            AlertDialogMsg.showMsg(getContext(),"file not found","Selected song was probably deleted.");
        }
    }
    //old
    public void handleProgress(int progress){
        String finalText = "";
        int hourInt = progress /HOUR;
        int minutesInt = progress /MINUTE%60;
        int secondsInt = progress / SECOND %60;
        String hour = String.valueOf(hourInt);
        String minutes = String.valueOf(minutesInt);
        String seconds = String.valueOf(secondsInt);
        if(hourInt!=0){
            if(hourInt<10){
                hour = "0"+hour;
            }
            if(minutesInt<10){
                minutes = "0"+minutes;
            }
            finalText+=hour+":";
        }
        if(secondsInt<10){
            seconds="0"+seconds;
        }
        finalText+=minutes+":"+seconds;
        timeCurrent.setText(finalText);
    }
    public void setText(String name, String author,String duration, String thumbnailUrl){
        noSongPlaying.setVisibility(View.INVISIBLE);
        this.songName.setText(name);
        this.authorName.setText(author);
        this.length.setText(duration);
        Picasso.with(getContext()).load(thumbnailUrl).fit().into(thumbnail_show);
        Picasso.with(getContext()).load(thumbnailUrl).transform(new CircleTransform()).fit().into(thumbnail_hide);
    }
    public void setProgress(){
        if(mediaPlayer!=null ) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handleProgress(mediaPlayer.getCurrentPosition());
        }
    }
    public void setRunnable(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //update progress
                setProgress();
                handler.postDelayed(runnable,1000);
            }
        };
    }
    public void setSeekBar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    handleProgress(progress);
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void destroyMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
        length.setText("0:00");
        timeCurrent.setText("0:00");
        songName.setText("song name");
        authorName.setText("author name");
        play_Extend.setVisibility(View.VISIBLE);
        play_Hide.setVisibility(View.VISIBLE);
        pause_Extend.setVisibility(View.GONE);
        pause_Hide.setVisibility(View.GONE);
    }
    public class MyTask extends AsyncTask<Integer,Integer,Integer>{
        @Override
        public void onPreExecute(){
            //loadingPB.setVisibility(View.VISIBLE);
            //System.out.println("Starting streaming");
            //stop runnable
            handler.removeCallbacks(runnable);
            mediaPlayer.reset();

        }
        @Override
        protected Integer doInBackground(Integer...params) {
            try {
                URL url = new URL(urlVid);
                System.out.println(url.toString());
                HttpURLConnection request = (HttpURLConnection)url.openConnection();
                request.connect();
                if(HttpURLConnection.HTTP_OK == request.getResponseCode()){
                    System.out.println("-------OK----------");
                }
                else{
                    System.out.println("-------BAD----------");
                    return 0;
                }
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("----------------------------- failed connection");
                return 0;
            }
            try {
                mediaPlayer.setDataSource(urlVid) ;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            catch (IOException e){
                e.printStackTrace();
                System.out.println("Music player failed ... ");
                return 0;


            }
            catch (IllegalStateException e){
                e.printStackTrace();
                System.out.println("Illegal State Exception Music player failed ... ");
                //need to show message to user(custom dialog class need to be created .)
            }


            return 100;
        }
        @Override
        public void onPostExecute(Integer result){
          //  loadingPB.setVisibility(View.GONE);
            if(result == 0) {
                System.out.println("----------Connection failed too much times !---------");
                AlertDialogMsg.showMsg(getContext(),"Connection Failed","Connection failed too much times !\nPlease try again later");
            }
            if(result==100){
                if(mediaPlayer.isPlaying()){
                    play_Extend.setVisibility(View.GONE);
                    pause_Extend.setVisibility(View.VISIBLE);
                    play_Hide.setVisibility(View.GONE);
                    pause_Hide.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    //need to delete unneccesary systemoutprint



}

