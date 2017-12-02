package com.cohav.mymusicplayer;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.ViewPagerAdapter;
import com.cohav.mymusicplayer.MyMusic.MyMusicActivity;
import com.cohav.mymusicplayer.searchMusic.MusicPlayerView;
import com.cohav.mymusicplayer.searchMusic.searchViewFragment;


public class MainActivity extends AppCompatActivity{
    private Toolbar myToolBar;
    private SearchView searchView;
    private MenuItem item;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private  boolean isGranted;

    public void destoryMP(){
        //destory mediaPlayer
        MusicPlayerView fragment = (MusicPlayerView)getSupportFragmentManager().findFragmentById(R.id.musicPlayerView);
        if(fragment !=null) {
            fragment.destroyMediaPlayer();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        destoryMP();
    }
    @Override
    public void onPause(){
        super.onPause();
        destoryMP();
    }
    @Override
    public void onResume(){
        super.onResume();
        System.out.println("-----------"+isGranted);
        if(isGranted){
            setToolBar();
            isGranted = false;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //searchable configurations
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        item = menu.findItem(R.id.search);
        isSearchViewFocus();
        return true;
    }
    @Override
    public void onBackPressed(){
        MusicPlayerView fragment = (MusicPlayerView)getSupportFragmentManager().findFragmentById(R.id.musicPlayerView);
        //set bottom sheet state according to its current state.
        fragment.setBottomSheetState();
    }
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab);
        //check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isGranted = false;
            checkPermissions();
        }
        else{
            setToolBar();
        }

    }
    private void setToolBar(){
        //bottom sheet
        onFragmentActivated();

        //toolbar
        myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolBar.setTitle("Rabbit-Player");
        this.setSupportActionBar(myToolBar);
        //tabs
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void checkPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
        {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else{
            setToolBar();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length==grantResults.length) {
                    for (int i = 0; i < permissions.length; i++){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            isPerpermissionForAllGranted=true;
                        }else{
                            isPerpermissionForAllGranted=false;
                        }
                    }
                }
                else {
                    isPerpermissionForAllGranted=true;
                }
                if(!isPerpermissionForAllGranted){
                    Toast.makeText(this,"Permissions denied",Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    //setToolBar();
                    isGranted = true;
                }
                break;
        }
    }
    protected void onNewIntent(Intent intent){
        handleSearchIntent(intent);
    }
    public void handleSearchIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchViewFragment fragment = (searchViewFragment) adapter.getItem(1);
            if(fragment.getUserVisibleHint()) {
                fragment.searchOnYoutube(query);
                myToolBar.setTitle("Search: "+query.toUpperCase());
                item.collapseActionView();
                searchView.clearFocus();
            }else{
                //search in "my music"
            }

        }
    }
    public void onFragmentActivated() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        MusicPlayerView fragment1 = new MusicPlayerView();
        fragmentTransaction.add(R.id.musicPlayerView, fragment1);
        fragmentTransaction.commit();
    }
    //youtube search methods
    private void isSearchViewFocus(){
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    item.collapseActionView();
                }
            }
        });
    }
    //tabs
    public void setupViewPager(ViewPager viewPager){
        //call to adapter
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyMusicActivity(),"My Music");
        adapter.addFragment(new searchViewFragment(),"Search Music");
        viewPager.setAdapter(adapter);
    }
    public Fragment getActiveFrag(){
        searchViewFragment fragment2 = (searchViewFragment) adapter.getItem(1);
        MyMusicActivity fragment1 = (MyMusicActivity) adapter.getItem(0);
        if(fragment1.getUserVisibleHint()){
            return fragment1;
        }
        if(fragment2.getUserVisibleHint()) {
            return fragment2;
        }
        return null;
    }
    public MyMusicActivity getMyMusicFrag(){
        return (MyMusicActivity) adapter.getItem(0);
    }

}
