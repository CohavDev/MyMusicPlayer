package com.cohav.mymusicplayer.searchMusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.cohav.mymusicplayer.R;
import com.cohav.mymusicplayer.secondActivity;


/**
 * Created by Shaul on 06/12/2017.
 */

public class searchPlayListFrag extends Fragment {
    private Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.categories_page,parent,false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        intent = new Intent(getActivity(),secondActivity.class);
        ImageButton btn = (ImageButton)view.findViewById(R.id.pop_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pop
                intent.putExtra("categoryName","pop");
                getActivity().startActivity(intent);
            }
        });
        ImageButton rockBtn = (ImageButton)view.findViewById(R.id.rock_btn);
        rockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rock
                intent.putExtra("categoryName","rock");
                getActivity().startActivity(intent);
            }
        });

    }

}
