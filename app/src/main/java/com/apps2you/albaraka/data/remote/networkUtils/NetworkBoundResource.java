package com.apps2you.albaraka.data.remote.networkUtils;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.BuildConfig;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.exception.NoInternetException;
import com.apps2you.albaraka.data.exception.RequestTimedOutException;
import com.apps2you.albaraka.data.exception.ServerException;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.interceptor.AuthInterceptor;
import com.apps2you.albaraka.data.remote.networkUtils.interceptor.DecryptionInterceptor;
import com.apps2you.albaraka.data.remote.networkUtils.interceptor.EncryptionInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class NetworkBoundResource<T> {

    private final MediatorLiveData<Resource<T>> resultServer = new MediatorLiveData<>();

    private Call<MyResponse<T>> call = null;


    @MainThread
    protected NetworkBoundResource() {
        setCall(createCall());
        setResultLoading();

        fetchFromNetwork();
    }

    public final LiveData<Resource<T>> getAsLiveServerData() {
        return resultServer;
    }

    public Call<MyResponse<T>> getCall() {
        return call;
    }

    public static void skipTrustedCertificateCheck() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String
                                authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("SSL");

            sc.init(null,  trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static OkHttpClient provideOkHttpClient(

    ) {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }


        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        okHttpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        okHttpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });





        okHttpClient.connectTimeout(60000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(60000, TimeUnit.MILLISECONDS);
        okHttpClient.writeTimeout(60000, TimeUnit.MILLISECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            okHttpClient.addInterceptor(interceptor);
        }
        return okHttpClient.build();
    }

    public void setCall(Call<MyResponse<T>> call) {
        this.call = call;
    }

    @NonNull
    @MainThread
    protected abstract Call<MyResponse<T>> createCall();

    private void fetchFromNetwork() {

        Runnable runnable = ()->
            getCall().enqueue(new Callback<MyResponse<T>>() {
                @Override
                public void onResponse(@NonNull Call<MyResponse<T>> call, @NonNull Response<MyResponse<T>> response) {
                    if (response.body() != null && response.isSuccessful()) {

                        resultServer.setValue(Resource.success(response.body().data, response.body().message));

                    } else if (response.code() == 400 || response.code() == 422) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody() != null ? response.errorBody().string() : "");
                            String message = jObjError.getString("message");
                            resultServer.setValue(Resource.error(message, null, response.code(), new Exception(message)));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            resultServer.setValue(Resource.error(MyApplication.getAppContext().getString(R.string.error_occurred), null, response.code(), new ServerException()));
                        }
                    } else if (response.code() == 401) {
                        // we don't have to setValue for resultServer when unAuthorized error
                        // it's handled by UnAuthorizedUserEvent & AuthInterceptor
                    } else {
                        resultServer.setValue(Resource.error(MyApplication.getAppContext().getString(R.string.error_occurred), null, response.code(), new ServerException()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MyResponse<T>> call, @NonNull Throwable t) {
                    if (!getCall().isCanceled()) {
                        if (t instanceof SocketTimeoutException) {
                            resultServer.setValue(Resource.error(MyApplication.getAppContext().getString(R.string.error_connection), null, 0, new RequestTimedOutException()));
                        } else if (t instanceof UnknownHostException) {
                            resultServer.setValue(Resource.error(MyApplication.getAppContext().getString(R.string.error_connection), null, 0, new NoInternetException()));
                        } else {
                            resultServer.setValue(Resource.error(MyApplication.getAppContext().getString(R.string.error_connection), null, 0, new ServerException()));
                        }
                    }
                }
            });

        new Handler().post(runnable);
    }

    private void setResultLoading() {
        resultServer.setValue(Resource.loading(null));
    }
}
