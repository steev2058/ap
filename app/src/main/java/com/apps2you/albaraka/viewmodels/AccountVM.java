package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.data.remote.responseModel.HomeData;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;


public class AccountVM extends BaseViewModel {

    public boolean isSYGSActivated = false;

    private final UserRepository userRepository;

    @Inject
    AccountVM(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public LiveData<Resource<ArrayList<Account>>> getAccounts() {
//        return userRepository.getAccounts();
//    }

    public LiveData<Resource<HomeData>> getHomeData() {
        return userRepository.getHomeData();
    }
}
