package com.example.zwh.meetingroom.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zwh.meetingroom.R;
import com.example.zwh.meetingroom.base.BaseFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeetingRoomFragment extends BaseFragment {

    private View view;
    private Button net_test_button;
    private TextView net_test;
    private Response response;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            net_test.setText(msg.obj.toString());
            return true;
        }
    });

    @Override
    protected View initView() {
        view = View.inflate(mContext, R.layout.meetingroom_fragment, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        net_test = (TextView) getActivity().findViewById(R.id.net_test);
        net_test_button = (Button) getActivity().findViewById(R.id.net_test_button);
        net_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNet("https://www.baidu.com/");
            }
        });
    }



    private void requestNet(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder().get().url(url).build();
                Call call=okHttpClient.newCall(request);
                try {
                    response=call.execute();
                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void initData() {

    }
}
