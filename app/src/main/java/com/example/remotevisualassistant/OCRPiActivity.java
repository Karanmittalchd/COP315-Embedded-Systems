package com.example.remotevisualassistant;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;

public class OCRPiActivity extends AppCompatActivity {

    private Button b_capture;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrpi);

        set_UI_components();
        final String vid_url = "http://10.194.105.20:8081/";

        b_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                imageView.setImageBitmap(getVideoFrameFromVideo(vid_url));
                imageView.setImageURI(Uri.parse(vid_url));
            }
        });
    }

    private void set_UI_components(){
        b_capture = (Button)findViewById(R.id.button_capture);
        imageView = (ImageView)findViewById(R.id.imageViewCapture);
    }

    private Bitmap getVideoFrameFromVideo(String videoPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
//        try
//        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
//            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
//            else
//                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
//        } catch (Exception e) {
//            e.printStackTrace();
////            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
//
//        } finally {
//            if (mediaMetadataRetriever != null) {
//                mediaMetadataRetriever.release();
//            }
//        }
        return bitmap;
    }
}
