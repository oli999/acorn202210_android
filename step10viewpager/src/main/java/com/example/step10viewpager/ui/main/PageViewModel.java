package com.example.step10viewpager.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    //수정 가능한 라이브 데이터
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    //읽기 전용 라이브 데이터
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from section: " + input;
        }
    });
    //수정 가능한 라이브 데이터를 update 하는 메소드
    public void setIndex(int index) {
        mIndex.setValue(index);
    }
    //읽기 전용 라이브 데이터를 리턴하는 메소드
    public LiveData<String> getText() {
        return mText;
    }
}