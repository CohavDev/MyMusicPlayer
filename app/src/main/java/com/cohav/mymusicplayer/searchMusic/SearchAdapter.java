package com.cohav.mymusicplayer.searchMusic;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cohav.mymusicplayer.Custom_Classes.CircleTransform;
import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Shaul on 02/11/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<VideoItem> myList;
    private Context mContext;
    private AppCompatActivity activityCompat;

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        public TextView title, count;
        public ImageView thumbnail;
        public RelativeLayout dwnLayout, cardviewPress;
        public SearchViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            dwnLayout = (RelativeLayout)view.findViewById(R.id.dwnRelative);
            cardviewPress = (RelativeLayout)view.findViewById(R.id.cardview_press);
        }
    }

    public SearchAdapter(Activity activity, Context mContext, List<VideoItem>myList){
        this.mContext=mContext;
        this.myList=myList;
        this.activityCompat = (AppCompatActivity)activity;
    }
    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_card, parent, false);

        return new SearchViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final SearchViewHolder holder,int position){
        final VideoItem item = myList.get(position);
        holder.title.setText(item.getTitle());
        holder.count.setText(item.getInfo());//need to change
        //loading cover image
        Picasso.with(mContext).load(item.getThumbnailURL()).into(holder.thumbnail);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show bottom sheet
                callBottomSheet(item,holder);
            }
        };
        holder.cardviewPress.setOnClickListener(clickListener);
        holder.thumbnail.setOnClickListener(clickListener);
    }
    @Override
    public int getItemCount(){
        if(this.myList!=null){
            return myList.size();
        }
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    private void callBottomSheet(VideoItem item,SearchViewHolder holder){
        BottomSheetDialogFragment bottomSheetDialogFragment = new CallDwnBottomSheet();
        ((CallDwnBottomSheet)bottomSheetDialogFragment).assignVars(mContext,item,holder,this);
        bottomSheetDialogFragment.show(activityCompat.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

    }





}
