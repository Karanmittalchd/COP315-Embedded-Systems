package com.example.remotevisualassistant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
                webView.stopLoading();;
//                webView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createBitmap(webView.getDrawingCache());
//                webView.setDrawingCacheEnabled(false);

//                Picture p = webView.capturePicture();
////                PictureDrawable pd = new PictureDrawable(p);
//                Bitmap bitmap = Bitmap.createBitmap(p.getWidth()
//                        ,p.getHeight()
//                        , Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                p.draw(canvas);
//                imageView.draw(canvas);

                //to save as an image on device
                Picture picture = webView.capturePicture();
                Bitmap  b = Bitmap.createBitmap( picture.getWidth(),
                        picture.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas( b );

                picture.draw( c );
                FileOutputStream fos = null;
                try {

                    fos = new FileOutputStream( "mnt/sdcard/capture.jpg" );
                    if ( fos != null )
                    {
                        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        Toast.makeText(OCRPiActivity.this,"image saved",Toast.LENGTH_SHORT).show();
                        fos.close();
                        try {
                            File f=new File("mnt/sdcard/", "capture.jpg");
                            Bitmap b_in = BitmapFactory.decodeStream(new FileInputStream(f));
                            imageView.setImageBitmap(b_in);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch( Exception e )
                {
                    Toast.makeText(OCRPiActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                webView.reload();
            }
        });

        b_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vid_url = "http://10.194.105.20:8081";
//                webView.scrollTo(webView.get,webView);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
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
