package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;

public class ConfirmPinViewModel extends BaseViewModel {

    private final UserRepository userRepository;

    public final MutableLiveData<String> pinCode = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> _pinCodeError = new MutableLiveData<>(false);
    public final LiveData<Boolean> pinCodeError = _pinCodeError;

    private final MediatorLiveData<Boolean> _checkPinStatus = new MediatorLiveData<>();
    public final LiveData<Boolean> checkPinStatus = _checkPinStatus;


    @Inject
    public ConfirmPinViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void check(){
        if (isContentLoadingValue()){
            return;
        }

        String pinCodeText = pinCode.getValue();
        if (pinCodeText != null && pinCodeText.length() == 4){
            _checkPinStatus.addSource(userRepository.checkPinCode(pinCodeText), resource -> {
                stopContentLoading();
                switch (resource.status){
                    case LOADING:
                        startContentLoading();
                        break;
                    case ERROR:
                        showMessage(resource.message);
                        break;
                    case SUCCESS:
                        hideKeyboard();
                        _checkPinStatus.setValue(true);
                        break;
                }
            });
        }else {
            _pinCodeError.setValue(true);
        }
    }
}
