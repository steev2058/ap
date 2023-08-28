package com.apps2you.albaraka.data.remote.networkUtils.interceptor;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.BuildConfig;
import com.apps2you.albaraka.Keys;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.SCEE;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DecryptionInterceptor implements Interceptor {

    @Inject
    public DecryptionInterceptor() {
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        ResponseBody body = originalResponse.body();

        String result = "";
        if (body != null) {
            result = body.string();
            try {
                result = new JSONObject(result).getString("data");

                try {
                    result = SCEE.decryptString(result, getDecKey(originalResponse.code()));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return originalResponse.newBuilder()
                .body(ResponseBody.create(result, MediaType.parse("application/json")))
                .build();
    }

    private static String getDecKey(int responseCode) {
        return responseCode == 401 ? Keys.INSTANCE.encryptionKey() : getEncKey();
    }


    private static String getEncKey() {
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
        return user == null ? Keys.INSTANCE.encryptionKey() : user.getAccessToken();
    }
}
