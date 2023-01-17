package com.example.step07fragment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMsg(String msg){
        //프레그먼트로 부터 전달된 문자열을 토스트 메세지로 출력하기
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}