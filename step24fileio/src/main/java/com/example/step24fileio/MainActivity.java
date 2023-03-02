package com.example.step24fileio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText inputMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //필요한 UI 의 참조값 얻어오기
        inputMsg=findViewById(R.id.inputMsg);
        Button saveBtn=findViewById(R.id.saveBtn);
        //버튼에 리스너 등록
        saveBtn.setOnClickListener(this);

        Button saveBtn2=findViewById(R.id.saveBtn2);
        saveBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveBtn:
                saveToInternal2();
                break;
            case R.id.saveBtn2:

                saveToExternal();
                break;
        }
    }


    //외부 저장 장치에 저장하기
    public void saveToExternal(){
        //입력한 문자열을 읽어온다.
        String msg=inputMsg.getText().toString();
        //외부 저장 장치의 폴더를 가리키는 File 객체
        File externalDir=getExternalFilesDir(null);
        //해당 폴더의 절대경로를 얻어낸다.
        String absolutePath=externalDir.getAbsolutePath();
        //텍스트 파일을 만들기 위한 파일 객체 생성
        File file=new File(absolutePath+"/memo.txt");
        try{
            FileWriter fw=new FileWriter(file, true);
            fw.append(msg);
            fw.flush();
            fw.close();
        }catch (Exception e){
            Log.e("saveToExternal()", e.getMessage());
        }
    }

    //내부 저장 장치에 저장하기
    public void saveToInternal(){
        //입력한 문자열을 읽어온다.
        String msg=inputMsg.getText().toString();
        try {
            //파일을 저장하기 위한 디렉토리 만들기
            File dir=new File(getFilesDir(), "myDir");
            if(!dir.exists()){
                dir.mkdir();
            }
            //해당 디렉토리에 파일을 만들기 위한 File 객체
            File file=new File(dir, "memo.txt");
            FileWriter fw=new FileWriter(file, true);
            fw.append(msg+"\n");
            fw.flush();
            fw.close();
        }catch(Exception e){
            Log.e("saveToInternal()", e.getMessage());
        }
    }
    //내부 저장 장치에 저장하기
    public void saveToInternal2(){
        //입력한 문자열을 읽어온다.
        String msg=inputMsg.getText().toString();
        try {
            FileOutputStream fos=openFileOutput("memo2.txt", MODE_APPEND);
            PrintWriter pw=new PrintWriter(fos);
            pw.println(msg+"\n");
            pw.flush();
            pw.close();
        }catch(Exception e){
            Log.e("saveToInternal()", e.getMessage());
        }
    }
}





