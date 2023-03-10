package com.example.weather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class MainActivity extends AppCompatActivity {
    private Button mbutton;
    private TextView mtv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setBackgroundDrawableResource(R.drawable.chongqing);
        initView();
        mHandler = new MyHandler();//准备接线员
        mbutton.setOnClickListener(view -> sendGetNetRequest());
    }

    @SuppressLint("SetTextI18n")
    private void sendGetNetRequest() {
        new Thread(
                () -> {
                    try {
                        URL url = new URL("https://weather.cma.cn//web/weather/57516.html");
                        HttpURLConnection connection = (HttpURLConnection)
                                url.openConnection();
                        connection.setRequestMethod("GET");//设置请求方式为GET
                        connection.setConnectTimeout(8000);//设置最大连接时间，单位为ms
                        connection.setReadTimeout(8000);//设置最大的读取时间，单位为ms
                        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
                        connection.connect();//正式连接
                        InputStream in = connection.getInputStream();//从接口处获取
                        String responseData = StreamToString(in);//这里就是服务器返回的数据
                        Message message = new Message();//新建一个Message
                        message.obj = responseData;//将数据赋值给message的obj属性
                        mHandler.sendMessage(message);
                        Log.d("RQ", "sendGetNetRequest: " + responseData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    private String StreamToString(InputStream in) {
        StringBuilder sb = new StringBuilder();//新建一个StringBuilder，用于一点一点
        String oneLine;//流转换为字符串的一行
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));//
        try {
            while ((oneLine = reader.readLine()) != null) {//readLine方法将读取一行
                sb.append(oneLine).append('\n');//拼接字符串并且增加换行，提高可读性
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();//关闭InputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();//将拼接好的字符串返回出去
    }
    private void initView() {
        mtv = findViewById(R.id.textView);
        mbutton = findViewById(R.id.button);
    }

    @SuppressLint("SetTextI18n")
    private void jsonDecodeTest(String jsonData) {
        String a = jsonData.substring(jsonData.indexOf("high"), jsonData.indexOf("\"low\""));
        String b = jsonData.substring(jsonData.indexOf("\"low\""), jsonData.indexOf("day-item nighticon"));
        String str1 = a.replaceAll("\\D+", "");//提取数字
        String str2 = b.replaceAll("\\D+", "");//提取数字
        mtv.setText(str1 + "℃-" + str2 + "℃");
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //这个Message msg 就是从另一个线程传递过来的数据
            //在这里进行你要对msg的处理
            String responseData = msg.obj.toString();
            jsonDecodeTest(responseData);
        }
    }
}