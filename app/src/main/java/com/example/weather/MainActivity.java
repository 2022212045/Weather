package com.example.weather;

import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mbutton.setOnClickListener(view -> sendGetNetRequest());

    }

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
                        MyHandler mHandler = new MyHandler();
                        //下面三行代码的意思就是将数据保存在message里也就是消息里，然后交给接线员
                        Message message = new Message();//新建一个Message
                        message.obj = responseData;//将数据赋值给message的obj属性
                        mHandler.sendMessage(message);
                        mtv.setText(responseData);
//                        Log.d("RQ", "sendGetNetRequest: "+responseData);
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
        int time=0;
        try {
//            (oneLine = reader.readLine()).indexOf("℃")==-1
            while (reader.readLine() != null) {
                //readLine方法将读取一行
                if ((oneLine = reader.readLine()).contains("℃")) {
                } else {
                    if (time == 0) {
                        sb.append('最').append('高').append(oneLine).append('\n');
                    } else {
                        sb.append('最').append('低').append(oneLine).append('\n');
                    }
                    time++;
                    if (time > 1) {
                        break;
                    }
                }
                //拼接字符串并且增加换行，提高可读性
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



}