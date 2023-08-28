package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.AtmCard;
import com.apps2you.albaraka.data.model.AtmLimit;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AtmServicesRepository;
import com.apps2you.albaraka.data.remote.responseModel.AtmServicesData;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;


public class AtmViewModel extends BaseViewModel {

    public AtmServicesData atmData;

    private String pinCode;

    private final AtmServicesRepository repository;


    @Inject
    AtmViewModel(AtmServicesRepository atmServicesRepository) {
        this.repository = atmServicesRepository;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void selectLimit(String limit) {
        for (AtmLimit listItem : atmData.getLimits())
            listItem.setIsSelected(listItem.getLimit().equals(limit));
    }

    public LiveData<Resource<ArrayList<AtmCard>>> getAtmCards() {
        return repository.getAtmCards();
    }

    public LiveData<Resource<AtmServicesData>> getAtmServicesData() {
        return repository.getAtmServicesData();
    }

    public LiveData<Resource<String>> resendPinCode(String cardNumber) {
        return repository.resendPinCode(cardNumber, pinCode);
    }

    public LiveData<Resource<String>> updateLimit(String cardNumber, String limit) {
        return repository.updateLimit(cardNumber, limit, pinCode);
    }

    public LiveData<Resource<String>> updateStatus(AtmCard card) {
        return repository.updateStatus(card.getCardNumber(), card.isActive(), pinCode);
    }
}
