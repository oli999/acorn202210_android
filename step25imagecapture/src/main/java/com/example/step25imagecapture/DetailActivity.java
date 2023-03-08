package com.example.step25imagecapture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //DetailActivity 가 활성화 될때 전달 받은 Intent 객체의 참조값 얻어오기
        //GalleryListActivity 에서 생성한 Intent 객체이기 때문에
        // "dto" 라는 키값으로 GalleryDto 객체가 들어 있다.
        Intent intent=getIntent();
        GalleryDto dto=(GalleryDto)intent.getSerializableExtra("dto");

    }
}