package com.example.zwh.meetingroom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.activity.FaceLogin;
import com.example.zwh.meetingroom.base.BaseFragment;

import static android.content.ContentValues.TAG;

public class MyFragment extends BaseFragment implements View.OnClickListener {

    private View myFragmentView;
    private Button my_booking_button;
    private Button message_button;
    private Button notebook_button;
    private Button face_login_button;

    @Override
    protected View initView() {
        myFragmentView=View.inflate(mContext,R.layout.my_fragment,null);
        my_booking_button = (Button) myFragmentView.findViewById(R.id.my_booking_button);
        message_button = (Button) myFragmentView.findViewById(R.id.message_button);
        notebook_button = (Button) myFragmentView.findViewById(R.id.notebook_button);
        face_login_button = (Button) myFragmentView.findViewById(R.id.face_login_button);
        my_booking_button.setOnClickListener(this);
        message_button.setOnClickListener(this);
        notebook_button.setOnClickListener(this);
        face_login_button.setOnClickListener(this);
        return myFragmentView;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.face_login_button:
                Intent faceLogin=new Intent(mContext,FaceLogin.class);
                startActivity(faceLogin);
                break;
        }
    }
}
