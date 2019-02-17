package com.example.zwh.meetingroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.zwh.meetingroom.R;

/**
 * created at $date$ $time$ by wenhaoz
 */
public class FaceLogin extends AppCompatActivity  {
    private TextView text1;
    private TextView text2;
    private TextView tip_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initActionBar();
        setContentView(R.layout.activity_face_login);
        AssetManager assetManager=getAssets();
        Typeface tf = Typeface.createFromAsset(assetManager, "fonts/PingFang Regular.otf");
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text1);
        tip_text = (TextView) findViewById(R.id.tip_text);
        text1.setTypeface(tf);
        text2.setTypeface(tf);
        tip_text.setTypeface(tf);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initActionBar() {
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle("");
    }


}
