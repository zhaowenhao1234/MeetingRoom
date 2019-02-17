package com.example.zwh.meetingroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.carrier.CarrierService;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.util.ImageAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * created at $date$ $time$ by wenhaoz
 */
public class FaceLogin extends AppCompatActivity {
    private TextView text1;
    private TextView text2;
    private TextView tip_text;
    private Button text_people;
    private Uri imageUrl;
    private int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initActionBar();
        setContentView(R.layout.activity_face_login);
        AssetManager assetManager = getAssets();
        Typeface tf = Typeface.createFromAsset(assetManager, "fonts/PingFang Regular.otf");
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text1);
        tip_text = (TextView) findViewById(R.id.tip_text);
        text_people = (Button) findViewById(R.id.text_people);

        text_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        text1.setTypeface(tf);
        text2.setTypeface(tf);
        tip_text.setTypeface(tf);
    }

    /***
     *完成相机启动的一系列工作
     *@return void
     *@author wenhaoz
     *created at 2019/2/17 21:48
     */
    private void startCamera() {
        File file = new File(getExternalCacheDir(), "login.jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUrl = FileProvider.getUriForFile(this, "com.example.zwh.meetingroom.fileprovider", file);
        } else {
            imageUrl = Uri.fromFile(file);
        }
        Intent openCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl);
        startActivityForResult(openCamera, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                Log.d("FaceLogin", "onActivityResult: ");
                faceDetect();
                break;
        }
    }

    private void faceDetect() {
        ImageAnalysis imageAnalysis = new ImageAnalysis(this);
        imageAnalysis.initEngine();
        imageAnalysis.processImage(getExternalCacheDir() + "/login.jpg");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle("");
    }

}
