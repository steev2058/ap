package com.apps2you.albaraka.data.remote.repository;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.QuickService;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.responseModel.HomeData;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserRepository {

    private final ApiService apiService;

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<User>> login(String number, String password, String fcm_token) {
        return new NetworkBoundResource<User>() {
            @NonNull
            @Override
            protected Call<MyResponse<User>> createCall() {
                return apiService.login(number, password, fcm_token);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> resetPassword(String currentPass, String newPass, String confirmNewPass) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.resetPassword(currentPass, newPass, confirmNewPass);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> resetPin(String currentPin, String newPin, String confirmNewPin, String otpCode) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.resetPin(currentPin, newPin, confirmNewPin, otpCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<HomeData>> getHomeData() {
        return new NetworkBoundResource<HomeData>() {
            @NonNull
            @Override
            protected Call<MyResponse<HomeData>> createCall() {
                return apiService.getHomeData();
            }
        }
                .getAsLiveServerData();
    }

    public NetworkBoundResource<ArrayList<Transaction>> getRecentTransactions() {
        return new NetworkBoundResource<ArrayList<Transaction>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Transaction>>> createCall() {
                return apiService.getRecentTransactions();
            }
        };
    }

    public LiveData<Resource<ArrayList<Account>>> getAccounts() {
        return new NetworkBoundResource<ArrayList<Account>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Account>>> createCall() {
                return apiService.getAccounts();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> logout() {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.logout();
            }
        }
                .getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<Transaction>>> getTransactions(String accountNumber, String accountName, String fromDate, String toDate) {
        return new NetworkBoundResource<ArrayList<Transaction>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Transaction>>> createCall() {
                return apiService.getTransactions(accountNumber, accountName, fromDate, toDate);
            }
        }.getAsLiveServerData();
    }

    public MediatorLiveData<ResponseBody> getStatementFile() {
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
        okHttpClient.hostnameVerifier((hostname, session) -> true);



        okHttpClient.connectTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.writeTimeout(30000, TimeUnit.MILLISECONDS);

        okHttpClient.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "*/*")
                    .addHeader("os", "android")
                    .addHeader("lang", UserUtils.getInstance(MyApplication.getAppContext()).getLanguage());

            User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
            if (user != null)
                requestBuilder.addHeader("Authorization", "Bearer " + user.getAccessToken());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.getApiBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Call<ResponseBody> call = service.getStatementFile();
        final MediatorLiveData<ResponseBody> resultServer = new MediatorLiveData<>();

        Runnable runnable = () -> call.enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            resultServer.setValue(response.body());
                        } else if (response.code() == 400 || response.code() == 401 || response.code() == 422) {
                            resultServer.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (!call.isCanceled()) {
                            resultServer.setValue(null);
                        }
                    }
                }
        );

        new Handler().postDelayed(runnable, 1000);

        return resultServer;
    }

    public MediatorLiveData<ResponseBody> getStatementFile2() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(30000, TimeUnit.MILLISECONDS);
        okHttpClient.writeTimeout(30000, TimeUnit.MILLISECONDS);
        // Skip SSL certificate validation for Android versions 6 or less
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            try {
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

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                okHttpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                okHttpClient.hostnameVerifier((hostname, session) -> true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        okHttpClient.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "*/*")
                    .addHeader("os", "android")
                    .addHeader("lang", UserUtils.getInstance(MyApplication.getAppContext()).getLanguage());

            User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
            if (user != null)
                requestBuilder.addHeader("Authorization", "Bearer " + user.getAccessToken());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.getApiBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Call<ResponseBody> call = service.getStatementFile();
        final MediatorLiveData<ResponseBody> resultServer = new MediatorLiveData<>();

        Runnable runnable = () -> call.enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            resultServer.setValue(response.body());
                        } else if (response.code() == 400 || response.code() == 401 || response.code() == 422) {
                            resultServer.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (!call.isCanceled()) {
                            resultServer.setValue(null);
                        }
                    }
                }
        );

        new Handler().postDelayed(runnable, 1000);

        return resultServer;
    }

    public LiveData<Resource<String>> changeLanguage(String lang) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.changeClientLanguage(lang);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> resetNotificationCounter() {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.resetNotificationCounter();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> turnNotifications(int status) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.turnNotifications(status);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<ArrayList<QuickService>>> getQuickServices() {
        return new NetworkBoundResource<ArrayList<QuickService>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<QuickService>>> createCall() {
                return apiService.getQuickServices();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> personalizeQuickServices(String personalizedServicesString) {

        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.
                        personalizeQuickServices(personalizedServicesString);

            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> checkPinCode(String pinCode) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {

                return apiService.checkPinCode(pinCode);
            }
        }.getAsLiveServerData();
    }
}
