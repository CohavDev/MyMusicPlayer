package com.cohav.mymusicplayer.searchMusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.secondActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaul on 06/12/2017.
 */

public class searchPlayListFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.categories_page,parent,false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button btn = (Button)view.findViewById(R.id.pop_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pop
                SearchPlayList playList = new SearchPlayList(getActivity());
                playList.execute();
            }
        });

    }
    private static class SearchPlayList extends AsyncTask<Integer,Integer,List<VideoItem>>{
        private WeakReference<MainActivity> activityWeakReference;
        private SearchPlayList(Activity activity){
            this.activityWeakReference = new WeakReference<>((MainActivity) activity);
        }
            @Override
            protected void onPreExecute(){
                Activity activity = activityWeakReference.get();
                if(activity==null){
                    return;
                }
                ConnectivityManager connectivityManager = (ConnectivityManager) (activity.getSystemService(Context.CONNECTIVITY_SERVICE));
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || ! networkInfo.isConnected()){
                    Toast.makeText(activity,"No Connection",Toast.LENGTH_LONG).show();
                    cancel(true);
                }
            }
            @Override
            protected List<VideoItem> doInBackground(Integer... params) {
                Activity activity = activityWeakReference.get();
                YouTubeSearch youTubeSearch = new YouTubeSearch(activity);
                List<VideoItem> items = youTubeSearch.searchPlayList(YouTubeSearch.pop);
                // proccess the list
                return items;
            }
            @Override
            protected void onPostExecute(List<VideoItem> result){
                System.out.println("asyncTask searching playlsit completed.");
                if(result != null){
                    Activity activity = activityWeakReference.get();
                    Intent intent = new Intent(activity,secondActivity.class);
                    intent.putExtra("categoryName","pop");
                    intent.putParcelableArrayListExtra("videoList",(ArrayList<? extends Parcelable>) result);
                    activity.startActivity(intent);
                }

            }


    }
}
