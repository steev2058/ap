package com.apps2you.albaraka.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;


public class PageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.85f;

    @Override
    public void transformPage(@NonNull View view, float position) {

        // set margin between pages
        float offset = -28 * position;
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL)
            view.setTranslationX(-offset);
        else
            view.setTranslationX(offset);

        // Scale the page down (between MIN_SCALE and 1)
        if (position <= 1) { // [-1,1]
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
}