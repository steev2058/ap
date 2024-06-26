package com.apps2you.albaraka.viewmodels;

// SharedViewModel.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.ui.sep.profile.UserData;

public class SharedViewModel extends BaseViewModel {

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();

    public void setUserData(UserData data) {
        userData.setValue(data);
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }
}
