package com.apps2you.albaraka.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

// Determine the distance difference of the movement of the Y axis of the X axis to determine whether it is necessary to intercept the event
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    // The X coordinate of the last touch
    private float mPreDownX;
    private float mPreDownY;
    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreDownX = ev.getX();
                mPreDownY = ev.getY();
                break;


            case MotionEvent.ACTION_MOVE:
                final float eventX = ev.getX();
                final float eventY = ev.getY();
                float xAbs = Math.abs(eventX - mPreDownX);
                float yAbs = Math.abs(eventY - mPreDownY);
                // If the distance moved by the X axis is greater than the distance moved by the Y axis
                // Then don't intercept the touch event and hand it to the following processing
                if (xAbs > yAbs) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
