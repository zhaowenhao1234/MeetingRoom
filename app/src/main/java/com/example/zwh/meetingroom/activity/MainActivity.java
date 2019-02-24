package com.example.zwh.meetingroom.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.base.BaseFragment;
import com.example.zwh.meetingroom.fragment.MeetingFragment;
import com.example.zwh.meetingroom.fragment.MeetingRoomFragment;
import com.example.zwh.meetingroom.fragment.MyFragment;

import java.text.ParsePosition;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private String TAG =MainActivity.class.getSimpleName();
    private RadioGroup rg_main;
    private RadioButton meeting_room;
    private ArrayList<Fragment> fragmentList;
    private FragmentManager fm;
    private int position;
    private FragmentTransaction transaction;
    private BaseFragment mContent;
    private RadioButton meeting;
    private RadioButton my;
    private FaceEngine faceEngine=new FaceEngine();
    String appId = "HvBCSXKhQD4FPhpDBUAV3uW5MnP2i1n8SFf34Y1UTV6h";
    String sdk_Key = "BxfiPa1e5BTUqjsYc9JF59H4z7DTqq5pvWULUXc6V32";
    private int activeCode=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getSupportActionBar().hide();
        }

        requestPermission();
        setContentView(R.layout.activity_main);
        activeEngine();
        //初始化view
        initView();
        //初始化fragment
        initFragment();
    }
    /***
     *激活FaceEngine引擎
     *@return void
     *@author wenhaoz
     *created at 2019/2/17 23:41
     */
    public void activeEngine() {
        activeCode = faceEngine.active(MainActivity.this, appId, sdk_Key);
        if (activeCode == ErrorInfo.MOK) {
            Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "激活失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},3);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager
                .PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"已获取拍照权限",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"获取拍照权限失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"已获取存储权限",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"获取存储权限失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"已获取读取手机状态权限",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"获取读取手机状态权限失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"已获取联网权限",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"获取联网权限失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void initFragment() {
        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(new MeetingRoomFragment());
        fragmentList.add(new MeetingFragment());
        fragmentList.add(new MyFragment());
        fm=getSupportFragmentManager();
        transaction=fm.beginTransaction();
        transaction.add(R.id.fl_main,fragmentList.get(0));
        transaction.commit();
    }

    public void initView() {
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        meeting_room = (RadioButton) findViewById(R.id.meeting_room);
        meeting = (RadioButton) findViewById(R.id.meeting);
        my = (RadioButton) findViewById(R.id.my);
        //定义底部标签图片大小和位置
        Drawable drawable_meetingroom = getResources().getDrawable(R.drawable.meetingroom_selector);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_meetingroom.setBounds(0, 0, 70, 70);
        //设置图片在文字的哪个方向
        meeting_room.setCompoundDrawables(null, drawable_meetingroom, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_meeting = getResources().getDrawable(R.drawable.meeting_selector);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_meeting.setBounds(0, 0, 70, 70);
        //设置图片在文字的哪个方向
        meeting.setCompoundDrawables(null, drawable_meeting, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_my = getResources().getDrawable(R.drawable.my_selector);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_my.setBounds(0, 0, 70, 70);
        //设置图片在文字的哪个方向
        my.setCompoundDrawables(null, drawable_my, null, null);

        rg_main.check(R.id.meeting_room);
        rg_main.setOnCheckedChangeListener(this);

    }


    @Override
    /**
    *实现RadioGroup监听切换fragment
    *@param onCheckedChanged
    *@return void
    *@author wenhaoz
    *created at 2019/2/16 22:16
    */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("123456", "onCheckedChanged: "+checkedId);
        //获得目前的fragment
        mContent= (BaseFragment) fragmentList.get(position);
        switch (checkedId){
            case R.id.meeting_room:
                position=0;
                break;
            case R.id.meeting:
                position=1;
                break;
            case R.id.my:
                position=2;
                break;
        }
        //获得要转到的fragment
        BaseFragment to= (BaseFragment) fragmentList.get(position);
        changeFragment(mContent,to);
    }

    /**
    *切换fragment
     *@return void
    *@author wenhaoz
    *created at 2019/2/16 23:03
     * @param to
     * @param from
    */
    private void changeFragment(BaseFragment from, BaseFragment to) {
        transaction=fm.beginTransaction();
        if(to != null&&from != null&& from != to){
            mContent=to;
            if(!to.isAdded()){
                //没有被添加进去
                //隐藏from
                transaction.hide(from);
                //添加to
                transaction.add(R.id.fl_main,to);
            } else{
                //隐藏from
                transaction.hide(from);
                //展示
                transaction.show(to);
            }
          transaction.commit();
        }
    }
}
