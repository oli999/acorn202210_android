package com.example.step21notification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //전달된 Intent 객체
        Intent intent=getIntent();
        //Intent 객체에 "msg" 라는 키값으로 전달된 문자열 얻어내기
        String msg=intent.getStringExtra("msg");
        //TextView 에 출력해 보기
        TextView textView=findViewById(R.id.textView);
        textView.setText(msg);
    }
}