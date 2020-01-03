package com.jiuhua.jiuhuaheatpumpsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuhua.mqttsample.MQTTService;
import com.jiuhua.mqttsample.MyServiceConnection;
import java.util.StringTokenizer;
import static java.lang.String.valueOf;

public class SetPeroidActivity extends AppCompatActivity implements View.OnClickListener {
    private MyServiceConnection serviceConnection;//连接实例

    String roomName = "";
    String roomID = "";
    private int weekday = 0;
    private int session = 0;
    private int startHour, startMinute, endHour, endMinute, setTemperature;
    private String summerRoomPeriod_S;
    private String winterRoomPeriod_S;
    private int[][] summerPeriod = new int[7][30];
    private int[][] winterPeriod = new int[7][30];

    private String [] displayStartTime = new String[6];
    private String [] displayEndTime = new String[6];
    private String [] displayTemperature= new String[6];

    private RadioButton periodBtn1;
    private RadioButton periodBtn2;
    private RadioButton periodBtn3;
    private RadioButton periodBtn4;
    private RadioButton periodBtn5;
    private RadioButton periodBtn6;
    private RadioButton periodBtn7;
    private RadioButton periodBtn8;
    private RadioButton periodBtn9;
    private RadioButton periodBtn10;
    private RadioButton periodBtn11;
    private RadioButton periodBtn12;

    private NumberPicker startHourPicker;
    private NumberPicker startMinutePicker;
    private NumberPicker endHourPicker;
    private NumberPicker endMinutePicker;
    private NumberPicker setTemperaturePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //这个放在开始的部分，利用线程，不耽误其他线程
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        //从上一个activity传入房间名称和房间ID
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomname");  //RoomActivity传过来的点击的哪一个房间名
        roomID = intent.getStringExtra("roomid");//传一个标识

        //读取夏季和冬季周期的字符串
        int i, j;
        summerRoomPeriod_S = sharedPreferences.getString("Room"+roomID+"summerperoid_S", "");
        StringTokenizer tokenizer0 = new StringTokenizer(summerRoomPeriod_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer0.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，在执行就会报错了
                    summerPeriod[i][j] = Integer.valueOf(tokenizer0.nextToken());
                }
            }
        }
        winterRoomPeriod_S = sharedPreferences.getString("Room"+roomID+"winterperoid_S", "");
        StringTokenizer tokenizer1 = new StringTokenizer(winterRoomPeriod_S, ",");
        for(i=0;i<7;i++) {
            for(j=0;j<30;j++) {
                if (tokenizer1.hasMoreTokens()) {//这里需要判断一下，如果没有元素了，再执行就会报错了
                    winterPeriod[i][j] = Integer.valueOf(tokenizer1.nextToken());
                }
            }
        }

        //设置周一的夏季第一时段为最初的显示数据，session当前为0
        startHour = summerPeriod[weekday][session*5];
        startMinute = summerPeriod[weekday][session*5+1];
        endHour = summerPeriod[weekday][session*5+2];
        endMinute = summerPeriod[weekday][session*5+3];
        setTemperature = summerPeriod[weekday][session*5+4];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_peroid);

        TextView tvRoomName = findViewById(R.id.nameofroom);
        tvRoomName.setText(roomName);

        Button confirmBtn = findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(this);
        Button uploadBtn = findViewById(R.id.upload);
        uploadBtn.setOnClickListener(this);


        periodBtn1 = findViewById(R.id.period1);
        periodBtn2 = findViewById(R.id.period2);
        periodBtn3 = findViewById(R.id.period3);
        periodBtn4 = findViewById(R.id.period4);
        periodBtn5 = findViewById(R.id.period5);
        periodBtn6 = findViewById(R.id.period6);
        periodBtn7 = findViewById(R.id.period7);
        periodBtn8 = findViewById(R.id.period8);
        periodBtn9 = findViewById(R.id.period9);
        periodBtn10 = findViewById(R.id.period10);
        periodBtn11 = findViewById(R.id.period11);
        periodBtn12 = findViewById(R.id.period12);


        final RadioButton week1button = findViewById(R.id.mon);
        final RadioButton week2button = findViewById(R.id.tue);
        final RadioButton week3button = findViewById(R.id.wed);
        final RadioButton week4button = findViewById(R.id.thu);
        final RadioButton week5button = findViewById(R.id.fri);
        final RadioButton week6button = findViewById(R.id.sat);
        final RadioButton week7button = findViewById(R.id.sun);

        RadioGroup weekSelect = findViewById(R.id.weekradio);
        weekSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == week1button.getId()) {
                    weekday = 0;
                    Toast.makeText(SetPeroidActivity.this, "设置周一时段", Toast.LENGTH_SHORT).show();
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week2button.getId()) {
                    weekday = 1;
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week3button.getId()) {
                    weekday = 2;
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week4button.getId()) {
                    weekday = 3;
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week5button.getId()) {
                    weekday = 4;
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week6button.getId()) {
                    weekday = 5;
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == week7button.getId()) {
                    weekday = 6;
                    displayTimeTemperature();
                    pickerDisplay();
                }
            }

        });

        RadioGroup sessionSelect = findViewById(R.id.sessionradio);
        sessionSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == periodBtn1.getId()) {
                    session = 0;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];
                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn2.getId()) {
                    session = 1;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn3.getId()) {
                    session = 2;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn4.getId()) {
                    session = 3;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn5.getId()) {
                    session = 4;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn6.getId()) {
                    session = 5;

                    startHour = summerPeriod[weekday][session*5];
                    startMinute = summerPeriod[weekday][session*5+1];
                    endHour = summerPeriod[weekday][session*5+2];
                    endMinute = summerPeriod[weekday][session*5+3];
                    setTemperature = summerPeriod[weekday][session*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn7.getId()) {
                    session = 6;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn8.getId()) {
                    session = 7;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn9.getId()) {
                    session = 8;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn10.getId()) {
                    session = 9;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn11.getId()) {
                    session = 10;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
                if(checkedId == periodBtn12.getId()) {
                    session = 11;

                    startHour = winterPeriod[weekday][(session-6)*5];
                    startMinute = winterPeriod[weekday][(session-6)*5+1];
                    endHour = winterPeriod[weekday][(session-6)*5+2];
                    endMinute = winterPeriod[weekday][(session-6)*5+3];
                    setTemperature = winterPeriod[weekday][(session-6)*5+4];

                    displayTimeTemperature();
                    pickerDisplay();
                }
            }
        });

        startHourPicker = findViewById(R.id.startHourPicker);
        startHourPicker.setMaxValue(23);
        startHourPicker.setMinValue(0);
        startHourPicker.setValue(startHour);
        startHourPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        startHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                startHour = newVal;
            }
        });

        startMinutePicker = findViewById(R.id.startMinutePicker);
        startMinutePicker.setMaxValue(59);
        startMinutePicker.setMinValue(0);
        startMinutePicker.setValue(startMinute);
        startMinutePicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        startMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                startMinute = newVal;
            }
        });

        endHourPicker = findViewById(R.id.endHourPicker);
        endHourPicker.setMaxValue(23);
        endHourPicker.setMinValue(0);
        endHourPicker.setValue(endHour);
        endHourPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        endHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                endHour = newVal;
            }
        });

        endMinutePicker = findViewById(R.id.endMinutePicker);
        endMinutePicker.setMaxValue(59);
        endMinutePicker.setMinValue(0);
        endMinutePicker.setValue(endMinute);
        endMinutePicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        endMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                endMinute = newVal;
            }
        });

        setTemperaturePicker = findViewById(R.id.setTemperaturePicker);
        setTemperaturePicker.setMaxValue(35);
        setTemperaturePicker.setMinValue(5);
        setTemperaturePicker.setValue(setTemperature);
        setTemperaturePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setTemperature = newVal;
            }
        });

        serviceConnection = new MyServiceConnection();//新建连接服务的实例
        Intent intentS = new Intent(this, MQTTService.class);//传递信息
        bindService(intentS, serviceConnection, Context.BIND_AUTO_CREATE);//绑定服务

        displayTimeTemperature();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                //实现period二维数组的修改
                if (session < 6) {
                    summerPeriod[weekday][session*5] = startHour;
                    summerPeriod[weekday][session*5+1] = startMinute;
                    summerPeriod[weekday][session*5+2] = endHour;
                    summerPeriod[weekday][session*5+3] = endMinute;
                    summerPeriod[weekday][session*5+4] = setTemperature;
                } else {
                    winterPeriod[weekday][(session-6)*5] = startHour;
                    winterPeriod[weekday][(session-6)*5+1] = startMinute;
                    winterPeriod[weekday][(session-6)*5+2] = endHour;
                    winterPeriod[weekday][(session-6)*5+3] = endMinute;
                    winterPeriod[weekday][(session-6)*5+4] = setTemperature;
                }
                Toast.makeText(SetPeroidActivity.this, "临时保存了本条数据", Toast.LENGTH_SHORT).show();
                displayTimeTemperature();
                break;
            case R.id.upload:
                //TODO 参照上面在这里做一个全数字字符和逗号组成的，没有方括号，方便模块处理，
                //每个数组210位，加逗号420位。
                int j, k;
                summerRoomPeriod_S = "";
                    for (j=0; j<7; j++) {
                        for (k=0; k<30; k++) {
                            summerRoomPeriod_S = summerRoomPeriod_S.concat(String.valueOf(summerPeriod[j][k])).concat(",");
                        }
                }
                winterRoomPeriod_S = "";
                for (j=0; j<7; j++) {
                    for (k=0; k<30; k++) {
                        winterRoomPeriod_S = winterRoomPeriod_S.concat(String.valueOf(winterPeriod[j][k])).concat(",");
                    }
                }

                //结尾加上标识符便于模块区分
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID, summerRoomPeriod_S.concat("Room"+roomID+"summerperoid"), 1, false);
                MQTTService.publish("86518/JYCFGC/6-2-3401/Room"+roomID, winterRoomPeriod_S.concat("Room"+roomID+"winterperoid"), 1, false);

                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("Room"+roomID+"summerperoid_S", summerRoomPeriod_S);
                editor.putString("Room"+roomID+"winterperoid_S", winterRoomPeriod_S);
                editor.apply();

                displayTimeTemperature();

                Toast.makeText(SetPeroidActivity.this, "上传夏季周期", Toast.LENGTH_SHORT).show();//最后要去掉
                Toast.makeText(SetPeroidActivity.this, "上传冬季周期", Toast.LENGTH_SHORT).show();//最后要去掉

                finish();
                break;

        }
    }


    /***************************************
     * 就是在每个时段条上的显示开始时间/结束时间/设置温度
     * *************************************/
    @SuppressLint("SetTextI18n")
    public void displayTimeTemperature(){
        int i = 0;
        for (i=0;i<6;i++) {
            if(summerPeriod[weekday][i*5] < 10 && summerPeriod[weekday][i*5+1] <10) {
                displayStartTime[i] = ("0" + valueOf(summerPeriod[weekday][i*5]) + ":" + "0" + valueOf(summerPeriod[weekday][i*5+1]));
            }else if (summerPeriod[weekday][i*5] < 10) {
                displayStartTime[i] = ("0"+valueOf(summerPeriod[weekday][i*5]) + ":" + valueOf(summerPeriod[weekday][i*5+1]));
            }else if(summerPeriod[weekday][i*5+1] < 10) {
                displayStartTime[i] = (valueOf(summerPeriod[weekday][i*5])+":"+"0"+valueOf(summerPeriod[weekday][i*5+1]));
            }else {
                displayStartTime[i] = (valueOf(summerPeriod[weekday][i*5]) + ":" + valueOf(summerPeriod[weekday][i*5+1]));
            }

            if(summerPeriod[weekday][i*5+2] < 10 && summerPeriod[weekday][i*5+3] <10) {//这三个if用来给个位数的前面加上0。
                displayEndTime[i] = ("0" + valueOf(summerPeriod[weekday][i*5+2]) + ":" + "0" + valueOf(summerPeriod[weekday][i*5+3]));
            }else if (summerPeriod[weekday][i*5+2] < 10) {
                displayEndTime[i] = ("0"+valueOf(summerPeriod[weekday][i*5+2]) + ":" + valueOf(summerPeriod[weekday][i*5+3]));
            }else if(summerPeriod[weekday][i*5+3] < 10) {
                displayEndTime[i] = (valueOf(summerPeriod[weekday][i*5+2])+":"+"0"+valueOf(summerPeriod[weekday][i*5+3]));
            }else {
                displayEndTime[i] = (valueOf(summerPeriod[weekday][i*5+2]) + ":" + valueOf(summerPeriod[weekday][i*5+3]));
            }

            displayTemperature[i] = (valueOf(summerPeriod[weekday][i*5+4] ));
        }

        periodBtn1.setText("夏季      " + displayStartTime[0] + "---" + displayEndTime[0] + "      " + displayTemperature[0] + " C");
        periodBtn2.setText("夏季      " + displayStartTime[1] + "---" + displayEndTime[1] + "      " + displayTemperature[1] + " C");
        periodBtn3.setText("夏季      " + displayStartTime[2] + "---" + displayEndTime[2] + "      " + displayTemperature[2] + " C");
        periodBtn4.setText("夏季      " + displayStartTime[3] + "---" + displayEndTime[3] + "      " + displayTemperature[3] + " C");
        periodBtn5.setText("夏季      " + displayStartTime[4] + "---" + displayEndTime[4] + "      " + displayTemperature[4] + " C");
        periodBtn6.setText("夏季      " + displayStartTime[5] + "---" + displayEndTime[5] + "      " + displayTemperature[5] + " C");

        for (i=0;i<6;i++) {
            if(winterPeriod[weekday][i*5] < 10 && winterPeriod[weekday][i*5+1] <10) {
                displayStartTime[i] = ("0" + valueOf(winterPeriod[weekday][i*5]) + ":" + "0" + valueOf(winterPeriod[weekday][i*5+1]));
            }else if (winterPeriod[weekday][i*5] < 10) {
                displayStartTime[i] = ("0"+valueOf(winterPeriod[weekday][i*5]) + ":" + valueOf(winterPeriod[weekday][i*5+1]));
            }else if(winterPeriod[weekday][i*5+1] < 10) {
                displayStartTime[i] = (valueOf(winterPeriod[weekday][i*5])+":"+"0"+valueOf(winterPeriod[weekday][i*5+1]));
            }else {
                displayStartTime[i] = (valueOf(winterPeriod[weekday][i*5]) + ":" + valueOf(winterPeriod[weekday][i*5+1]));
            }

            if(winterPeriod[weekday][i*5+2] < 10 && winterPeriod[weekday][i*5+3] <10) {//这三个if用来给个位数的前面加上0。
                displayEndTime[i] = ("0" + valueOf(winterPeriod[weekday][i*5+2]) + ":" + "0" + valueOf(winterPeriod[weekday][i*5+3]));
            }else if (winterPeriod[weekday][i*5+2] < 10) {
                displayEndTime[i] = ("0"+valueOf(winterPeriod[weekday][i*5+2]) + ":" + valueOf(winterPeriod[weekday][i*5+3]));
            }else if(winterPeriod[weekday][i*5+3] < 10) {
                displayEndTime[i] = (valueOf(winterPeriod[weekday][i*5+2])+":"+"0"+valueOf(winterPeriod[weekday][i*5+3]));
            }else {
                displayEndTime[i] = (valueOf(winterPeriod[weekday][i*5+2]) + ":" + valueOf(winterPeriod[weekday][i*5+3]));
            }

            displayTemperature[i] = (valueOf(winterPeriod[weekday][i*5+4] ));
        }

        periodBtn7.setText("冬季      " + displayStartTime[0] + "---" + displayEndTime[0] + "      " + displayTemperature[0] + " C");
        periodBtn8.setText("冬季      " + displayStartTime[1] + "---" + displayEndTime[1] + "      " + displayTemperature[1] + " C");
        periodBtn9.setText("冬季      " + displayStartTime[2] + "---" + displayEndTime[2] + "      " + displayTemperature[2] + " C");
        periodBtn10.setText("冬季      " + displayStartTime[3] + "---" + displayEndTime[3] + "      " + displayTemperature[3] + " C");
        periodBtn11.setText("冬季      " + displayStartTime[4] + "---" + displayEndTime[4] + "      " + displayTemperature[4] + " C");
        periodBtn12.setText("冬季      " + displayStartTime[5] + "---" + displayEndTime[5] + "      " + displayTemperature[5] + " C");


    }
    
    public void pickerDisplay() {
        startHourPicker.setValue(startHour);
        startMinutePicker.setValue(startMinute);
        endHourPicker.setValue(endHour);
        endMinutePicker.setValue(endMinute);
        setTemperaturePicker.setValue(setTemperature);
    }

}
