package com.example.step18login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.step18login.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //activity_main.xml 문서와 대응되는 데이터 type
    ActivityMainBinding binding;
    //session id 값을 영구 저장할 SharedPreferences
    SharedPreferences pref;
    //session id 값을 임시 저장할 필드
    String sessionId;
    //로그인된 id 값을 저장할 필드
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActivityMainBinding 객체의 참조값 얻어와서 필드에 저장
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        //화면 구성하기
        setContentView(binding.getRoot());
        //로그인 버튼을 누르면
        binding.login.setOnClickListener(v->{
            //로그인 액티비티로 이동한다.
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        //로그아웃 버튼을 누르면
        binding.logout.setOnClickListener(v->{
            //로그아웃 액티비티로 이동한다.
            Intent intent=new Intent(this, LogoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //SharedPreferences 객체의 참조값 얻어와서 필드에 저장하기
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        //저장된 session id 가 있는지 읽어와 본다 (없다면 기본값 은 빈 문자열)
        sessionId=pref.getString("sessionId", "");
        //로그인 했는지 여부를 요청해보기
        new LoginCheckTask().execute(AppContants.BASE_URL+"/logincheck");
    }

    //로그인 여부를 체크하는 작업을 할 비동기 task
    class LoginCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            //로그인 체크 url
            String requestUrl=strings[0];
            //서버가 http 요청에 대해서 응답하는 문자열을 누적할 객체
            StringBuilder builder=new StringBuilder();
            HttpURLConnection conn=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            boolean isLogin=false;
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
                            MainActivity.this.sessionId=sessionId;
                        }
                    }
                }
                //출력받은 문자열 전체 얻어내기
                JSONObject obj=new JSONObject(builder.toString());
                /*
                    {"isLogin":false} or {"isLogin":true, "id":"kimgura"}
                    서버에서 위와 같은 형식의 json 문자열을 응답할 예정이다.
                 */
                Log.d("서버가 응답한 문자열", builder.toString());
                //로그인 여부를 읽어와서
                isLogin=obj.getBoolean("isLogin");
                //만일 로그인을 했다면
                if(isLogin){
                    //필드에 로그인된 아이디를 담아둔다.
                    id=obj.getString("id");
                }
            }catch(Exception e){//예외가 발생하면
                Log.e("LoginCheckTask", e.getMessage());
            }finally {
                try{
                    if(isr!=null)isr.close();
                    if(br!=null)br.close();
                    if(conn!=null)conn.disconnect();
                }catch(Exception e){}
            }
            //로그인 여부를 리턴하면 아래의 onPostExecute() 메소드에 전달된다.
            return isLogin;
        }

        @Override
        protected void onPostExecute(Boolean isLogin) {
            super.onPostExecute(isLogin);
            //여기는 UI 스레드 이기 때문에 UI 와 관련된 작업을 할수 있다.
            //TextView 에 로그인 여부를 출력하기
            if(isLogin){
                binding.userInfo.setText(id+" 님 로그인중...");
            }else{
                binding.userInfo.setText("로그인이 필요 합니다.");
            }
        }
    }
}













