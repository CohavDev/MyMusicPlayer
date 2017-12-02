package com.cohav.mymusicplayer.Custom_Classes;

/**
 * Created by shaulcohav on 27/06/17.
 */

public class Song {
    private String name;
    private String author;
    private String date;
    public Song(String name,String author,String date){
        this.name=name;
        this.author=author;
        this.date=date;
    }
    public void SetName(String name){
        this.name=name;
    }
    public void SetAuthor(String author){
        this.author=author;
    }
    public void SetDate(String date){
        this.date=date;
    }
    public String GetName(){
        return this.name;
    }
    public String GetDate(){
        return this.date;
    }
    public String GetAuthor(){
        return this.author;
    }
}
