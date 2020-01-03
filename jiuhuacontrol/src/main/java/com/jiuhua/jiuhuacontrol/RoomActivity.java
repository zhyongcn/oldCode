package com.jiuhua.jiuhuacontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jiuhua.mqttsample.IGetMessageCallBack;
import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.MyServiceConnection;

import static java.lang.String.valueOf;

public class RoomActivity extends AppCompatActivity implements IGetMessageCallBack,
        View.OnClickListener, NumberPicker.OnValueChangeListener{
    private MyServiceConnection serviceConnection;//连接实例
    private MQTTService mqttService;//服务实例
    private MyView myView;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private Button btManualOff;//手动关
    private Button btManualOn;//手动开
    private Button btAutomation;//自动运行
    private Button btFeast;//宴会
    String roomID="";
    String temperature="";
    String humidity="";

    private Context context;
    private int settingTemperature = 18;//缺省温度
    private String peroid_S;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        myView = findViewById(R.id.myview);
        tvTemperature = findViewById(R.id.currentTemperature);
        tvHumidity = findViewById(R.id.currentHumidity);
        btManualOn = findViewById(R.id.manual_on);
        btManualOff = findViewById(R.id.manual_off);
        btAutomation = findViewById(R.id.automation);
        btFeast = findViewById(R.id.feast);
        TextView tvRoomName = findViewById(R.id.roomname);
        Intent intent = getIntent();
        String roomName = intent.getStringExtra("roomname");//Mainactivity传过来的点击的哪一个按钮（房间）
        roomID = intent.getStringExtra("roomid");//传一个标识
        tvRoomName.setText(roomName);
        peroid_S = sharedPreferences.getString("Room"+roomID+"peroid_S", "");//利用标识，获取存储的周期数据

        context = this;
        myView.getdata(peroid_S);//把获取的周期数据传给myview

        btManualOn.setOnClickListener(this);
        btManualOff.setOnClickListener(this);
        btAutomation.setOnClickListener(this);
        btFeast.setOnClickListener(this);
        //MQTT服务，显示实时的温度和湿度
        serviceConnection = new MyServiceConnection();//新建连接服务的实例
        serviceConnection.setIGetMessageCallBack(RoomActivity.this);//把本活动传入
        Intent intentServer = new Intent(this, MQTTService.class);//传递信息
        bindService(intentServer, serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务
    }

    @Override
    public void setMessage(String message) {
        //依据message字符串最后一位决定房间号，倒数第二位决定温湿度C为温度，H为湿度。
        if(message.contains("C"+roomID)) temperature = message.replace("C" + roomID, "C");
        if(message.contains("RH"+roomID)) humidity = message.replace("RH" + roomID, "RH");

        tvTemperature.setText(temperature);
        tvHumidity.setText(humidity);
        //服务实例
        mqttService = serviceConnection.getMqttService();
        //mqttService.toCreateNotification(message);//服务的发布消息的方法
    }

//TODO 模块回传状态，设置四个按钮的颜色。
    //先注释掉，暂时不用
//    boolean manualOnState = false;
//    boolean manualOffState = false;
//    boolean automation_state = false;
//    boolean feastState = false;
    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.manual_off:
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"turn-off", 1, true);
                btManualOff.setBackgroundColor(Color.parseColor("#00FF00"));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.argb(20,0,0,0));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.argb(20,0,0,0));//设置自动按钮的背景为灰色
                btFeast.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色
                break;
            case R.id.manual_on:
                //TODO  用数字选择器实现温度选择
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                                valueOf(settingTemperature)+"Room"+roomID+"set_temp", 1, true);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_number, null);
                NumberPicker numberPicker = dialogView.findViewById(R.id.numberpicker);
                numberPicker.setMaxValue(35);
                numberPicker.setMinValue(5);
                numberPicker.setValue(settingTemperature);//这里使用变量记录了前面的数值
                numberPicker.setOnValueChangedListener(this);
                dialog.setTitle("设置温度");
                dialog.setView(dialogView);
                dialog.show();
                //先开阀，再考虑温度，是不是应该开。
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,"Room"+roomID+"turn-on", 1, true);
                btManualOff.setBackgroundColor(Color.argb(20,0,0,0));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.parseColor("#00FF00"));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.argb(20,0,0,0));//设置自动按钮的背景为灰色
                btFeast.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色
                break;
            case R.id.automation:
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"automation", 1, true);
                btManualOff.setBackgroundColor(Color.argb(20,0,0,0));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.argb(20,0,0,0));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.parseColor("#00FF00"));//设置自动按钮的背景为灰色
                btFeast.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色
                break;
            case R.id.feast:
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"feast", 1, true);
                btManualOff.setBackgroundColor(Color.argb(20,0,0,0));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.argb(20,0,0,0));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.argb(20,0,0,0));//设置自动按钮的背景为灰色
                btFeast.setBackgroundColor(Color.parseColor("#00FF00"));//设置宴会按钮的背景为灰色
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        this.settingTemperature = newVal;
    }
}
