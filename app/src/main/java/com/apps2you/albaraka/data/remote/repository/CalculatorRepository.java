package com.apps2you.albaraka.data.remote.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.Currency;
import com.apps2you.albaraka.data.model.DepositResult;
import com.apps2you.albaraka.data.model.FinancingResult;
import com.apps2you.albaraka.data.model.FinancingType;
import com.apps2you.albaraka.data.model.ProfitsResult;
import com.apps2you.albaraka.data.remote.networkUtils.ApiService;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.responseModel.CalculatorData;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class CalculatorRepository {

    private final ApiService apiService;

    @Inject
    public CalculatorRepository(ApiService apiService) {
        this.apiService = apiService;
    }


    public LiveData<Resource<ArrayList<FinancingType>>> getFinancingTypes() {
        return new NetworkBoundResource<ArrayList<FinancingType>>() {

            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<FinancingType>>> createCall() {
                return apiService.getFinancingTypes();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<CalculatorData<FinancingResult>>> calculateFinancing(int typeId, String months, String amount) {
        return new NetworkBoundResource<CalculatorData<FinancingResult>>() {
            @NonNull
            @Override
            protected Call<MyResponse<CalculatorData<FinancingResult>>> createCall() {
                return apiService.calculateFinancing(typeId, months, amount);
            }
        }.getAsLiveServerData();
    }


    public LiveData<Resource<ArrayList<Currency>>> getProfitsCurrencies() {
        return new NetworkBoundResource<ArrayList<Currency>>() {

            @NonNull
            @Override
            protected Call<MyResponse<ArrayList<Currency>>> createCall() {
                return apiService.getProfitsCurrencies();
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<CalculatorData<ProfitsResult>>> calculateProfits(int currencyId, int months, String amount, String date) {
        return new NetworkBoundResource<CalculatorData<ProfitsResult>>() {
            @NonNull
            @Override
            protected Call<MyResponse<CalculatorData<ProfitsResult>>> createCall() {
                return apiService.calculateProfits(currencyId, months, amount, date);
            }
        }.getAsLiveServerData();
    }

    public LiveData<Resource<CalculatorData<DepositResult>>> calculateDeposit(int months, String amount, String date) {
        return new NetworkBoundResource<CalculatorData<DepositResult>>() {
            @NonNull
            @Override
            protected Call<MyResponse<CalculatorData<DepositResult>>> createCall() {
                return apiService.calculateDeposit(months, amount, date);
            }
        }.getAsLiveServerData();
    }
}
