package com.example.step25imagecapture;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.example.step25imagecapture.databinding.ActivityGalleryListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GalleryListActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityGalleryListBinding binding;
    //서버에서 받아온 갤러리 목록을 저장할 객체
    List<GalleryDto> list=new ArrayList<>();
    String sessionId;
    SharedPreferences pref;
    GalleryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 객체의 참조값을 필드에 저장
        binding=ActivityGalleryListBinding.inflate(getLayoutInflater());
        //바인딩 객체를 이용해서 화면 구성
        setContentView(binding.getRoot());

        //ListView 에 연결할 아답타 객체 생성
        adapter=new GalleryAdapter(this, R.layout.listview_cell, list);
        //ListView 에 아답타 연결하기
        binding.listView.setAdapter(adapter);
        //버튼에 리스너 등록하기
        binding.takePicBtn.setOnClickListener(this);
        binding.refreshBtn.setOnClickListener(this);
        //ListView 에 cell 을 클릭했을때 동작할 리스너 등록
        binding.listView.setOnItemClickListener((parent, view, position, id) -> {
            //position 은 클릭한 cell 의 인덱스 값이다.
            GalleryDto dto=list.get(position); //자세히 보여줄 GalleryDto 정보
            Intent intent=new Intent(this, DetailActivity.class);
            intent.putExtra("dto", dto);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        sessionId=pref.getString("sessionId", "");

        //원격지 서버로 부터 갤러리 목록을 받아오는 요청을 한다.
        new GalleryListTask().execute(AppConstants.BASE_URL+"/api/gallery/list");
    }

    //버튼을 눌렀을때 호출되는 메소드
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takePicBtn:
                //사진을 찍어서 올리는 액티비티를 실행한다.
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.refreshBtn:
                //목록을 다시 받아온다.
                new GalleryListTask().execute(AppConstants.BASE_URL+"/api/gallery/list");
                break;
        }
    }
    //갤러리 목록을 얻어올 작업을 할 비동기 task
    class GalleryListTask extends AsyncTask<String, Void, String> {
        //진행중 알림을 띄우기 위한 객체
        ProgressDialog progress=new ProgressDialog(GalleryListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //진행중 알림을 띄운다.
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage("다운로드중...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //요청 url
            String requestUrl=strings[0];
            //서버가 http 요청에 대해서 응답하는 문자열을 누적할 객체
            StringBuilder builder=new StringBuilder();
            HttpURLConnection conn=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            try{
                //URL 객체 생성
                URL url=new URL(requestUrl);
                //HttpURLConnection 객체의 참조값 얻어오기
                conn=(HttpURLConnection)url.openConnection();
                if(conn!=null){//연결이 되었다면
                    conn.setConnectTimeout(20000); //응답을 기다리는 최대 대기 시간
                    conn.setRequestMethod("GET");//Default 설정
                    conn.setUseCaches(false);//케쉬 사용 여부
                    //App 에 저장된 session id 가 있다면 요청할때 쿠키로 같이 보내기
                    if(!sessionId.equals("")) {
                        // JSESSIONID=xxx 형식의 문자열을 쿠키로 보내기
                        conn.setRequestProperty("Cookie", sessionId);
                    }

                    //응답 코드를 읽어온다.
                    int responseCode=conn.getResponseCode();

                    if(responseCode==200){//정상 응답이라면...
                        //서버가 출력하는 문자열을 읽어오기 위한 객체
                        isr=new InputStreamReader(conn.getInputStream());
                        br=new BufferedReader(isr);
                        //반복문 돌면서 읽어오기
                        while(true){
                            //한줄씩 읽어들인다.
                            String line=br.readLine();
                            //더이상 읽어올 문자열이 없으면 반복문 탈출
                            if(line==null)break;
                            //읽어온 문자열 누적 시키기
                            builder.append(line);
                        }
                    }
                }
                //서버가 응답한 쿠키 목록을 읽어온다.
                List<String> cookList=conn.getHeaderFields().get("Set-Cookie");
                //만일 쿠키가 존대 한다면
                if(cookList != null){
                    //반복문 돌면서
                    for(String tmp : cookList){
                        //session id 가 들어 있는 쿠키를 찾아내서
                        if(tmp.contains("JSESSIONID")){
                            //session id 만 추출해서
                            String sessionId=tmp.split(";")[0];
                            //SharedPreferences 을 편집할수 있는 객체를 활용해서
                            SharedPreferences.Editor editor=pref.edit();
                            //sessionId 라는 키값으로 session id 값을 저장한다.
                            editor.putString("sessionId", sessionId);
                            editor.apply();//apply() 는 비동기로 저장하기 때문에 실행의 흐름이 잡혀 있지 않다(지연이 없음)
                            //필드에도 담아둔다.
                            GalleryListActivity.this.sessionId=sessionId;
                        }
                    }
                }

            }catch(Exception e){//예외가 발생하면
                Log.e("MusicListTask", e.getMessage());
            }finally {
                try{
                    if(isr!=null)isr.close();
                    if(br!=null)br.close();
                    if(conn!=null)conn.disconnect();
                }catch(Exception e){}
            }
            //응답받은 문자열을 리턴한다.
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            //여기는 UI 스레드
            // jsonStr 은 [{},{},...]  형식의 문자열이기 때문에 JSONArray 객체를 생성한다.
            list.clear();
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for(int i=0; i<arr.length(); i++){
                    // i 번째 JSONObject 객체를 참조
                    JSONObject tmp=arr.getJSONObject(i);
                    int num=tmp.getInt("num");
                    String writer=tmp.getString("writer");
                    String caption=tmp.getString("caption");
                    String imagePath=tmp.getString("imagePath");
                    String regdate=tmp.getString("regdate");
                    GalleryDto dto=new GalleryDto();
                    dto.setNum(num);
                    dto.setWriter(writer);
                    dto.setCaption(caption);
                    //http://xxx/xxx/resources/upload/xxx.jpg 형식의 문자열 구성해서 넣기
                    dto.setImagePath(AppConstants.BASE_URL+imagePath);
                    dto.setRegdate(regdate);
                    //ArrayList 객체에 누적 시키기
                    list.add(dto);
                }
                //모델이 바뀌었다고 아답타에 알려서 ListView 가 업데이트 되도록 한다.
                adapter.notifyDataSetChanged();
            }catch (JSONException je){
                Log.e("onPoseExecute()", je.getMessage());
            }
            //프로그래스 취소
            progress.dismiss();
        }
    }
}












