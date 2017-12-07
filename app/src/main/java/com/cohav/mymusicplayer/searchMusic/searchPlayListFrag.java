package com.cohav.mymusicplayer.searchMusic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;

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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fab_parent,new categoryPage()).addToBackStack("categoryPage").commit();


            }
        });

    }
    public void searchPlayList(){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute(){
                MainActivity activity = (MainActivity) getActivity();
                ConnectivityManager connectivityManager = (ConnectivityManager) (activity.getSystemService(Context.CONNECTIVITY_SERVICE));
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || ! networkInfo.isConnected()){
                    Toast.makeText(getContext(),"No Connection",Toast.LENGTH_LONG).show();
                    cancel(true);
                }
            }
            @Override
            protected Object doInBackground(Object[] objects) {
                YouTubeSearch youTubeSearch = new YouTubeSearch(getActivity());
                List<VideoItem> items = youTubeSearch.searchPlayList(YouTubeSearch.pop);
                // proccess the list
                return null;
            }
        };
        asyncTask.execute();
    }
}
