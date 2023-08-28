package com.apps2you.albaraka.ui.exception;

import androidx.annotation.DrawableRes;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.exception.NoInternetException;
import com.apps2you.albaraka.data.exception.RequestTimedOutException;
import com.apps2you.albaraka.data.exception.ServerException;

public final class ExceptionDrawableFactory {
    private ExceptionDrawableFactory() {
    }

    /**
     * @param e Exception to return its message
     * @return Drawable resource id (ex: R.drawable.image_warning)
     */
    public static @DrawableRes int getDrawableResOf(Exception e) {
        if (e instanceof ServerException) {
            return R.drawable.img_connection_timeout;
        } else if (e instanceof NoInternetException) {
            return R.drawable.img_no_internet;
        } else if (e instanceof RequestTimedOutException) {
            return R.drawable.img_connection_timeout;
        } else {
            return R.drawable.img_no_internet;
        }
    }
}
