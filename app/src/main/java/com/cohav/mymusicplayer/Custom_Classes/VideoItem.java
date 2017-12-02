package com.cohav.mymusicplayer.Custom_Classes;

/**
 * Created by shaulcohav on 07/07/17.
 */

public class VideoItem {
    private String title;
    private String info;
    private String thumbnailURL;
    private String id;
    private String highThumbnail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }
    public void setHighThumbnail(String url){this.highThumbnail = url;}
    public String getHighThumbnail(){return this.highThumbnail;}
}
