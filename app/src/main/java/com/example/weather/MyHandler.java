package com.example.weather;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

//这个MyHandler作为内部类
public class MyHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        //这个Message msg 就是从另一个线程传递过来的数据
        String responseData = msg.obj.toString();
        jsonDecodeTest(responseData);
    }

    private void jsonDecodeTest(String responseData) {

    }
}