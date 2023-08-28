package com.apps2you.albaraka.data.remote.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.AtmCard;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.responseModel.AtmServicesData;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class AtmServicesRepository {

    private final ApiService apiService;

    @Inject
    public AtmServicesRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<ArrayList<AtmCard>>> getAtmCards() {
        return new NetworkBoundResource<ArrayList<AtmCard>>() {
            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<AtmCard>>> createCall() {
                return apiService.getAtmCards();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<AtmServicesData>> getAtmServicesData() {
        return new NetworkBoundResource<AtmServicesData>() {

            @NonNull
            @Override
            protected Call<MyResponse<AtmServicesData>> createCall() {
                return apiService.getAtmServicesData();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> resendPinCode(String cardNumber, String pinCode) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.resendPinCode(cardNumber, pinCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> updateLimit(String cardNumber, String limit, String pinCode) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.updateLimit(cardNumber, limit, pinCode);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<String>> updateStatus(String cardNumber, boolean currentStatus, String pinCode) {
        return new NetworkBoundResource<String>() {
            @NonNull
            @Override
            protected Call<MyResponse<String>> createCall() {
                return apiService.updateStatus(cardNumber, currentStatus ? 1 : 0, pinCode);
            }
        }.getAsLiveServerData();
    }
}
