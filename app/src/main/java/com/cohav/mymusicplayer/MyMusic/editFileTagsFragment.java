package com.cohav.mymusicplayer.MyMusic;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cohav.mymusicplayer.Custom_Classes.MusicFolder;
import com.cohav.mymusicplayer.R;
import com.squareup.picasso.Picasso;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
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

public class editFileTagsFragment extends Fragment {
    private MusicFolder myFolder;
    private TextInputEditText songNameEdit,authorNameEdit;
    private ImageView artImage;
    private TextView title;
    private Button saveBtn,cancelBtn;
    private ImageButton selectArt;
    private File chosenArt;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.edit_tags, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //start code
        this.myFolder = (MusicFolder) getArguments().get("myFolder");
        System.out.println("My Folder     "+myFolder);
        if(myFolder == null){
            Toast.makeText(getActivity(),"File was not found",Toast.LENGTH_LONG).show();
            getActivity().finish();

        }
        songNameEdit = (TextInputEditText) view.findViewById(R.id.editText_Title);
        authorNameEdit = (TextInputEditText) view.findViewById(R.id.editText_Author);
        title = (TextView)view.findViewById(R.id.editTagTitlePage);
        artImage = (ImageView) view.findViewById(R.id.art_tag);
        selectArt = (ImageButton)view.findViewById(R.id.selectArt);
        cancelBtn = (Button)view.findViewById(R.id.cancel_Tags);
        saveBtn = (Button)view.findViewById(R.id.save_tags);
        title.setText(myFolder.getFile().getName());
        songNameEdit.setText(myFolder.getSongName());
        authorNameEdit.setText(myFolder.getAuthorName());
        if(myFolder.getThumbnail()!=null){
            Picasso.with(getActivity()).load(myFolder.getThumbnail()).fit().into(artImage);
            chosenArt = myFolder.getThumbnail();
        }
        else{
            Picasso.with(getActivity()).load(R.drawable.music_icon).fit().into(artImage);
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
                getActivity().finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setID3Tag(songNameEdit.getText().toString(),authorNameEdit.getText().toString(),chosenArt);
                Toast.makeText(getActivity(),"Data was saved successfully",Toast.LENGTH_LONG).show();
                //close activity
                getActivity().finish();
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2){
            if(data!=null) {
                Uri selectedImg = data.getData();
                Picasso.with(getActivity()).load(selectedImg).fit().into(artImage);
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
        getActivity().sendBroadcast(intent);
        //need to scan gallery
    }
    public void getPath(Uri uri) {
        if(this.chosenArt == null){
            //creates file
            File cachFolder = new File(getActivity().getCacheDir(),File.separator+"cachThumbnails");
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
            InputStream in = getActivity().getContentResolver().openInputStream(uri);
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
