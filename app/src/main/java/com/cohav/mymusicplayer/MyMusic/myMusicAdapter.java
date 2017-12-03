package com.cohav.mymusicplayer.MyMusic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.CircleTransform;
import com.cohav.mymusicplayer.Custom_Classes.MusicFolder;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.searchMusic.MusicPlayerView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by shaulcohav on 18/07/17.
 */

public class myMusicAdapter extends RecyclerView.Adapter<myMusicAdapter.MyMusicHolder> {
    private List<MusicFolder>myList;
    private Context context;
    private List<MyMusicHolder> mHodler;
    private MyMusicActivity activity;
    public static int count;
    public class MyMusicHolder extends RecyclerView.ViewHolder{
        public TextView songName,authorName,duration;
        public ImageView thumbnail;
        public ConstraintLayout myMusicRowLayout,colorRow;
        //continue from here............
        public int index;
        public MyMusicHolder(View view){
            super(view);
            songName =(TextView) view.findViewById(R.id.mySongName);
            authorName = (TextView)view.findViewById(R.id.myAuthorName);
            duration=(TextView)view.findViewById(R.id.myDuration);
            myMusicRowLayout = (ConstraintLayout)view.findViewById(R.id.clickableRowMyMusic);
            colorRow = (ConstraintLayout)view.findViewById(R.id.rowwhole);
            thumbnail = (ImageView)view.findViewById(R.id.thumbnail_layout);
            mHodler.add(this);
            myMusicAdapter.count++;
            this.index = myMusicAdapter.count;
            if(index % 2 == 0){
                colorRow.setBackgroundColor(Color.parseColor("#66786060"));
            }
            else{
                colorRow.setBackgroundColor(Color.parseColor("#66000000"));
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount(){
        if(this.myList!=null){
            return myList.size();
        }
        return 0;
    }
    public myMusicAdapter(List<MusicFolder> myList, Context context ,MyMusicActivity activity){
        this.myList = myList;
        this.context=context;
        this.activity=activity;
        this.mHodler = new ArrayList<>();
        myMusicAdapter.count = 0;
    }
    @Override
    public MyMusicHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mymusic_row,parent,false);
        return new MyMusicHolder(view);
    }
    @Override
    public void onBindViewHolder(final MyMusicHolder holder, final int position){
        holder.songName.setText(myList.get(position).getSongName());
        holder.authorName.setText(myList.get(position).getAuthorName());
        holder.duration.setText(myList.get(position).getDuration());
        setTouchForItem(holder,position);
        File fileThumb = myList.get(position).getThumbnail();
        if(fileThumb!=null){
            Picasso.with(context).load(fileThumb).transform(new CircleTransform()).fit().into(holder.thumbnail);
        }
        setMediaPlayer(holder,position);
    }
    public void setMediaPlayer(final MyMusicHolder holder, final int pos){
        holder.myMusicRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) activity.getActivity();
                MusicPlayerView fragment = (MusicPlayerView) mainActivity.getSupportFragmentManager().findFragmentById(R.id.musicPlayerView);
                fragment.handleMusic(myList,pos);
            }
        });
    }
    public void updateList(List<MusicFolder>mlist){
        this.myList = mlist;
        this.notifyDataSetChanged();
    }
    public void setTouchForItem(final MyMusicHolder holder,final int pos){
        holder.myMusicRowLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.inflate(R.menu.overflow_mymusic);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete_song:

                                File file = myList.get(pos).getFile();
                                if(file!=null){
                                    //delete song
                                    file.delete();
                                    //activity.getActivity().getContentResolver().delete(Uri.fromFile(file),null,null);
                                    //tell gallery
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    intent.setData(Uri.fromFile(file));
                                    activity.getActivity().sendBroadcast(intent);
                                    Toast.makeText(activity.getContext(),"1 song was deleted",Toast.LENGTH_LONG).show();
                                }
                                file = myList.get(pos).getThumbnail();
                                if(file !=null){
                                    //delete thumbnail
                                    file.delete();
                                }
                                //scan Media
                                MainActivity mainActivity = (MainActivity) activity.getActivity();
                                MyMusicActivity fragment = mainActivity.getMyMusicFrag();
                                fragment.updateDwn();

                                break;
                            case R.id.edit_tags:
                                //edit id3 tags
                                //start fragment
                                Intent intent = new Intent(activity.getContext(),editFileTagsFragment.class);
                                intent.putExtra("myFolder",myList.get(pos));
                                //start activity
                                activity.getActivity().startActivity(intent);

                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }



}
