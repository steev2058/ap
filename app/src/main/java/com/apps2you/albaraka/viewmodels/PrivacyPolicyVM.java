package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.PrivacyPolicy;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;


public class PrivacyPolicyVM extends BaseViewModel {

    private final AppRepository appRepository;

    @Inject
    public PrivacyPolicyVM(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public LiveData<Resource<PrivacyPolicy>> getPrivacyPolicy() {
        return appRepository.getPrivacyPolicy();
    }
}
