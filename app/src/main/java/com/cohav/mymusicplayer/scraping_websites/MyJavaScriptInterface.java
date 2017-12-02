package com.cohav.mymusicplayer.scraping_websites;

import android.os.Handler;
import android.webkit.JavascriptInterface;


/**
* Created by Shaul on 28/10/2017.
*/
public class MyJavaScriptInterface{
    private Scraping sourceClass;
    public static String staticHref;
    public static int count;
    public MyJavaScriptInterface(Scraping sourceClass){
        this.sourceClass = sourceClass;
        count = 0;
    }

    @JavascriptInterface
    public void processHTML(String html){
        System.out.println(html);
   }
    @JavascriptInterface
    public void getHref(String href){
        staticHref = href;
    }
    @JavascriptInterface
    public void getHrefLowSdk(String href){
        System.out.println("Href is = "+href);
        staticHref = href;
        if(count <= 15){
            if(href ==null || href.length()<5){
                //couldnt found href
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        sourceClass.loadJs();
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(runnable,1000);

            }
            else{
                //href found
                sourceClass.sendToFrag();
            }
        }
        else{
            sourceClass.failedMsg();
        }
        count++;
    }
}
