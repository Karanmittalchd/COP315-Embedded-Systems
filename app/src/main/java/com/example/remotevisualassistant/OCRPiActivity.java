package com.example.remotevisualassistant;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;

public class OCRPiActivity extends AppCompatActivity {

    private Button b_capture, b_stream;
    private ImageView imageView;
    private WebView webView;

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

        b_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vid_url = "http://10.194.105.20:8081";
//                webView.scrollTo(webView.get,webView);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.loadUrl(vid_url);
            }
        });
    }

    private void set_UI_components(){
        b_capture = (Button)findViewById(R.id.button_capture);
        b_stream = (Button)findViewById(R.id.button_stream);
        imageView = (ImageView)findViewById(R.id.imageViewCapture);
        webView = (WebView)findViewById(R.id.webView2);
    }
}
