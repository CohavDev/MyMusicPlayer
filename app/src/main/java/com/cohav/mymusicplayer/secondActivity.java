package com.cohav.mymusicplayer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.cohav.mymusicplayer.MyMusic.editFileTagsFragment;
import com.cohav.mymusicplayer.searchMusic.DwnFragment;
import com.cohav.mymusicplayer.searchMusic.categoryPage;

/**
 * Created by Shaul on 07/12/2017.
 */

public class secondActivity extends AppCompatActivity {

    @Override
    public void onBackPressed(){
        finish();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        //toTdo
        proccessIntent(getIntent());


    }
    private void proccessIntent(Intent intent){
        System.out.println("new intent called.");
        if(intent!=null){
            Bundle bundle = new Bundle();

           if(intent.hasExtra("myFolder")){
               //start fragment of edit-tags.
               bundle.putSerializable("myFolder",intent.getSerializableExtra("myFolder"));
               editFileTagsFragment tagsFragment = new editFileTagsFragment();
               tagsFragment.setArguments(bundle);
               getSupportFragmentManager().beginTransaction().add(R.id.secondActivityLayout,tagsFragment).addToBackStack(editFileTagsFragment.class.getName()).commit();

           }
           else if(intent.hasExtra("categoryName")){
               //start fragment of category page and send category name details.
               bundle.putString("categoryName",intent.getStringExtra("categoryName"));
               bundle.putParcelableArrayList("videoList",intent.getParcelableArrayListExtra("videoList"));
               categoryPage categoryPageFrag = new categoryPage();
               categoryPageFrag.setArguments(bundle);
               getSupportFragmentManager().beginTransaction().add(R.id.secondActivityLayout,categoryPageFrag).addToBackStack(categoryPage.class.getName()).commit();
           }
        }
    }

}
