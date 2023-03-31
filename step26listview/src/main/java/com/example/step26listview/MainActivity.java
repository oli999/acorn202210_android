package com.example.step26listview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.step26listview.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;

    WearableRecyclerView recyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyView=binding.recyView;

        //아이템이 가운데 정렬되도록 설정
        recyView.setEdgeItemsCenteringEnabled(true);
        //레이아웃 메니저 설정정
        recyView.setLayoutManager(new WearableLinearLayoutManager(this));

        //출력할 Data (모델)
        List<String> names=new ArrayList<>();
        names.add("김구라");
        names.add("해골");
        names.add("원숭이");
        names.add("덩어리");
        names.add("친구1");
        names.add("친구2");
        names.add("친구3");
        names.add("친구4");
        names.add("친구5");
        //RecyclerView 에 연결할 아답타 객체 생성
        RecyAdapter adapter=new RecyAdapter(names);
        //RecyclerView 에 아답타 연결
        recyView.setAdapter(adapter);
        recyView.setHasFixedSize(true);
        recyView.setCircularScrollingGestureEnabled(true);

    }

}









