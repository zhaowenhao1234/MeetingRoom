package com.example.zwh.meetingroom.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.base.BaseFragment;

public class MeetingRoomFragment extends BaseFragment {

    @Override
    protected View initView() {
        return View.inflate(mContext,R.layout.meetingroom_fragment,null);
    }

    @Override
    protected void initData() {

    }
}
