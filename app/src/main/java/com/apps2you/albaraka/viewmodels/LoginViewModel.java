package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.PrivacyPolicy;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;


public class LoginViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final AppRepository appRepository;
    private User user;

    @Inject
    public LoginViewModel(UserRepository userRepository, AppRepository appRepository) {
        this.userRepository = userRepository;
        this.appRepository = appRepository;
        this.user = new User();
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LiveData<Resource<User>> login(String password, String fcm_token,String device) {
        return userRepository.login(user.getCif_number(), password , fcm_token,device);
    }

    public LiveData<Resource<String>> resetPassword(String currentPass, String newPass, String confirmNewPass) {
        return userRepository.resetPassword(currentPass, newPass, confirmNewPass);
    }

    public LiveData<Resource<String>> resetPin(String currentPin, String newPin, String confirmNewPin,String otpCode) {
        return userRepository.resetPin(currentPin, newPin, confirmNewPin,otpCode);
    }

    public LiveData<Resource<PrivacyPolicy>> getPrivacyPolicy() {
        return appRepository.getPrivacyPolicy();
    }

    public LiveData<Resource<Boolean>> checkVisitor(){
        return appRepository.checkVisitor();
    }

    public LiveData<Resource<Boolean>> checkAppVersion(String version) {
        return appRepository.checkAppVersion(version);
    }
}
