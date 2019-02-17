package com.example.zwh.meetingroom.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
*作为MeetingRoomFragment,MeetingFragment,MyFragment的基类
*@param 
*@return 
*@author wenhaoz
*created at 2019/2/16 21:43
*/

public abstract class BaseFragment extends Fragment {
    public Context mContext;
    public BaseFragment() {
        super();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    /**
    *强制子类实现视图初始化
    *@param
    *@return
    *@author wenhaoz
    *created at 2019/2/16 21:36
    */
    protected abstract View initView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }
    /**
    *初始化数据
    *@param 
    *@return 
    *@author wenhaoz
    *created at 2019/2/16 21:40
    */

    protected abstract void initData();
}
