package com.example.step25imagecapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.step25imagecapture.databinding.ActivityDetailBinding;
import com.example.step25imagecapture.util.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DetailActivity extends AppCompatActivity implements MyHttpUtil.RequestListener {

    ActivityDetailBinding binding;
    GalleryDto dto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityDetailBinding 객체의 참조값 얻어내기
        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        //화면 구성은 binding 객체를 활용해서 한다.
        setContentView(binding.getRoot());

        //DetailActivity 가 활성화 될때 전달 받은 Intent 객체의 참조값 얻어오기
        //GalleryListActivity 에서 생성한 Intent 객체이기 때문에
        // "dto" 라는 키값으로 GalleryDto 객체가 들어 있다.
        Intent intent=getIntent();
        dto=(GalleryDto)intent.getSerializableExtra("dto");

        //이미지 출력(Glide 를 활용)
        Glide.with(this)
                .load(dto.getImagePath())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imageView);
        //세부 정보 출력
        binding.writer.setText("writer:"+dto.getWriter());
        binding.caption.setText(dto.getCaption());
        binding.regdate.setText(dto.getRegdate());

        //삭제 버튼에 리스너 등록
        binding.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("삭제 하시겠습니까?")
                    .setPositiveButton("네", (dialog, which) -> {
                        //삭제할 갤러리 사진의 Primary Key 를 이용해서 삭제 작업을 진행한다.
                        int num=dto.getNum();

                        Map<String, String> map=new HashMap<>();
                        //삭제할 번호를 Map 에 담는다.
                        map.put("num", Integer.toString(num));
                        //삭제 요청하기
                        new MyHttpUtil(this).sendPostRequest(2,
                                AppConstants.BASE_URL+"/api/gallery/delete", map, this);
                    })
                    .setNegativeButton("아니요", null)
                    .create()
                    .show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //로그인 체크
        new MyHttpUtil(this).sendGetRequest(1,
                AppConstants.BASE_URL+"/music/logincheck", null, this);
    }

    @Override
    public void onSuccess(int requestId, String data) {
        switch (requestId){
            case 1://로그인 체크의 결과
                try{
                    //json 문자열을 이용해서 JSONObject 객체를 생성한다.
                    JSONObject obj=new JSONObject(data);
                    //로그인 여부
                    boolean isLogin=obj.getBoolean("isLogin");
                    if(isLogin){
                        //로그인된 아이디를 읽어와서
                        String id=obj.getString("id");
                        //갤러리 writer 와 비교해서 같으면 삭제 버튼을 보이게 한다.
                        if(id.equals(dto.getWriter())){
                            //삭제 버튼을 보이도록 한다.
                            binding.deleteBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }catch (JSONException je){
                    Log.e("onPostExecute()", je.getMessage());
                }
                break;
            case 2://삭제 요청에 대한 결과
                //data 는 {"isSuccess":true} 형식의 json 문자열이다. 필요하다면 여기서 사용하면 된다.

                //DetailActivity 를 종료시켜서 GalleryListActivity  가 다시 활성화 되도록 한다.
                finish();
                break;
        }
    }

    @Override
    public void onFail(int requestId, Map<String, Object> result) {
        //에러 메세지를 읽어와서
        String errMsg=(String)result.get("errMsg");
        switch (requestId){
            case 1:

                break;
            case 2:

                break;
        }
    }

}














