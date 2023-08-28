package com.apps2you.albaraka.ui.exception;

import androidx.annotation.StringRes;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.exception.NoInternetException;
import com.apps2you.albaraka.data.exception.RequestTimedOutException;
import com.apps2you.albaraka.data.exception.ServerException;

public final class ExceptionMessageFactory {
    private ExceptionMessageFactory() {
    }

    /**
     * @param e Exception to return its message
     * @return String resource id (ex: R.string.network_error)
     */
    public static @StringRes
    int getStringResOf(Exception e) {
        if (e instanceof ServerException) {
            return R.string.unknown_error_message;
        } else if (e instanceof NoInternetException) {
            return R.string.no_internet_message;
        } else if (e instanceof RequestTimedOutException) {
            return R.string.connection_timeout_message;
        } else {
            return R.string.unknown_error_message;
        }
    }
}
