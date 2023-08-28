package com.apps2you.albaraka.viewmodels;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.ExchangeRate;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.data.remote.responseModel.ExchangeData;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class ExchangeViewModel extends BaseViewModel {

    private final AppRepository appRepository;
    public ObservableField<String> lastUpdate;
    public ArrayList<ExchangeRate> exchangeRatesList;

    @Inject
    public ExchangeViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;

        this.lastUpdate = new ObservableField<>("--.--.----");
    }

    public LiveData<Resource<ExchangeData>> getExchangeRates() {
        return appRepository.getExchangeRates();
    }
}
