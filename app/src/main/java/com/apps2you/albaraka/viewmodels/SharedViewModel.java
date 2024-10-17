package com.apps2you.albaraka.viewmodels;

// SharedViewModel.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.ui.sep.profile.UserData;
import com.apps2you.albaraka.ui.sep.tabs.CardItemProfile;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends BaseViewModel {

    private final MutableLiveData<List<CardItemProfile>> cardItemList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<CardItemProfile>> getCardItemList() {
        return cardItemList;
    }

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private MutableLiveData<Boolean> refreshData = new MutableLiveData<>(false);
    public void setUserData(UserData data) {
        userData.setValue(data);
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getRefreshData() {
        return refreshData;
    }

    public void setRefreshData(boolean refresh) {
        refreshData.setValue(refresh);
    }
}
