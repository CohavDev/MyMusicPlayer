package com.cohav.mymusicplayer.MyMusic;

import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.MusicFolder;
import com.cohav.mymusicplayer.MainActivity;
import com.cohav.mymusicplayer.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by Shaul on 22/11/2017.
 */

public class editFileTagsFragment extends AppCompatActivity {
    private MusicFolder myFolder;
    private TextInputEditText songNameEdit,authorNameEdit;
    private ImageView artImage;
    private Button saveBtn,cancelBtn;
    private ImageButton selectArt;
    private File chosenArt;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tags);
        //start code
        this.myFolder = (MusicFolder) getIntent().getSerializableExtra("myFolder");
        System.out.println("My Folder     "+myFolder);
        if(myFolder == null){
            Toast.makeText(this,"File was not found",Toast.LENGTH_LONG).show();
            finish();

        }
        songNameEdit = (TextInputEditText) findViewById(R.id.editText_Title);
        authorNameEdit = (TextInputEditText) findViewById(R.id.editText_Author);
        artImage = (ImageView) findViewById(R.id.art_tag);
        selectArt = (ImageButton)findViewById(R.id.selectArt);
        cancelBtn = (Button)findViewById(R.id.cancel_Tags);
        saveBtn = (Button)findViewById(R.id.save_tags);
        songNameEdit.setText(myFolder.getSongName());
        authorNameEdit.setText(myFolder.getAuthorName());
        if(myFolder.getThumbnail()!=null){
            Picasso.with(this).load(myFolder.getThumbnail()).fit().into(artImage);
            chosenArt = myFolder.getThumbnail();
        }
        else{
            Picasso.with(this).load(R.drawable.music_icon).fit().into(artImage);
            chosenArt = null;
        }



        selectArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Artwork"),2);
            }//להמשיך מכאן
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close activity
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setID3Tag(songNameEdit.getText().toString(),authorNameEdit.getText().toString(),chosenArt);
                Toast.makeText(editFileTagsFragment.this,"Data was saved successfully",Toast.LENGTH_LONG).show();
                //close activity
                finish();
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2){
            if(data!=null) {
                Uri selectedImg = data.getData();
                Picasso.with(editFileTagsFragment.this).load(selectedImg).fit().into(artImage);
                //need to check if null
                getPath(selectedImg);
            }
        }
    }
    public void setID3Tag(String title, String author, File fileArt){
        try {
            MP3File audio = new MP3File(myFolder.getFile());
            Tag tag = audio.getTagAndConvertOrCreateAndSetDefault();
            tag.setField(FieldKey.TITLE,title);
            tag.setField(FieldKey.ARTIST,author);
            tag.setField(FieldKey.ALBUM,title);
            if(fileArt!=null) {
                tag.deleteArtworkField();
                Artwork artwork = ArtworkFactory.createArtworkFromFile(fileArt);
                tag.setField(artwork);
            }
            audio.commit();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        //tell gallery
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(myFolder.getFile()));
        sendBroadcast(intent);
        //need to scan gallery
    }
    public void getPath(Uri uri) {
        if(this.chosenArt == null){
            //creates file
            File cachFolder = new File(getCacheDir(),File.separator+"cachThumbnails");
            if(!cachFolder.exists()){
                cachFolder.mkdirs();
            }
            File tempFile = new File(cachFolder.getAbsolutePath(),this.myFolder.getFile().getName());
            try {
                tempFile.createNewFile();
                this.chosenArt = tempFile;
                myFolder.setThumbnailFile(tempFile);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];

        try {
            InputStream in = getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(this.chosenArt);
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
            in.close();
            out.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
