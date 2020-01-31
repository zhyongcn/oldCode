package com.jiuhua.jiuhuaheatpumpsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.espressif.iot.esptouch.demo_activity.EsptouchDemoActivity;
import com.jiuhua.mqttsample.IGetMessageCallBack;
import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.MyServiceConnection;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack {

    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private Button buttonE;
    private Button buttonF;
    private Button buttonG;
    private Button buttonH;

    //定义了各个房间的名称变量
    private String room1name;
    private String room2name;
    private String room3name;
    private String room4name;
    private String room5name;
    private String room6name;
    private String room7name;
    private String room8name;

    //MQTT需要的参数
    private MyServiceConnection serviceConnection;//连接实例
    private MQTTService mqttService;//服务实例

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //TODO 是不是用全局变量来存储数据，getapplication（）
        //从存储器中读取数据：各个房间的名字
        //这个放在开始的部分，利用线程，不耽误其他线程
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        room1name = sharedPreferences.getString("room1name","");
        room2name = sharedPreferences.getString("room2name","");
        room3name = sharedPreferences.getString("room3name","");
        room4name = sharedPreferences.getString("room4name","");
        room5name = sharedPreferences.getString("room5name","");
        room6name = sharedPreferences.getString("room6name","");
        room7name = sharedPreferences.getString("room7name","");
        room8name = sharedPreferences.getString("room8name","");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //拿到各个按钮的实例，定义了视图中的一系列按钮
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        buttonA =  findViewById(R.id.buttonA);
        buttonB =  findViewById(R.id.buttonB);
        buttonC =  findViewById(R.id.buttonC);
        buttonD =  findViewById(R.id.buttonD);
        buttonE =  findViewById(R.id.buttonE);
        buttonF =  findViewById(R.id.buttonF);
        buttonG =  findViewById(R.id.buttonG);
        buttonH =  findViewById(R.id.buttonH);

        //如果房间名称是空的，让这个按钮不可见
        if(room1name.equals("")) buttonA.setVisibility(View.INVISIBLE);
        if(room2name.equals("")) buttonB.setVisibility(View.INVISIBLE);
        if(room3name.equals("")) buttonC.setVisibility(View.INVISIBLE);
        if(room4name.equals("")) buttonD.setVisibility(View.INVISIBLE);
        if(room5name.equals("")) buttonE.setVisibility(View.INVISIBLE);
        if(room6name.equals("")) buttonF.setVisibility(View.INVISIBLE);
        if(room7name.equals("")) buttonG.setVisibility(View.INVISIBLE);
        if(room8name.equals("")) buttonH.setVisibility(View.INVISIBLE);

        //刷新按钮的功能实现
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                onDestroy();
            }
        });
        //文档和报修按钮的功能实现
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DocumentsAndRepairActivity.class);
                startActivity(intent);
            }
        });
        //点击各个房间的按钮，跳转到各个房间的详情
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room1name);//传递了房间名字的参数
                intent.putExtra("roomid", "1");//传递了房间的ID
                startActivity(intent);
            }
        });
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room2name);//传递了房间名字的参数
                intent.putExtra("roomid", "2");
                startActivity(intent);
            }
        });
        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room3name);
                intent.putExtra("roomid", "3");
                startActivity(intent);
            }
        });
        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room4name);
                intent.putExtra("roomid", "4");
                startActivity(intent);
            }
        });
        buttonE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room5name);
                intent.putExtra("roomid", "5");
                startActivity(intent);
            }
        });
        buttonF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room6name);
                intent.putExtra("roomid", "6");
                startActivity(intent);
            }
        });
        buttonG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomname", room7name);
                intent.putExtra("roomid", "7");
                startActivity(intent);
            }
        });
        buttonH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//启动室外的页面
                Intent intent = new Intent(MainActivity.this, OutdoorActivity.class);
                startActivity(intent);
            }
        });


        serviceConnection = new MyServiceConnection();//新建连接服务的实例
        serviceConnection.setIGetMessageCallBack(MainActivity.this);//把本活动传入
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    //右上角的目录的设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userinfo:
                //这里的intent必须不同，因为他们在一个作用域范围内
                Intent intent1 = new Intent(MainActivity.this, UserinfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.set_up_net:
                Intent intent2 = new Intent(MainActivity.this, EsptouchDemoActivity.class);
                startActivity(intent2);
                break;
            case R.id.set_up_timer:
                Intent intent3 = new Intent(MainActivity.this, timerSettingActivity.class);
                startActivity(intent3);
                break;
            case R.id.check_version:
                //Intent intent4 = new Intent(MainActivity.this, );
                Toast.makeText(this, "开发中，敬请期待。。。",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    String room1temperature="";
    String room1humidity="";
    String room2temperature="";
    String room2humidity="";
    String room3temperature="";
    String room3humidity="";
    String room4temperature="";
    String room4humidity="";
    String room5temperature="";
    String room5humidity="";
    String room6temperature="";
    String room6humidity="";
    String room7temperature="";
    String room7humidity="";
    String room8temperature="";
    String room8humidity="";
    //定义运行状态
    String room1states="状态未知";
    String room2states="状态未知";
    String room3states="状态未知";
    String room4states="状态未知";
    String room5states="状态未知";
    String room6states="状态未知";
    String room7states="状态未知";
    String room8states="";


    @SuppressLint("SetTextI18n")
    @Override
    public void setMessage(String message) {
        //依据message字符串最后一位决定房间号，倒数第二位决定温湿度C为温度，H为湿度。
        if(message.contains("C1")) room1temperature = message.replace("C1", "C");
        if(message.contains("RH1")) room1humidity = message.replace("RH1", "RH");
        if(message.contains("C2")) room2temperature = message.replace("C2", "C");
        if(message.contains("RH2")) room2humidity = message.replace("RH2", "RH");
        if(message.contains("C3")) room3temperature = message.replace("C3", "C");
        if(message.contains("RH3")) room3humidity = message.replace("RH3", "RH");
        if(message.contains("C4")) room4temperature = message.replace("C4", "C");
        if(message.contains("RH4")) room4humidity = message.replace("RH4", "RH");
        if(message.contains("C5")) room5temperature = message.replace("C5", "C");
        if(message.contains("RH5")) room5humidity = message.replace("RH5", "RH");
        if(message.contains("C6")) room6temperature = message.replace("C6", "C");
        if(message.contains("RH6")) room6humidity = message.replace("RH6", "RH");
        if(message.contains("C7")) room7temperature = message.replace("C7", "C");
        if(message.contains("RH7")) room7humidity = message.replace("RH7", "RH");
        if(message.contains("C8")) room8temperature = message.replace("C8", "C");
        if(message.contains("RH8")) room8humidity = message.replace("RH8", "RH");

        //运行状态需要反馈回来
        if(message.contains("valveonRoom1")) {room1states = "正在运行";}
        if(message.contains("valveonRoom2")) {room2states = "正在运行";}
        if(message.contains("valveonRoom3")) {room3states = "正在运行";}
        if(message.contains("valveonRoom4")) {room4states = "正在运行";}
        if(message.contains("valveonRoom5")) {room5states = "正在运行";}
        if(message.contains("valveonRoom6")) {room6states = "正在运行";}
        if(message.contains("valveonRoom7")) {room7states = "正在运行";}
        if(message.contains("valveonRoom8")) {room8states = "正在运行";}

        if(message.contains("shutoffRoom1")) {room1states = "停止运行";}
        if(message.contains("shutoffRoom2")) {room2states = "停止运行";}
        if(message.contains("shutoffRoom3")) {room3states = "停止运行";}
        if(message.contains("shutoffRoom4")) {room4states = "停止运行";}
        if(message.contains("shutoffRoom5")) {room5states = "停止运行";}
        if(message.contains("shutoffRoom6")) {room6states = "停止运行";}
        if(message.contains("shutoffRoom7")) {room7states = "停止运行";}
        if(message.contains("shutoffRoom8")) {room8states = "停止运行";}


        //设置显示的文字
        buttonA.setText("\n"+ room1name +"\n\n"+ room1temperature +"\n\n" + room1humidity +"\n\n"+room1states+"\n");
        buttonB.setText("\n"+ room2name +"\n\n"+ room2temperature +"\n\n" + room2humidity +"\n\n"+room2states+"\n");
        buttonC.setText("\n"+ room3name +"\n\n"+ room3temperature +"\n\n" + room3humidity +"\n\n"+room3states+"\n");
        buttonD.setText("\n"+ room4name +"\n\n"+ room4temperature +"\n\n" + room4humidity +"\n\n"+room4states+"\n");
        buttonE.setText("\n"+ room5name +"\n\n"+ room5temperature +"\n\n" + room5humidity +"\n\n"+room5states+"\n");
        buttonF.setText("\n"+ room6name +"\n\n"+ room6temperature +"\n\n" + room6humidity +"\n\n"+room6states+"\n");
        buttonG.setText("\n"+ room7name +"\n\n"+ room7temperature +"\n\n" + room7humidity +"\n\n"+room7states+"\n");
        buttonH.setText("\n"+ room8name +"\n\n"+ room8temperature +"\n\n" + room8humidity +"\n\n"+room8states+"\n");
        mqttService = serviceConnection.getMqttService();//服务连接实例 的 获得服务的方法
//        mqttService.toCreateNotification(message);//服务的发布消息的方法
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //这里是不是应该实现查询反馈指令？ 这个时候activity还没有走到前台。
        //因为两遍代码的原因，也可以不在这里实现。
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room1","Room1feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room2","Room2feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room3","Room3feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room4","Room4feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room5","Room5feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room6","Room6feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room7","Room7feedback", 1, true);
//        MQTTService.publish("86518/JYCFGC/6-2-3401/Room8","Room8feedback", 1, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //应该在这里实现查询反馈指令。（最初onstart的时候也经过onResume）
        //TODO 在全新启动的时候没有发布，是不是MQTTService 没有就绪。是不是应该在成功订阅“Room1” 的主题之后在做一次发布。
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room1","Room1feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room2","Room2feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room3","Room3feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room4","Room4feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room5","Room5feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room6","Room6feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room7","Room7feedback", 1, true);
        MQTTService.publish("86518/JYCFGC/6-2-3401/Room8","Room8feedback", 1, true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
