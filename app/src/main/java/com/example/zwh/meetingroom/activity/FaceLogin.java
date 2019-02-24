package com.example.zwh.meetingroom.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.zwh.meetingroom.R;

/**
 * created at $date$ $time$ by wenhaoz
 */
public class FaceLogin extends AppCompatActivity {
    private TextView text1;
    private TextView text2;
    private TextView tip_text;
    private Button text_people;
    private Uri imageUri;


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
                Intent imageAnalysis=new Intent(FaceLogin.this,ImageAnalysis.class);
                startActivity(imageAnalysis);
            }
        });
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle("");
    }

}
