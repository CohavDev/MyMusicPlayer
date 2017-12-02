package com.cohav.mymusicplayer.searchMusic;

import android.content.Context;

import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaulcohav on 07/07/17.
 */

public class YouTubeSearch {
    private static YouTube youtube;
    private static final String KEY = "AIzaSyBVCofD0lxiQzSBdeLscf3DmjY8eAL1p_4";
    private YouTube.Search.List query;

    public YouTubeSearch(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();
        try{

            query = youtube.search().list("snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setMaxResults((long)15);
            query.setFields("items(id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/thumbnails/high,snippet/channelTitle)");

        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not initialize: "+e);
        }
    }
    public List<VideoItem> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();
            System.out.println(response);
            List<SearchResult> results = response.getItems();
            List<VideoItem> items = new ArrayList<>();
            for(SearchResult result:results){
                //video item instead of song
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setHighThumbnail(result.getSnippet().getThumbnails().getHigh().getUrl());
                item.setId(result.getId().getVideoId());
                item.setInfo(result.getSnippet().getChannelTitle());
                items.add(item);
            }
            return items;
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not search: "+ e);
            return null;
        }
    }

}
//search activity will call this class.
