package com.jiuhua.jiuhuaheatpumpsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.IGetMessageCallBack;
import com.jiuhua.mqttsample.MyServiceConnection;

public class ManualActivity extends AppCompatActivity {

    String roomName;
    String roomID="";
    private int setTemperature = 18;//缺省温度
    private int setHumidity = 60; //缺省湿度
    private int  fanspeed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomname"); //Mainactivity传过来的点击的哪一个按钮（房间）
        roomID = intent.getStringExtra("roomid");//传一个标识

        TextView tvRoomName = findViewById(R.id.manualRoomName);
        tvRoomName.setText(roomName);

        final RadioButton highspeedfan = findViewById(R.id.fanhighspeed);
        final RadioButton middlespeedfan = findViewById(R.id.fanmiddlespeed);
        final RadioButton lowspeedfan = findViewById(R.id.fanlowspeed);
        final RadioButton autospeedfan = findViewById(R.id.fanautospeed);
        final RadioButton floorheat = findViewById(R.id.floorheat);

        RadioGroup fanspeedselecter = findViewById(R.id.fanspeedselecter);
        fanspeedselecter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == lowspeedfan.getId()) {
                    fanspeed = 0;
                }
                if (checkedId == middlespeedfan.getId()) {
                    fanspeed = 1;
                }
                if (checkedId == highspeedfan.getId()) {
                    fanspeed = 2;
                }
                if (checkedId == autospeedfan.getId()) {
                    fanspeed = 3;
                }
                if (checkedId == floorheat.getId()) {
                    fanspeed = 4;
                }
            }
        });

        Button confirmTemperature = findViewById(R.id.confirmTemperature);
        Button confirmHumidity = findViewById(R.id.confirmHumidity);

        NumberPicker temperatureNumberPicker = findViewById(R.id.temperaturePicker);
        temperatureNumberPicker.setMaxValue(35);
        temperatureNumberPicker.setMinValue(5);
        temperatureNumberPicker.setValue(setTemperature);//这里使用变量记录了前面的数值
        //接口方法改编为调用的方法，避免了数值的胡乱安插
        temperatureNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setTemperature = newVal;
            }
        });
        temperatureNumberPicker.setWrapSelectorWheel(false);
        temperatureNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker humidityNumberPicker = findViewById(R.id.humidityPicker);
        humidityNumberPicker.setMaxValue(80);
        humidityNumberPicker.setMinValue(20);
        humidityNumberPicker.setValue(setHumidity);//这里使用变量记录了前面的数值
        humidityNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setHumidity = newVal;
            }
        });
        humidityNumberPicker.setWrapSelectorWheel(false);
        humidityNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        confirmTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //传输设置温度temperature
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        setTemperature+"Room"+roomID+"set_temp", 1, true);
                //传输设置的风速
                if (fanspeed == 0) {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"lowSpeed", 1, true);
                }
                if (fanspeed == 1) {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"middleSpeed", 1, true);
                }
                if (fanspeed == 2) {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"highSpeed", 1, true);
                }
                if (fanspeed == 3) {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"manual-onFP", 1, true); //自动风就是最初的手动模式
                }
                if (fanspeed == 4) {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"manual-onfloor", 1, true);
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"turn-offFP", 1, true);
                }else {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                            "Room"+roomID+"turn-offfloor", 1, true);
                }

                finish();
            }

        });

        confirmHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //传输设置湿度humidity
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        setHumidity+"Room"+roomID+"set_humidity", 1, true); //传给模块设定湿度
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"deHumidity", 1, true);  //启动除湿
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"turn-offfloor", 1, true);

                finish();
            }
        });

    }

}
