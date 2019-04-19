package com.example.remotevisualassistant;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OCRPiActivity extends AppCompatActivity {

    private Button b_capture, b_stream, b_ocr;
    private ImageView imageView;
    private WebView webView;
    private TextView tresult;
    boolean playing;
    private String vid_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrpi);

        set_UI_components();

        b_ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    playing=false;
                    webView.stopLoading();;

                    Picture picture = webView.capturePicture();
                    Bitmap  b = Bitmap.createBitmap( picture.getWidth(),
                            picture.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas( b );
                    picture.draw( c );

                    String timeStamp =
                            new SimpleDateFormat("yyyyMMdd_HHmmss",
                                    Locale.getDefault()).format(new Date());
                    String imageFileName = "IMG_" + timeStamp + "_";
                    File storageDir =
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    String imageFilePath="";
                    try {
                        File image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                        imageFilePath = image.getAbsolutePath();
                    } catch (IOException e) {
                        Toast.makeText(OCRPiActivity.this, "Failed to create new img file", Toast.LENGTH_SHORT).show();
                    }

                    if(!imageFileName.equals("")) {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(imageFilePath);
                            if (fos != null) {
                                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                                Toast.makeText(OCRPiActivity.this,"image saved",Toast.LENGTH_SHORT).show();
                                fos.close();
                                try {
                                    File f = new File(imageFilePath);
                                    Bitmap b_in = BitmapFactory.decodeStream(new FileInputStream(f));

                                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                                    if(!textRecognizer.isOperational())
                                    {
                                        Toast.makeText(getApplicationContext(),"Could not get the Text",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Frame frame = new Frame.Builder().setBitmap(b_in).build();
                                        SparseArray<TextBlock> items = textRecognizer.detect(frame);
                                        StringBuilder sb = new StringBuilder();
                                        for(int i=0; i<items.size(); ++i){
                                            TextBlock myItem = items.valueAt(i);
                                            sb.append(myItem.getValue());
                                            sb.append("\n");
                                        }
                                        imageView.setVisibility(View.INVISIBLE);
                                        tresult.setVisibility(View.VISIBLE);
                                        tresult.bringToFront();
//                                        Toast.makeText(OCRPiActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
                                        tresult.setText("OCR Result: "+sb.toString());
                                        f.delete();
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(OCRPiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    webView.reload();
                    playing=true;
                }
                else{
                    build_an_alert("Capture Failed","Must frist start stream to do ocr","okay");
                }
            }
        });

        b_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    playing=false;
                    webView.stopLoading();;

                    Picture picture = webView.capturePicture();
                    Bitmap  b = Bitmap.createBitmap( picture.getWidth(),
                            picture.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas( b );
                    picture.draw( c );

                    String timeStamp =
                            new SimpleDateFormat("yyyyMMdd_HHmmss",
                                    Locale.getDefault()).format(new Date());
                    String imageFileName = "IMG_" + timeStamp + "_";
                    File storageDir =
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    String imageFilePath="";
                    try {
                        File image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );

                        imageFilePath = image.getAbsolutePath();
                    } catch (IOException e) {
                        Toast.makeText(OCRPiActivity.this, "Failed to create new img file", Toast.LENGTH_SHORT).show();
                    }

                    if(!imageFileName.equals("")){
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream( imageFilePath );
                            if ( fos != null )
                            {
                                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                Toast.makeText(OCRPiActivity.this,"image saved",Toast.LENGTH_SHORT).show();
                                fos.close();
                                try {
                                    File f=new File(imageFilePath);
                                    Bitmap b_in = BitmapFactory.decodeStream(new FileInputStream(f));
                                    imageView.setVisibility(View.VISIBLE);
                                    tresult.setVisibility(View.INVISIBLE);
                                    imageView.bringToFront();
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
                    }
                    webView.reload();
                    playing=true;
                }
                else{
                    build_an_alert("Capture Failed","Must frist start stream to capture","okay");
                }

            }
        });

        b_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String vid_url = "http://192.168.225.34:8081";
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl(vid_url);
                playing=true;
            }
        });
    }

    private void set_UI_components(){
        b_capture = (Button)findViewById(R.id.button_capture);
        b_stream = (Button)findViewById(R.id.button_stream);
        imageView = (ImageView)findViewById(R.id.imageViewCapture);
        webView = (WebView)findViewById(R.id.webView2);
        b_ocr = (Button)findViewById(R.id.button_ocr);
        tresult = (TextView)findViewById(R.id.t_ocrresult);

        imageView.setVisibility(View.INVISIBLE);
        tresult.setVisibility(View.INVISIBLE);

        String id = FirebaseAuth.getInstance().getUid();
        DatabaseReference mydbr = FirebaseDatabase.getInstance().getReference("userdetails");
        mydbr.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails ud = dataSnapshot.getValue(UserDetails.class);
                vid_url = "http://"+ud.getDevice_ip();
                Toast.makeText(OCRPiActivity.this, vid_url, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OCRPiActivity.this, "Unable to retrieve device", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void build_an_alert(String t, String m, String b){
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(OCRPiActivity.this);
        builder.setTitle(t);
        builder.setMessage(m);
        builder.setCancelable(false);
        builder.setPositiveButton(
                b,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }
        );
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}
