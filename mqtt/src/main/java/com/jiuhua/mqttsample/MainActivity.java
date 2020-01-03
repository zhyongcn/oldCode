package com.jiuhua.mqttsample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack {

    private TextView textView;
    private Button button;
    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        serviceConnection = new MyServiceConnection();//创建连接服务的实例
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
                             //把当前活动传入连接实例的set方法，产生域值IGetMessageCallBack

        Intent intent = new Intent(this, MQTTService.class);//启动的钩子，启动服务的方法

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);//这是一种隐式启动的方法吗？


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MQTTService.publish( "/mqtt/topic/hello1","测试一下子", 0, false);
            }
        });
    }

    @Override
    public void setMessage(String message) {
        textView.setText(message);
        mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
