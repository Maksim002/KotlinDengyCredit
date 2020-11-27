package com.example.kotlincashloan.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class DeactivatableViewPager extends ViewPager {
    public DeactivatableViewPager(Context context) {
        super(context);
    }

    public DeactivatableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isEnabled() || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isEnabled() && super.onInterceptTouchEvent(event);
    }
}
