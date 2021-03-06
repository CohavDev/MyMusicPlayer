package com.cohav.mymusicplayer.scraping_websites;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.cohav.mymusicplayer.Custom_Classes.AlertDialogMsg;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.searchMusic.DwnFragment;
import com.cohav.mymusicplayer.searchMusic.MusicPlayerView;

/**
 * Created by Shaul on 27/10/2017.
 */

public class Scraping {
    public WebView myWebView;
    private FragmentActivity activity;
    private Context mContext;
    private String dwnUrl = "https://ytmp3.cc/";
    private String vidUrl ="";
    private String js = "";
    private String finalDwnURL = "";
    private String fragmentName;
    private Runnable runnable;
    private Handler handler;
    private int counts;
    public Scraping(String fragmentName, FragmentActivity activity, Context mContext, String vidUrl){
        this.activity=activity;
        this.mContext = mContext;
        this.vidUrl=vidUrl;
        this.fragmentName = fragmentName;
        this.js = "javascript:document.getElementById('input').setAttribute('value','"+vidUrl+"');javascript:document.getElementById('submit').click();";
        MyJavaScriptInterface.staticHref = "";

    }
    public void createWebView(){

        myWebView = new WebView(activity.getApplicationContext());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new MyJavaScriptInterface(this),"HTMLOUT");
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                AlertDialogMsg.showMsg(mContext,"Server failed",description);
            }
            @Override
            public void onPageFinished(final WebView view, String url){
                super.onPageFinished(view,url);
                //injecting values to the input
                System.out.println("Page finished loading");
                if(Build.VERSION.SDK_INT>=19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            counts = 0;
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    counts++;
                                    handler.postDelayed(runnable, 1000);

                                }
                            };
                            handler = new Handler();
                            runnable.run();
                            isReady(view);
                        }
                    });
                }
                else{
                    myWebView.loadUrl(js);
                    isReady(myWebView);
                }
            }
        });

        myWebView.loadUrl(dwnUrl);//check if is loaded at first try



    }
    public ValueCallback myValueCallback = new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
            finalDwnURL = MyJavaScriptInterface.staticHref;
            if(!finalDwnURL.equals("") && finalDwnURL!=null){
                //found href
                System.out.println("-----------"+finalDwnURL);
                //sending url to fragment
                sendToFrag();
            }
            else{
                if(counts>15){
                    handler.removeCallbacks(runnable);
                    failedMsg();
                }
                else {
                    //missing href
                    isReady(myWebView);
                }
            }
        }
    };
    public void isReady(WebView view){
        final String js = "javascript:window.HTMLOUT.getHref(document.getElementById('file').getAttribute('href'));";
        if(Build.VERSION.SDK_INT>=19){
            view.evaluateJavascript(js,myValueCallback);
        }
        else{
            loadJs();
        }

    }
    public void sendToFrag(){
        //killing webview
        myWebView.destroy();
        myWebView = null;
        finalDwnURL = MyJavaScriptInterface.staticHref;
        //sending url to fragment
        FragmentManager manager = activity.getSupportFragmentManager();
        switch (fragmentName){
            case "DwnFragment":
                DwnFragment fragmentDwn = (DwnFragment) manager.findFragmentById(R.id.dwnRelative);
                fragmentDwn.startDwn(finalDwnURL);
                break;
            case "MusicPlayer":
                MusicPlayerView playerFrag = (MusicPlayerView) manager.findFragmentById(R.id.musicPlayerView);
                playerFrag.streamMusic(finalDwnURL);
                break;

        }
    }
    public void loadJs(){
        System.out.println("Load Js called");
        final String js = "javascript:window.HTMLOUT.getHrefLowSdk(document.getElementById('file').getAttribute('href'));";
        myWebView.loadUrl(js);
    }
    public void failedMsg(){
        AlertDialogMsg.showMsg(mContext,"Error - Copyrights problem","Please select a different song / video.");
    }

}