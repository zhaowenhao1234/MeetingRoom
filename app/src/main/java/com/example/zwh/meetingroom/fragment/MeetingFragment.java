package com.example.zwh.meetingroom.fragment;

import android.view.View;
import android.widget.TextView;

import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.base.BaseFragment;

import java.util.zip.Inflater;

public class MeetingFragment extends BaseFragment {


    @Override
    protected View initView() {
        return View.inflate(mContext,R.layout.meeting_fragment,null);
    }

    @Override
    protected void initData() {

    }
}
