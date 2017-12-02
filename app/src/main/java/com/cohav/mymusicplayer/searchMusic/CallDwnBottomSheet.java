package com.cohav.mymusicplayer.searchMusic;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cohav.mymusicplayer.Custom_Classes.CircleTransform;
import com.cohav.mymusicplayer.Custom_Classes.VideoItem;
import com.cohav.mymusicplayer.R;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;

/**
 * Created by Shaul on 03/11/2017.
 */

public class CallDwnBottomSheet extends BottomSheetDialogFragment {
    private TextView songName;
    private ImageButton playBtn,dwnBtn;
    private ImageView thumbnail;
    private String dwnUrl,songNamevar,vidUrl;
    private VideoItem item;
    private Context mContext;
    private SearchAdapter.SearchViewHolder mHolder;
    private SearchAdapter adapter;
    private Dialog dialog;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        this.dialog = dialog;
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                songName =(TextView) bottomSheetDialog.findViewById(R.id.songname_preview);
                playBtn = (ImageButton)bottomSheetDialog.findViewById(R.id.playPrev_btn);
                dwnBtn = (ImageButton)bottomSheetDialog.findViewById(R.id.dwnPrev_btn);
                thumbnail = (ImageView)bottomSheetDialog.findViewById(R.id.preview_thumbnail);
                //call to method to handle data from the adapter
                setData();
                assignBtns();

            }
        });
        return dialog;
    }
    private void setData(){
        songName.setText(songNamevar);
        Picasso.with(mContext).load(dwnUrl).transform(new CircleTransform()).into(thumbnail);

    }
    public void assignVars(Context mContext, VideoItem item, SearchAdapter.SearchViewHolder holder, SearchAdapter adapter){
        this.mContext = mContext;
        this.dwnUrl = item.getThumbnailURL();
        this.songNamevar = item.getTitle();
        this.vidUrl = item.getId();
        this.mHolder = holder;
        this.adapter = adapter;
        this.item = item;
    }
    public void assignBtns(){
        dwnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call to DwnFragment
                DwnFragment fragment = (DwnFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.dwnRelative);
                fragment.handleDwn(item,mHolder,vidUrl);
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //call to play fragment.
                MusicPlayerView fragment = (MusicPlayerView) getActivity().getSupportFragmentManager().findFragmentById(R.id.musicPlayerView);
                fragment.streamMusic(adapter,item);

            }
        });
    }
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.download_bottomsheet, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
}
