package com.jiuhua.jiuhuaheatpumpsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.MyServiceConnection;

import java.util.StringTokenizer;

import static java.lang.String.valueOf;


public class timerSettingActivity extends AppCompatActivity implements View.OnClickListener,
        TimePicker.OnTimeChangedListener, NumberPicker.OnValueChangeListener {

    private MyServiceConnection serviceConnection;//连接实例
    //private MQTTService mqttService;//服务实例，应该也可以不引入，直接使用MQTTService。

    /**
     * 定义一个三维数组代表8个房间。 一周7天。
     * 时段morning，beforenoon，noon，afternoon，evening，night按照顺序实现。
     * 开始时间（时0-23和分0-59），结束时间（时和分），设定温度，重复6次，一共30个数据。
     * int类型，因为时间都是小整数。
     */
    String[] periodString = new String[8];
    private int[][][] period = new int[8][7][30];
    //数组需要的参数
    private int room, weekday, session, startHour, startMinute, endHour, endMinute, temperature;

    private Context context;
    TextView displayStartTime, displayEndTime, displayTemperature;
    //中间转移用的变量
    private int hour, minute, temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        //这个放在开始的部分，利用线程，不耽误其他线程
        //定义了各个房间的名称变量
        final String room1name = sharedPreferences.getString("room1name", "");  //在内部类中使用需要定义为 final 形式！！
        final String room2name = sharedPreferences.getString("room2name", "");
        final String room3name = sharedPreferences.getString("room3name", "");
        final String room4name = sharedPreferences.getString("room4name", "");
        final String room5name = sharedPreferences.getString("room5name", "");
        final String room6name = sharedPreferences.getString("room6name", "");
        final String room7name = sharedPreferences.getString("room7name", "");
        final String room8name = sharedPreferences.getString("room8name", "");
        /*
        * 下面的是从存储（sharedpreferences）中获取字符串，再转换位int数组
        * 不熟悉Java函数如何操作数组，所以变成这样了
        * 可以优化！！*/
        int i, j;
        String Room1peroid_S = sharedPreferences.getString("Room1peroid_S", "");
        StringTokenizer tokenizer0 = new StringTokenizer(Room1peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer0.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[0][i][j] = Integer.valueOf(tokenizer0.nextToken());
                }
            }
        }
        String Room2peroid_S = sharedPreferences.getString("Room2peroid_S", "");
        StringTokenizer tokenizer1 = new StringTokenizer(Room2peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer1.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[1][i][j] = Integer.valueOf(tokenizer1.nextToken());
                }
            }
        }
        String Room3peroid_S = sharedPreferences.getString("Room3peroid_S", "");
        StringTokenizer tokenizer2 = new StringTokenizer(Room3peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer2.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[2][i][j] = Integer.valueOf(tokenizer2.nextToken());
                }
            }
        }
        String Room4peroid_S = sharedPreferences.getString("Room4peroid_S", "");
        StringTokenizer tokenizer3 = new StringTokenizer(Room4peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer3.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[3][i][j] = Integer.valueOf(tokenizer3.nextToken());
                }
            }
        }
        String Room5peroid_S = sharedPreferences.getString("Room5peroid_S", "");
        StringTokenizer tokenizer4 = new StringTokenizer(Room5peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer4.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[4][i][j] = Integer.valueOf(tokenizer4.nextToken());
                }
            }
        }
        String Room6peroid_S = sharedPreferences.getString("Room6peroid_S", "");
        StringTokenizer tokenizer5 = new StringTokenizer(Room6peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer5.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[5][i][j] = Integer.valueOf(tokenizer5.nextToken());
                }
            }
        }
        String Room7peroid_S = sharedPreferences.getString("Room7peroid_S", "");
        StringTokenizer tokenizer6 = new StringTokenizer(Room7peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer6.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[6][i][j] = Integer.valueOf(tokenizer6.nextToken());
                }
            }
        }
        String Room8peroid_S = sharedPreferences.getString("Room8peroid_S", "");
        StringTokenizer tokenizer7 = new StringTokenizer(Room8peroid_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer7.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    period[7][i][j] = Integer.valueOf(tokenizer7.nextToken());
                }
            }
        }
        //Toast.makeText(timerSettingActivity.this, String.valueOf(period), Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setting);
        context = this;

        final RadioButton room1button = findViewById(R.id.room1button);
        final RadioButton room2button = findViewById(R.id.room2button);
        final RadioButton room3button = findViewById(R.id.room3button);
        final RadioButton room4button = findViewById(R.id.room4button);
        final RadioButton room5button = findViewById(R.id.room5button);
        final RadioButton room6button = findViewById(R.id.room6button);
        final RadioButton room7button = findViewById(R.id.room7button);
        final RadioButton room8button = findViewById(R.id.room8button);
        room1button.setText(room1name);
        room2button.setText(room2name);
        room3button.setText(room3name);
        room4button.setText(room4name);
        room5button.setText(room5name);
        room6button.setText(room6name);
        room7button.setText(room7name);
        room8button.setText(room8name);

        final RadioButton week1button = findViewById(R.id.Mon);
        final RadioButton week2button = findViewById(R.id.Tue);
        final RadioButton week3button = findViewById(R.id.Wed);
        final RadioButton week4button = findViewById(R.id.Thu);
        final RadioButton week5button = findViewById(R.id.Fri);
        final RadioButton week6button = findViewById(R.id.Sat);
        final RadioButton week7button = findViewById(R.id.Sun);

        final RadioButton session1button = findViewById(R.id.morning);
        final RadioButton session2button = findViewById(R.id.befornoon);
        final RadioButton session3button = findViewById(R.id.noon);
        final RadioButton session4button = findViewById(R.id.afternoon);
        final RadioButton session5button = findViewById(R.id.evening);
        final RadioButton session6button = findViewById(R.id.night);




        RadioGroup roomselect = findViewById(R.id.timer_set_room_radio);
        roomselect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == room1button.getId()) {
                    room=0;
                    //在内部类中使用room1name，需要把他们定义为 final 形式。！！
                    //Toast.makeText(timerSettingActivity.this, "你正在设置"+room1name, Toast.LENGTH_SHORT).show();
                    displayTimeTemperature();
                }
                if(checkedId == room2button.getId()) {
                    room=1;
                    displayTimeTemperature();
                }
                if(checkedId == room3button.getId()) {
                    room=2;
                    displayTimeTemperature();
                }
                if(checkedId == room4button.getId()) {
                    room=3;
                    displayTimeTemperature();
                }
                if(checkedId == room5button.getId()) {
                    room=4;
                    displayTimeTemperature();
                }
                if(checkedId == room6button.getId()) {
                    room=5;
                    displayTimeTemperature();
                }
                if(checkedId == room7button.getId()) {
                    room=6;
                    displayTimeTemperature();                }
                if(checkedId == room8button.getId()) {
                    room=7;
                    displayTimeTemperature();                }
            }
        });

        RadioGroup weekSelect = findViewById(R.id.timer_set_week_radio);
        weekSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == week1button.getId()) {
                    weekday = 0;
//                    Toast.makeText(timerSettingActivity.this, "设置周一时段", Toast.LENGTH_SHORT).show();
                    displayTimeTemperature();
                }
                if(checkedId == week2button.getId()) {
                    weekday = 1;
                    displayTimeTemperature();                }
                if(checkedId == week3button.getId()) {
                    weekday = 2;
                    displayTimeTemperature();                }
                if(checkedId == week4button.getId()) {
                    weekday = 3;
                    displayTimeTemperature();                }
                if(checkedId == week5button.getId()) {
                    weekday = 4;
                    displayTimeTemperature();                }
                if(checkedId == week6button.getId()) {
                    weekday = 5;
                    displayTimeTemperature();                }
                if(checkedId == week7button.getId()) {
                    weekday = 6;
                    displayTimeTemperature();                }
            }

        });

        RadioGroup sessionSelect = findViewById(R.id.timer_set_session_radio);
        sessionSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == session1button.getId()) {
                    session = 0;
                    displayTimeTemperature();
                }
                if (checkedId == session2button.getId()) {
                    session = 1;
                    displayTimeTemperature();
                }
                if (checkedId == session3button.getId()) {
                    session = 2;
                    displayTimeTemperature();
                }
                if (checkedId == session4button.getId()) {
                    session = 3;
                    displayTimeTemperature();
                }
                if (checkedId == session5button.getId()) {
                    session = 4;
                    displayTimeTemperature();
                }
                if (checkedId == session6button.getId()) {
                    session = 5;
                    displayTimeTemperature();
                }
            }
        });

        Button setStarttime = findViewById(R.id.set_starttime);
        Button setEndtime = findViewById(R.id.set_endtime);
        Button setTemperature = findViewById(R.id.set_temp);
        Button set_submit = findViewById(R.id.time_set_submit);
        Button set_upload = findViewById(R.id.timer_set_upload);

        displayStartTime = findViewById(R.id.starttime);
        displayStartTime.setText(valueOf(period[room][weekday][session*5] + ":" + valueOf(period[room][weekday][session*5+1])));
        displayEndTime = findViewById(R.id.endtime);
        displayEndTime.setText(valueOf(period[room][weekday][session*5+2] + ":" + valueOf(period[room][weekday][session*5+3])));
        displayTemperature = findViewById(R.id.temp);
        displayTemperature.setText(valueOf(period[room][weekday][session*5+4] ));


        setStarttime.setOnClickListener(this);
        setEndtime.setOnClickListener(this);
        setTemperature.setOnClickListener(this);
        set_submit.setOnClickListener(this);
        set_upload.setOnClickListener(this);

        serviceConnection = new MyServiceConnection();//新建连接服务的实例
        Intent intent = new Intent(this, MQTTService.class);//传递信息
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_starttime:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//新建构造器
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener(){//设置构造器的正向按钮 名称， 功能
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//参数：dialog发生点击的对话，which发生点击的按钮
                        if(hour < 10 && minute <10) {//这三个if用来给个位数的前面加上0。
                            displayStartTime.setText("0" + valueOf(hour) + ":" + "0" + valueOf(minute));
                        }else if (hour < 10) {
                            displayStartTime.setText("0"+valueOf(hour) + ":" + valueOf(minute));
                        }else if(minute < 10) {
                            displayStartTime.setText(valueOf(hour)+":"+"0"+valueOf(minute));
                        }else {
                            displayStartTime.setText(valueOf(hour) + ":" + valueOf(minute));
                        }
//                        displayTimeTemperature();
                        startHour = hour;
                        startMinute = minute;
                    }
                    });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);
                timePicker.setOnTimeChangedListener(this);
                dialog.setTitle("设置时间");
                dialog.setView(dialogView);
                dialog.show();
                break;
            case R.id.set_endtime:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setPositiveButton("设置", new DialogInterface.OnClickListener(){//这里规定了点击“设置”以后的处理程序
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(hour < 10 && minute <10) {
                            displayEndTime.setText("0" + valueOf(hour) + ":" + "0" + valueOf(minute));
                        }else if (hour < 10) {
                            displayEndTime.setText("0"+valueOf(hour) + ":" + valueOf(minute));
                        }else if(minute < 10) {
                            displayEndTime.setText(valueOf(hour)+":"+"0"+valueOf(minute));
                        }else {
                            displayEndTime.setText(valueOf(hour) + ":" + valueOf(minute));
                        }
//                        displayTimeTemperature();
                        endHour = hour;
                        endMinute = minute;
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2 = builder2.create();
                View dialogView2 = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker2 =  dialogView2.findViewById(R.id.timePicker);
                timePicker2.setIs24HourView(true);
                timePicker2.setOnTimeChangedListener(this);
                dialog2.setTitle("设置时间");
                dialog2.setView(dialogView2);
                dialog2.show();
                break;

            case R.id.set_temp:
                //实现一个数字选择器
                AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                builder3.setPositiveButton("设置", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayTemperature.setText(valueOf(temp) + "  C");
                                                               //这里必须要valueof（temp）否则会崩溃
                        temperature = temp;
                    }
                });
                builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog3 = builder3.create();
                View dialogView3 = View.inflate(context, R.layout.dialog_number, null);
                NumberPicker numberPicker = dialogView3.findViewById(R.id.temperaturePicker);
                numberPicker.setMaxValue(30);
                numberPicker.setMinValue(5);
                numberPicker.setValue(temp);
                numberPicker.setOnValueChangedListener(this);
                dialog3.setTitle("设置温度");
                dialog3.setView(dialogView3);
                dialog3.show();
                break;


            case R.id.time_set_submit:
                //实现period三维数组的修改
                period[room][weekday][session*5] = startHour;
                period[room][weekday][session*5+1] = startMinute;
                period[room][weekday][session*5+2] = endHour;
                period[room][weekday][session*5+3] = endMinute;
                period[room][weekday][session*5+4] = temperature;
                Toast.makeText(timerSettingActivity.this, "临时保存了本条数据",
                        Toast.LENGTH_SHORT).show();
                break;


            case R.id.timer_set_upload:
                //实现 各个房间的二维数组的发布Room1，Room2，。。。
                //把int三维数组转换为含有八个成员的字符串数组，一个成员对应一个房间。
                //TODO 最终是要用标准的JSON数据格式来实现传输，
                // TODO 目前的这一版实现的是JSON数组格式，不含对象（暂时舍弃）
//                int i, j, k;
//                for (i=0; i<8; i++) {
//                    periodString[i] = "[";
//                    for (j=0; j<7; j++) {
//                        periodString[i] = periodString[i].concat("[");
//                        for (k=0; k<30; k++) {
//                            periodString[i] = periodString[i].concat(String.valueOf(period[i][j][k])).concat(",");
//                        }
//                        periodString[i] = periodString[i].substring(0, periodString[i].length()-1);//去掉尾部的逗号
//                        periodString[i] = periodString[i].concat("]").concat(",");//加上 方括号和逗号
//                    }
//                    periodString[i] = periodString[i].substring(0, periodString[i].length()-1);/去掉尾部的逗号
//                    periodString[i] = periodString[i].concat("]");//加上 方括号
//                }
//                Toast.makeText(timerSettingActivity.this, periodString[0], Toast.LENGTH_SHORT).show();//  最后要去掉
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room1", periodString[0], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room2", periodString[1], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room3", periodString[2], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room4", periodString[3], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room5", periodString[4], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room6", periodString[5], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room7", periodString[6], 1, false);
//                MQTTService.publish("86518/JYCFGC/6-2-3401/Room8", periodString[7], 1, false);
                //TODO 参照上面在这里做一个全数字字符和逗号组成的，没有方括号，方便模块处理，
                //八个数组，每个数组210位，加逗号420位。
                int i, j, k;
                for (i=0; i<8; i++) {
                    periodString[i] = "";
                    for (j=0; j<7; j++) {
                        for (k=0; k<30; k++) {
                            periodString[i] = periodString[i].concat(String.valueOf(period[i][j][k])).concat(",");
                        }
                    }
                }
                //Toast.makeText(timerSettingActivity.this, periodString[0], Toast.LENGTH_SHORT).show();//最后要去掉
                //结尾加上标识符便于模块区分
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room1", periodString[0].concat("Room1peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room2", periodString[1].concat("Room2peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room3", periodString[2].concat("Room3peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room4", periodString[3].concat("Room4peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room5", periodString[4].concat("Room5peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room6", periodString[5].concat("Room6peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room7", periodString[6].concat("Room7peroid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room8", periodString[7].concat("Room8peroid"), 1, false);

                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("Room1peroid_S", periodString[0]);
                editor.putString("Room2peroid_S", periodString[1]);
                editor.putString("Room3peroid_S", periodString[2]);
                editor.putString("Room4peroid_S", periodString[3]);
                editor.putString("Room5peroid_S", periodString[4]);
                editor.putString("Room6peroid_S", periodString[5]);
                editor.putString("Room7peroid_S", periodString[6]);
                editor.putString("Room8peroid_S", periodString[7]);
                editor.apply();

        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        this.temp = newVal;
    }

    /****************************************************
    * 这个函数以后再写，把周期的字符串换为int数组。
    *private int[][] StringToInt(String str, int[][] arr){
    *
    * return arr;
    * }
    * **************************************************/

    /***************************************
    * 就是页面上的显示开始时间/结束时间/设置温度
    * *************************************/
    public void displayTimeTemperature(){
        if(period[room][weekday][session*5] < 10 && period[room][weekday][session*5+1] <10) {
            displayStartTime.setText("0" + valueOf(period[room][weekday][session*5]) + ":" + "0" + valueOf(period[room][weekday][session*5+1]));
        }else if (period[room][weekday][session*5] < 10) {
            displayStartTime.setText("0"+valueOf(period[room][weekday][session*5]) + ":" + valueOf(period[room][weekday][session*5+1]));
        }else if(period[room][weekday][session*5+1] < 10) {
            displayStartTime.setText(valueOf(period[room][weekday][session*5])+":"+"0"+valueOf(period[room][weekday][session*5+1]));
        }else {
            displayStartTime.setText(valueOf(period[room][weekday][session*5]) + ":" + valueOf(period[room][weekday][session*5+1]));
        }

        if(period[room][weekday][session*5+2] < 10 && period[room][weekday][session*5+3] <10) {//这三个if用来给个位数的前面加上0。
            displayEndTime.setText("0" + valueOf(period[room][weekday][session*5+2]) + ":" + "0" + valueOf(period[room][weekday][session*5+3]));
        }else if (period[room][weekday][session*5+2] < 10) {
            displayEndTime.setText("0"+valueOf(period[room][weekday][session*5+2]) + ":" + valueOf(period[room][weekday][session*5+3]));
        }else if(period[room][weekday][session*5+3] < 10) {
            displayEndTime.setText(valueOf(period[room][weekday][session*5+2])+":"+"0"+valueOf(period[room][weekday][session*5+3]));
        }else {
            displayEndTime.setText(valueOf(period[room][weekday][session*5+2]) + ":" + valueOf(period[room][weekday][session*5+3]));
        }

        //displayStartTime.setText(valueOf(period[room][weekday][session*5] + ":" + valueOf(period[room][weekday][session*5+1])));
        //displayEndTime.setText(valueOf(period[room][weekday][session*5+2] + ":" + valueOf(period[room][weekday][session*5+3])));
        displayTemperature.setText(valueOf(period[room][weekday][session*5+4] ));
    }

}
