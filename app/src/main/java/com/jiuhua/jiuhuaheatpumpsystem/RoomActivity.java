package com.jiuhua.jiuhuaheatpumpsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;

import com.jiuhua.mqttsample.IGetMessageCallBack;
import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.MyServiceConnection;


public class RoomActivity extends AppCompatActivity implements IGetMessageCallBack, View.OnClickListener {
    private MyServiceConnection serviceConnection;//连接实例
    private MQTTService mqttService;//服务实例
    private MyView myView;
    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvModuleState;
    private TextView tvFanState;
    private TextView tvFloorState;
    private Button btManualOff;//手动关
    private Button btManualOn;//手动开
    private Button btAutomation;//自动运行
    private Button btSetPeroid;//宴会
    String roomName="";
    String roomID="";
    String temperature="";
    String humidity="";

    private Context context;
    private String peroid_S;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        myView = findViewById(R.id.myview);
        tvTemperature = findViewById(R.id.currentTemperature);
        tvHumidity = findViewById(R.id.currentHumidity);

        tvModuleState = findViewById(R.id.modulestate);
        tvFanState = findViewById(R.id.fanstate);
        tvFloorState = findViewById(R.id.floorstate);

        btManualOn = findViewById(R.id.manual_on);
        btManualOff = findViewById(R.id.turn_off);
        btAutomation = findViewById(R.id.automation);
        btSetPeroid = findViewById(R.id.setperoid);
        TextView tvRoomName = findViewById(R.id.roomname);
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomname");  //Mainactivity传过来的点击的哪一个按钮（房间）
        roomID = intent.getStringExtra("roomid");  //传一个标识
        tvRoomName.setText(roomName);
        if (calendar.get(Calendar.MONTH)<9 && calendar.get(Calendar.MONTH)>3) {
            peroid_S = sharedPreferences.getString("Room"+roomID+"summerperoid_S", "");//利用标识，获取存储的周期数据
        } else {
            peroid_S = sharedPreferences.getString("Room"+roomID+"winterperoid_S", "");
        }

        context = this;
        myView.getdata(peroid_S);  //把获取的周期数据传给myview

        btManualOn.setOnClickListener(this);
        btManualOff.setOnClickListener(this);
        btAutomation.setOnClickListener(this);
        btSetPeroid.setOnClickListener(this);
        //MQTT服务，显示实时的温度和湿度
        serviceConnection = new MyServiceConnection();//新建连接服务的实例
        serviceConnection.setIGetMessageCallBack(RoomActivity.this);//把本活动传入
        Intent intentServer = new Intent(this, MQTTService.class);//传递信息
        bindService(intentServer, serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务

        MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                "Room"+roomID+"feedback", 1, true);
    }

    @Override
    public void setMessage(String message) {
        //依据message字符串最后一位决定房间号，倒数第二位决定温湿度C为温度，H为湿度。
        if(message.contains("C"+roomID)) temperature = message.replace("C" + roomID, "C");
        if(message.contains("RH"+roomID)) humidity = message.replace("RH" + roomID, "RH");
        //回传的模块状态
        if(message.contains("turnoffRoom"+roomID+"FP")) tvModuleState.setText("    停止");
        if(message.contains("manualRoom"+roomID+"FP")) tvModuleState.setText("    手动");
        if(message.contains("automationRoom"+roomID+"FP")) tvModuleState.setText("    自动");
        if(message.contains("dehumidityRoom"+roomID+"FP")) tvModuleState.setText("    除湿");
        //回传的风机状态
        if(message.contains("lowRoom"+roomID+"FP")) tvFanState.setText("    低风速");
        if(message.contains("middleRoom"+roomID+"FP")) tvFanState.setText("    中风速");
        if(message.contains("highRoom"+roomID+"FP")) tvFanState.setText("    高风速");
        if(message.contains("stopRoom"+roomID+"FP")) tvFanState.setText("    风速停");
        //地暖模块回传的信息
        if(message.contains("valveonRoom"+roomID+"floor")) tvFloorState.setText("    地暖运行");
        if(message.contains("valveoffRoom"+roomID+"floor")) tvFloorState.setText("    地暖停止");

        tvTemperature.setText(temperature);
        tvHumidity.setText(humidity);
        //服务实例
        mqttService = serviceConnection.getMqttService();
        //mqttService.toCreateNotification(message);//服务的发布消息的方法
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.turn_off:
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"turn-offFP", 1, true);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                        "Room"+roomID+"turn-offfloor", 1, true);
                //设置各个按钮的颜色
                btManualOff.setBackgroundColor(Color.parseColor("#00FF00"));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.argb(20,0,0,0));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.argb(20,0,0,0));//设置自动按钮的背景为灰色
                btSetPeroid.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色
                break;
            case R.id.manual_on:
                //设置各个按钮的颜色
                btManualOff.setBackgroundColor(Color.argb(20,0,0,0));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.parseColor("#00FF00"));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.argb(20,0,0,0));//设置自动按钮的背景为灰色
                btSetPeroid.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色

                        /*实现数字选择器，比较经典，保留参考！！
                        //用数字选择器实现温度temperature选择
                        AlertDialog.Builder temperatureBuilder = new AlertDialog.Builder(context);
                        temperatureBuilder.setPositiveButton("设置", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (pickedNumber > 0) setTemperature = pickedNumber;
                                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,
                                        valueOf(setTemperature)+"Room"+roomID+"set_temp", 1, true);
                            }
                        });
                        temperatureBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog temperatureDialog = temperatureBuilder.create();
                        View temperatureDialogView = View.inflate(context, R.layout.activity_manual, null);
                        NumberPicker temperatureNumberPicker = temperatureDialogView.findViewById(R.id.numberpicker);
                        temperatureNumberPicker.setMaxValue(35);
                        temperatureNumberPicker.setMinValue(5);
                        temperatureNumberPicker.setValue(setTemperature);//这里使用变量记录了前面的数值
                        temperatureNumberPicker.setOnValueChangedListener(this);
                        temperatureDialog.setTitle("手动设置");
                        temperatureDialog.setView(temperatureDialogView);
                        temperatureDialog.show();
                        */

                //先开阀，再考虑温度，是不是应该开。
                if (calendar.get(Calendar.MONTH)<9 && calendar.get(Calendar.MONTH)>3) {   //通过月份来判断冬季还是夏季
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,"Room"+roomID+"manual-onFP", 1, true);
                } else {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,"Room"+roomID+"manual-onfloor", 1, true);
                }
                //跳转到一个单独的设置页面（ManualActivity）
                Intent intent = new Intent(RoomActivity.this, ManualActivity.class);
                intent.putExtra("roomname", roomName);//传递了房间名字的参数
                intent.putExtra("roomid", roomID);//传递了房间的ID
                startActivity(intent);

                break;
            case R.id.automation:
                if (calendar.get(Calendar.MONTH)<9 && calendar.get(Calendar.MONTH)>3) {    //通过月份来判断冬季还是夏季
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,"Room"+roomID+"automationFP", 1, true);
                } else {
                    MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID,"Room"+roomID+"automationfloor", 1, true);
                }
                //设置各个按钮的颜色
                btManualOff.setBackgroundColor(Color.argb(20,0,0,0));//设置手动停止按钮背景为绿色
                btManualOn.setBackgroundColor(Color.argb(20,0,0,0));//设置手动的按钮背景为灰色
                btAutomation.setBackgroundColor(Color.parseColor("#00FF00"));//设置自动按钮的背景为灰色
                btSetPeroid.setBackgroundColor(Color.argb(20,0,0,0));//设置宴会按钮的背景为灰色
                break;
            case R.id.setperoid:
                //跳转到一个单独的设置页面（SetPeroidActivity）
                Intent intent1 = new Intent(RoomActivity.this, SetPeroidActivity.class);
                intent1.putExtra("roomname", roomName);//传递了房间名字的参数
                intent1.putExtra("roomid", roomID);//传递了房间的ID
                startActivity(intent1);
                break;
        }
    }

}

