package com.cohav.mymusicplayer;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.cohav.mymusicplayer.Custom_Classes.ViewPagerAdapter;
import com.cohav.mymusicplayer.MyMusic.MyMusicActivity;
import com.cohav.mymusicplayer.searchMusic.searchPlayListFrag;
import com.cohav.mymusicplayer.searchMusic.searchViewFragment;

/**
 * Created by Shaul on 07/12/2017.
 */

public class MainFragment extends Fragment {
    private Toolbar myToolBar;
    private SearchView searchView;
    private MenuItem item;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private  boolean isGranted;
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
        adapter.addFragment(new searchPlayListFrag(),"Search playList");
        viewPager.setAdapter(adapter);
    }
}
