package com.cohav.mymusicplayer.MyMusic;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.MusicFolder;
import com.cohav.mymusicplayer.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shaulcohav on 18/07/17.
 */

public class MyMusicActivity extends Fragment {
    private List<MusicFolder>myList;
    private RecyclerView mRecyclerView;
    private myMusicAdapter myMusicAdapter;
    private Date lastModifiedDate;
    @Override
    public void onResume(){
        super.onResume();
        if(lastmodified()) {
            updateDwn();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.my_music_layout,parent,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        scanMusic();
        initialzieRecycler(view);
    }
    public void scanMusic(){
        boolean createdVar = true;
        myList = new ArrayList<>();
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if(!musicDirectory.exists()){
            createdVar =  musicDirectory.mkdir();
           if(!createdVar){
               Toast.makeText(getContext(),"cannot find music directory",Toast.LENGTH_LONG).show();
           }
        }
        if(createdVar) {
            this.lastModifiedDate = new Date(musicDirectory.lastModified());
            MusicFolder.setContextMusicFolder(getContext());
            scanFilesRecursively(musicDirectory);
            System.out.println("updated library");
        }
        else{
            getActivity().finish();
        }
    }
    public void addFile(File file){
        myList.add(new MusicFolder(file));
    }
    public void scanFilesRecursively(File root){
        File[] list = root.listFiles();
        for (File f:list){
            if(f.isDirectory()){
                scanFilesRecursively(f);
            }
            else{
                String fileName = f.getName();
                String fileNameArray[] = fileName.split("\\.");
                String extension = fileNameArray[fileNameArray.length-1];
                if(extension.equals("mp3")){
                    addFile(f);
                }

            }
        }
    }
    public void initialzieRecycler(View view){
        mRecyclerView = (RecyclerView)view.findViewById(R.id.mList);
        myMusicAdapter = new myMusicAdapter(myList,getContext(),MyMusicActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myMusicAdapter);
    }
    public void updateDwn(){
        scanMusic();
        myMusicAdapter.updateList(this.myList);
    }
    public boolean lastmodified(){
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if(musicDirectory.exists()){
            Date date = new Date(musicDirectory.lastModified());
            if(date.after(this.lastModifiedDate)){
                this.lastModifiedDate = date;
                return true;
            }
        }
        return false;
    }
}
