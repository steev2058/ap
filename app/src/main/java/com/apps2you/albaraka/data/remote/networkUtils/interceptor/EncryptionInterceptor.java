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

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EncryptionInterceptor implements Interceptor {
    private final String ENC_DATA = "encrypt_data";

    @Inject
    public EncryptionInterceptor() {
    }

    // this function encrypts the request parameters:
    // first we create json object of request parameters
    // next, we call toString on this json object and encrypt it
    // we put the encrypted string in ENC_DATA key and send it

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        JSONObject jsonParameters = getParametersAsJSON(request);

        if (jsonParameters.length() != 0) {
            try {
                String encParameters = SCEE.encryptString(jsonParameters.toString(), getEncKey());

                if (request.method().equals("POST"))
                    request = rebuildPostRequest(request, encParameters);
                else
                    request = rebuildGetRequest(request, encParameters);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        return chain.proceed(request);
    }

    private static String getEncKey() {
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
        return user == null ? Keys.INSTANCE.encryptionKey() : user.getAccessToken();
    }

    private JSONObject getParametersAsJSON(Request request) {
        final RequestBody originalBody = request.body();
        final HttpUrl originalUrl = request.url();

        final JSONObject jsonBody = new JSONObject();

        //** NOTE: @Path parameters are not encrypted
        if (originalBody instanceof FormBody) { //POST request with @Field parameters
            FormBody formBody = (FormBody) originalBody;

            for (int i = 0; i < formBody.size(); i++) {
                try {
                    jsonBody.put(formBody.name(i), formBody.value(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else { // GET request with @Query parameters
            for (int i = 0; i < originalUrl.querySize(); i++) {
                try {
                    jsonBody.put(originalUrl.queryParameterName(i), originalUrl.queryParameterValue(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonBody;
    }

    private Request rebuildPostRequest(Request request, String encBody) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ENC_DATA, encBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        long length = 0;

        try {
            length = body.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request.newBuilder()
                .header("Content-Type", body.contentType().toString())
                .header("Content-Length", String.valueOf(length))
                .method(request.method(), body)//this replaces the old form body
                .build();
    }

    private Request rebuildGetRequest(Request request, String encQuery) {
        String originalUrlString = request.url().toString();
        // remove the unencrypted parameters from the url
        String newUrlString = originalUrlString.substring(0, originalUrlString.indexOf('?'));

        HttpUrl newUrl = HttpUrl.parse(newUrlString).newBuilder()
                .addQueryParameter(ENC_DATA, encQuery)
                .build();

        return request.newBuilder().url(newUrl).build();
    }
}
