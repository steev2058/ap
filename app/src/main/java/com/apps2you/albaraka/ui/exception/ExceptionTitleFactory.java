package com.apps2you.albaraka.ui.exception;

import androidx.annotation.StringRes;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.exception.NoInternetException;
import com.apps2you.albaraka.data.exception.RequestTimedOutException;
import com.apps2you.albaraka.data.exception.ServerException;

public final class ExceptionTitleFactory {
    private ExceptionTitleFactory() {
    }

    /**
     * @param e Exception to return its message
     * @return String resource id (ex: R.string.network_error)
     */
    public static @StringRes int getStringResOf(Exception e) {
        if (e instanceof ServerException) {
            return R.string.unknown_error_title;
        } else if (e instanceof NoInternetException) {
            return R.string.no_internet_title;
        } else if (e instanceof RequestTimedOutException) {
            return R.string.connection_timeout_title;
        } else {
            return R.string.unknown_error_title;
        }
    }
}
