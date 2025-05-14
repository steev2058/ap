package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.MyDevices;
import com.apps2you.albaraka.data.model.PrivacyPolicy;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class MyDevicesVM extends BaseViewModel {

    private final AppRepository appRepository;

    private final MutableLiveData<Boolean> confirmClicked = new MutableLiveData<>();

    @Inject
    public MyDevicesVM(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public LiveData<Resource<ArrayList<MyDevices>>> getMyDevices() {
        return appRepository.getMyDevices();
    }

    public LiveData<Resource<String>> changeTrusting(int id, int status) {
        return appRepository.changeTrusting(id, status);
    }

    public LiveData<Resource<String>> deleteDevice(int id) {
        return appRepository.deleteDevice(id);
    }
    public void onConfirmClicked() {
        confirmClicked.setValue(true);
    }

    public LiveData<Boolean> getConfirmClicked() {
        return confirmClicked;
    }

}
