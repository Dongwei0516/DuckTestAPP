package com.example.dongwei.testapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by dongwei on 2017/3/9.
 */

public class SetNumberPicker extends NumberPicker {
    public SetNumberPicker(Context context) {
        super(context);
    }

    public SetNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SetNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child){
        this.addView(child,null);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params){
        this.addView(child,-1,params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
        super.addView(child, index, params);
        setNumberPicker(child);
    }

    public void setNumberPicker(View view){
        if (view instanceof EditText){
            ((EditText) view).setTextColor(0xffff5000);
            ((EditText) view).setTextSize(18);
        }
    }


}
