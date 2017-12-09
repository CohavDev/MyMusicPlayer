package com.cohav.mymusicplayer.searchMusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.secondActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaul on 07/12/2017.
 */

public class categoryPage extends Fragment {
    private String categoryName;
    private View view;
    private ArrayList<VideoItem> myList;
    private RecyclerView mRecycler;
    private ProgressBar progressBar;
    private TextView genereTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.category_list,parent,false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view=view;
        this.progressBar = (ProgressBar)view.findViewById(R.id.progressBar_categoryPage);
        this.mRecycler = (RecyclerView)view.findViewById(R.id.mList);
        this.genereTitle = (TextView)view.findViewById(R.id.genere_title);
        //set text
        this.categoryName = getArguments().getString("categoryName");
        String text = (this.categoryName).toUpperCase() + " MUSIC";
        genereTitle.setText(text);
        initCollapsingToolbar();
        SearchPlayList async = new SearchPlayList(getActivity(),this);
        async.execute();
    }
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
    private void setAdapter(){
        SearchAdapter adapter = new SearchAdapter(getActivity(),getContext(),myList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(new categoryPage.GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(adapter);
        updateVideos();
    }
    private void updateVideos(){
        DwnFragment fragment2 = new DwnFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment2,"dwnRelative");
        fragmentTransaction.commit();
    }


    //async task class
    private static class SearchPlayList extends AsyncTask<Integer,Integer,List<VideoItem>> {
        private WeakReference<secondActivity> activityWeakReference;
        private WeakReference<categoryPage> fragmentWeakReference;
        private SearchPlayList(Activity activity,Fragment fragment){
            this.activityWeakReference = new WeakReference<>((secondActivity) activity);
            this.fragmentWeakReference = new WeakReference<>((categoryPage)fragment);
        }
        @Override
        protected void onPreExecute(){
            Activity activity = activityWeakReference.get();
            categoryPage fragment = fragmentWeakReference.get();
            if(activity==null||fragment==null){
                return;
            }
            fragment.progressBar.setVisibility(View.VISIBLE);
            ConnectivityManager connectivityManager = (ConnectivityManager) (activity.getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo == null || ! networkInfo.isConnected()){
                Toast.makeText(activity,"No Connection",Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected List<VideoItem> doInBackground(Integer... params) {
            Activity activity = activityWeakReference.get();
            YouTubeSearch youTubeSearch = new YouTubeSearch(activity);
            List<VideoItem> items = youTubeSearch.searchPlayList(fragmentWeakReference.get().categoryName);
            // proccess the list
            return items;
        }
        @Override
        protected void onPostExecute(List<VideoItem> result){
            System.out.println("asyncTask searching playlsit completed.");
            fragmentWeakReference.get().progressBar.setVisibility(View.GONE);
            if(result != null){
                categoryPage fragment = fragmentWeakReference.get();
                fragment.myList = (ArrayList<VideoItem>) result;
                fragment.setAdapter();
            }

        }


    }
    //item decoration
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column
            System.out.println(position);
            if (includeEdge) {
                outRect.left = spacing - column * (spacing / spanCount); // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * (spacing / spanCount); // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * (spacing / spanCount); // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * (spacing / spanCount); // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
