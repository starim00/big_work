package com.example.starim.big_work;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by starim on 2018/1/3.
 */

public class OnDoubleClickListener implements View.OnTouchListener{
    private int count = 0;
    private long firstClick = 0;
    private long secondClick = 0;

    private final int duration = 1000;

    private DoubleClickCallBack mDoubleClickCallBack;

    public interface DoubleClickCallBack{
        void onDoubleClick();
    }

    public OnDoubleClickListener(DoubleClickCallBack doubleClickCallBack){
        mDoubleClickCallBack = doubleClickCallBack;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            count++;
            if(count == 1){
                firstClick = System.currentTimeMillis();
            }else if(count == 2){
                secondClick = System.currentTimeMillis();
                if(secondClick - firstClick < duration){
                    if(mDoubleClickCallBack!=null){
                        mDoubleClickCallBack.onDoubleClick();
                    }
                    count = 0;
                    firstClick = 0;
                    return true;
                }else{
                    firstClick = secondClick;
                    count = 1;
                }
                secondClick = 0;
            }
        }
        return false;
    }
}
