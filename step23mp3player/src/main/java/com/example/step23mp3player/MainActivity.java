package com.example.step23mp3player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    MediaPlayer mp;
    //재생 준비가 되었는지 여부
    boolean isPrepared=false;
    ImageButton playBtn;
    ProgressBar progress;
    TextView time;
    SeekBar seek;
    //UI 를 주기적으로 업데이트 하기 위한 Handler
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentTime=mp.getCurrentPosition();
            //음악 재생이 시작된 이후에 주기적으로 계속 실행이 되어야 한다.
            progress.setProgress(currentTime);
            seek.setProgress(currentTime);
            //현재 재생 시간을 TextView 에 출력하기
            String info=String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(currentTime),
                    TimeUnit.MILLISECONDS.toSeconds(currentTime)
                    -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS. toMinutes(currentTime)) );
            time.setText(info);
            //자신의 객체에 다시 빈 메세제를 보내서 handleMessage() 가 일정시간 이후에 호출 되도록 한다.
            handler.sendEmptyMessageDelayed(0, 100); // 1/10 초 이후에
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TextView 의 참조값 얻어와서 필드에 저장
        time=findViewById(R.id.time);
        // %d 는 숫자, %s 문자
        String info=String.format("%d min, %d sec", 0, 0);
        time.setText(info);
        //ProgressBar 의 참조값 얻어오기
        progress=findViewById(R.id.progress);
        seek=findViewById(R.id.seek);

        //재생 버튼
        playBtn=findViewById(R.id.playBtn);
        //재생 버튼을 사용 불가 상태로 일단 만들어 놓고
        playBtn.setEnabled(false);
        playBtn.setOnClickListener(v->{
            //만일 준비 되지 않았으면
            if(!isPrepared){
                return;//메소드를 여기서 종료
            }

            mp.start();

        });
        //일시 중지 버튼
        ImageButton pauseBtn=findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(v->{
            mp.pause();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //음악을 재생할 준비를 한다.
        try {
            mp= new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource("http://192.168.0.31:9000/boot07/resources/upload/mp3piano.mp3");
            mp.setOnPreparedListener(this);
            //로딩하기
            mp.prepareAsync();
        }catch(Exception e){
            Log.e("MainActivity", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
        mp.release();
    }

    //재생할 준비가 끝나면 호출되는 메소드
    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(this, "로딩완료!", Toast.LENGTH_SHORT).show();
        isPrepared=true;
        playBtn.setEnabled(true);
        //전체 재생 시간을 ProgressBar 의 최대값으로 설정한다.
        progress.setMax(mp.getDuration());
        seek.setMax(mp.getDuration());
        Log.e("전체 시간", "duration:"+mp.getDuration());
        //Handler 객체에 메세지를 보내서 Handler 가 동작 되도록 한다.
        handler.sendEmptyMessageDelayed(0, 100);
    }
}







