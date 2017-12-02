package com.cohav.mymusicplayer.searchMusic;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.Song;
import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaulcohav on 19/07/17.
 */

public class searchViewFragment extends Fragment {
    //values
    private SearchAdapter myAdapter;
    private Paint p = new Paint();
    private RecyclerView mRecycler;
    private Drawable settingsIcon, closeIcon;
    private List<VideoItem> videoItemList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private ProgressBar loadingPB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.search_view_frag_layout,parent,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //view.findViewById(R.id.bottomSheetSpace).set
        mRecycler = (RecyclerView) view.findViewById(R.id.mList);
        //icons
        closeIcon = ContextCompat.getDrawable(getContext(),R.drawable.ic_close_24dp);
        settingsIcon = ContextCompat.getDrawable(getContext(),R.drawable.ic_settings_24dp);
        loadingPB = (ProgressBar)view.findViewById(R.id.loadingProgressBar);
        loadingPB.setVisibility(View.GONE);
        //recyclerview
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
    }
    //actions
    public void setRecyclerView(){
       // myAdapter = new MyAdapter(loadingPB,this.videoItemList,getContext(),(MainActivity)getActivity());
        myAdapter = new SearchAdapter(getActivity(),getContext(), videoItemList);
        mRecycler.setAdapter(myAdapter);
        //data
        //setTouchHelper();
    }
    /**public void setTouchHelper(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
                return false;
            }
            @Override
            public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction){
                if(direction == ItemTouchHelper.LEFT ){
                    myAdapter.changeState((MyAdapter.MyViewHolder)viewHolder);
                }
                else{
                    myAdapter.removeSelected((MyAdapter.MyViewHolder)viewHolder);
                }

            }
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (myAdapter.isItemSelected((MyAdapter.MyViewHolder) viewHolder)) {
                    return ItemTouchHelper.RIGHT;
                }
                return ItemTouchHelper.LEFT;
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {


                    if (!myAdapter.isItemSelected((MyAdapter.MyViewHolder) viewHolder)) {
                        View itemView = viewHolder.itemView;
                        p.setColor(Color.DKGRAY);
                        RectF background = new RectF((float) itemView.getRight() + dX,
                                (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        settingsIcon.setBounds(itemView.getRight()-2*settingsIcon.getIntrinsicWidth(),itemView.getTop()+settingsIcon.getIntrinsicHeight(),itemView.getRight()-settingsIcon.getIntrinsicWidth(),itemView.getBottom()-settingsIcon.getIntrinsicHeight());
                        settingsIcon.draw(c);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                    } else if (myAdapter.isItemSelected((MyAdapter.MyViewHolder) viewHolder)) {
                        View itemView = viewHolder.itemView;
                        p.setColor(Color.DKGRAY);
                        RectF background = new RectF((float) itemView.getLeft() + dX,
                                (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        closeIcon.setBounds(itemView.getLeft()+closeIcon.getIntrinsicWidth(),itemView.getTop()+closeIcon.getIntrinsicHeight(),itemView.getLeft()+2*closeIcon.getIntrinsicWidth(),itemView.getBottom()-closeIcon.getIntrinsicHeight());
                        closeIcon.draw(c);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                    //clean?
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecycler);

    }
     */
    //youtube search methods
    public void searchOnYoutube(final String keywords){
        AsyncTask asyncTask = new AsyncTask<Integer, Integer,Integer>() {
            @Override
            protected void onPreExecute(){
                MainActivity activity = (MainActivity) getActivity();
                ConnectivityManager connectivityManager = (ConnectivityManager) (activity.getSystemService(Context.CONNECTIVITY_SERVICE));
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || ! networkInfo.isConnected()){
                    Toast.makeText(getContext(),"No Connection",Toast.LENGTH_LONG).show();
                    cancel(true);
                }
            }
            @Override
            protected Integer doInBackground(Integer... params) {
                YouTubeSearch myYTB = new YouTubeSearch(getContext());
                videoItemList = myYTB.search(keywords);
                return 1;
            }
            @Override
            protected void onPostExecute(Integer result){
                System.out.println("post executed .");
                updateVideosFound();
            }
            //call asyncTask .
        }
                .execute();
    }
    private void updateVideosFound(){
        if(videoItemList==null){
            System.out.println("no results found .");
            //Toast.makeText(MainActivity.this,"No Results Found",Toast.LENGTH_LONG).show();
        }
        else{
            System.out.println("Updating video ");
            setRecyclerView();
            DwnFragment fragment2 = new DwnFragment();
            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.dwnRelative,fragment2);
            fragmentTransaction.commit();
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
