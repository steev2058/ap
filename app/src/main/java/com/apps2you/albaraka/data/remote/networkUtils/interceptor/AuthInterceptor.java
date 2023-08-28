package com.apps2you.albaraka.data.remote.networkUtils.interceptor;

import android.util.Log;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.responseModel.RefreshTokenData;
import com.apps2you.albaraka.ui.common.busEvent.UnAuthorizedUserEvent;
import com.apps2you.albaraka.utils.bus.Bus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthInterceptor implements Interceptor {
    private final Gson gson = new Gson();

    @Inject
    public AuthInterceptor() {
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401) {
            try {
                Response refreshTokenResponse = proceedRefreshTokenRequest(chain);
                ResponseBody body = refreshTokenResponse.body();
                if (refreshTokenResponse.isSuccessful() && body != null) {
                    Log.d(getClass().getSimpleName(), body.string());
                    Type type = TypeToken.getParameterized(MyResponse.class, RefreshTokenData.class).getType();
                    MyResponse<RefreshTokenData> refreshTokenDataResponse = gson.fromJson(body.string(), type);
                    saveNewToken(refreshTokenDataResponse.getData());

                    return proceedRequestWithNewAccessToken(
                            chain,
                            chain.request(),
                            refreshTokenDataResponse.getData().getAccessToken()
                    );
                } else {
                    publishUnAuthorizedUserEvent();
                }
            } catch (Exception e) {
                e.printStackTrace();
                publishUnAuthorizedUserEvent();
            }
        }

        return response;
    }

    private Response proceedRefreshTokenRequest(Interceptor.Chain chain) throws IOException {
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
        String url = ApiService.getApiBaseURL() + "refresh_token";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("refresh_token", user.getRefreshToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request refreshTokenRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return chain.proceed(refreshTokenRequest);
    }

    private void saveNewToken(RefreshTokenData refreshData) {
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
        user.setAccessToken(refreshData.getAccessToken());
        user.setRefreshToken(refreshData.getRefreshToken());

        UserUtils.getInstance(MyApplication.getAppContext()).saveUser(user);
    }

    private Response proceedRequestWithNewAccessToken(
            Interceptor.Chain chain,
            Request request,
            String newToken
    ) throws IOException {
        Request newRequest = addTokenToRequest(request, newToken);
        return chain.proceed(newRequest);
    }

    private Request addTokenToRequest(Request request, String newToken) {
        return request
                .newBuilder()
                .addHeader("Authorization", "Bearer " + newToken)
                .build();
    }

    private void publishUnAuthorizedUserEvent() {
        Bus.instance().publish(UnAuthorizedUserEvent.getInstance());
    }
}
