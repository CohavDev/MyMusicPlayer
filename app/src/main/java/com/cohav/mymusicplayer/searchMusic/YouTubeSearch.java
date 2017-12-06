package com.cohav.mymusicplayer.searchMusic;

import android.content.Context;

import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
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
    private static String pop,rock;
    private static final String KEY = "AIzaSyBVCofD0lxiQzSBdeLscf3DmjY8eAL1p_4";
    private YouTube.Search.List query;

    public YouTubeSearch(Context context) {
        pop = "PLDcnymzs18LWrKzHmzrGH1JzLBqrHi3xQ";
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();

    }
    private List<VideoItem> createListFromSearch(){
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
    public List<VideoItem> search(String keywords){
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
        query.setQ(keywords);
        return createListFromSearch();
    }


    //playlist
    private List<VideoItem> createListFromPlaylist(PlaylistItemListResponse response){

        System.out.println(response);
        List<PlaylistItem> results = response.getItems();
        List<VideoItem> items = new ArrayList<>();
        for(PlaylistItem result:results){
            //video item instead of song
            VideoItem item = new VideoItem();
            item.setTitle(result.getSnippet().getTitle());
            item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
            item.setHighThumbnail(result.getSnippet().getThumbnails().getHigh().getUrl());
            item.setId(result.getId());
            item.setInfo(result.getSnippet().getChannelTitle());
            items.add(item);
        }
            return items;

    }
    public List<VideoItem> searchPlayList(String playList_Id){
        YouTube.PlaylistItems.List query;
        try {
            query = youtube.playlistItems().list("snippet");
            query.setKey(KEY);
            query.setFields(("items(id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/thumbnails/high,snippet/channelTitle)"));
            query.setMaxResults((long)15);
            query.setPlaylistId(playList_Id);
            query.setPart("snippet,contentDetails");
            //search
            PlaylistItemListResponse response = query.execute();
            return createListFromPlaylist(response);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("couldnt initalize playlist search");
            return null;
        }



    }

}
//search activity & categories fragment will call this class.
