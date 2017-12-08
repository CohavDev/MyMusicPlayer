package com.cohav.mymusicplayer.Custom_Classes;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by shaulcohav on 07/07/17.
 */

public class VideoItem implements Parcelable {
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
    public VideoItem(){

    }
    protected VideoItem(Parcel in) {
        title = in.readString();
        info = in.readString();
        thumbnailURL = in.readString();
        id = in.readString();
        highThumbnail = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(info);
        dest.writeString(thumbnailURL);
        dest.writeString(id);
        dest.writeString(highThumbnail);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VideoItem> CREATOR = new Parcelable.Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };
}
