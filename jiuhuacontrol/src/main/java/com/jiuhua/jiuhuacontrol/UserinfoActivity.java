package com.jiuhua.jiuhuacontrol;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserinfoActivity extends AppCompatActivity {

    private String username;
    private String userphone;
    private String useraddress;
    private String room1name;
    private String room2name;
    private String room3name;
    private String room4name;
    private String room5name;
    private String room6name;
    private String room7name;
    private String room8name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "姓名");
        userphone = sharedPreferences.getString("userphone", "联系手机（必填）");
        useraddress = sharedPreferences.getString("useraddress","安装地址（很重要，必填）");
        room1name = sharedPreferences.getString("room1name","房间一名称");
        room2name = sharedPreferences.getString("room2name","房间二名称");
        room3name = sharedPreferences.getString("room3name","房间三名称");
        room4name = sharedPreferences.getString("room4name","房间四名称");
        room5name = sharedPreferences.getString("room5name","房间五名称");
        room6name = sharedPreferences.getString("room6name","房间六名称");
        room7name = sharedPreferences.getString("room7name","房间七名称");
        room8name = sharedPreferences.getString("room8name","室外");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        final EditText etusername = findViewById(R.id.user_name);
        final EditText etuserphone = findViewById(R.id.user_phone);
        final EditText etuseraddress = findViewById(R.id.user_address);
        final EditText etroom1name = findViewById(R.id.roomname1);
        final EditText etroom2name = findViewById(R.id.roomname2);
        final EditText etroom3name = findViewById(R.id.roomname3);
        final EditText etroom4name = findViewById(R.id.roomname4);
        final EditText etroom5name = findViewById(R.id.roomname5);
        final EditText etroom6name = findViewById(R.id.roomname6);
        final EditText etroom7name = findViewById(R.id.roomname7);
        final EditText etroom8name = findViewById(R.id.roomname8);

        etusername.setText(username);
        etuserphone.setText(userphone);
        etuseraddress.setText(useraddress);
        etroom1name.setText(room1name);
        etroom2name.setText(room2name);
        etroom3name.setText(room3name);
        etroom4name.setText(room4name);
        etroom5name.setText(room5name);
        etroom6name.setText(room6name);
        etroom7name.setText(room7name);
        etroom8name.setText(room8name);

        Button button = findViewById(R.id.submit_userinfo);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etusername.getText().toString();
                userphone = etuserphone.getText().toString();
                useraddress = etuseraddress.getText().toString();
                room1name = etroom1name.getText().toString();
                room2name = etroom2name.getText().toString();
                room3name = etroom3name.getText().toString();
                room4name = etroom4name.getText().toString();
                room5name = etroom5name.getText().toString();
                room6name = etroom6name.getText().toString();
                room7name = etroom7name.getText().toString();
                room8name = etroom8name.getText().toString();

                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("username", username);
                editor.putString("userphone",userphone);
                editor.putString("useraddress",useraddress);
                editor.putString("room1name", room1name);
                editor.putString("room2name", room2name);
                editor.putString("room3name", room3name);
                editor.putString("room4name", room4name);
                editor.putString("room5name", room5name);
                editor.putString("room6name", room6name);
                editor.putString("room7name", room7name);
                editor.putString("room8name", room8name);
                editor.apply();

                //TODO 在这里完成mqtt的topic的制作

                finish();

            }
        });

    }
}
