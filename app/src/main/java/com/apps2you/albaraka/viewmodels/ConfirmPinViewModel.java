package com.apps2you.albaraka.viewmodels;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;

public class ConfirmPinViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final Context context;

    public final MutableLiveData<String> pinCode = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> _pinCodeError = new MutableLiveData<>(false);
    public final LiveData<Boolean> pinCodeError = _pinCodeError;

    private final MediatorLiveData<Boolean> _checkPinStatus = new MediatorLiveData<>();
    public final LiveData<Boolean> checkPinStatus = _checkPinStatus;
    private final MutableLiveData<String> timerText = new MutableLiveData<>(); // Timer text
    private final MutableLiveData<Boolean> isResendEnabled = new MutableLiveData<>(true); // Resend button state

    private CountDownTimer countDownTimer;


    @Inject
    public ConfirmPinViewModel(UserRepository userRepository, Context context) {
        this.userRepository = userRepository;
        this.context = context.getApplicationContext();
    }

    public LiveData<String> getTimerText() {
        return timerText;
    }

    public LiveData<Boolean> getIsResendEnabled() {
        return isResendEnabled;
    }
    public void startTimer(String resendAvailableWithTime, String resendAvailableWithNoTime, String resendEnabled) {
        isResendEnabled.setValue(false); // Disable button

        // Set initial text with 60 seconds
        timerText.setValue(String.format(resendAvailableWithTime, 60));

        countDownTimer = new CountDownTimer(60000, 1000) { // 1-minute timer
            @Override
            public void onTick(long millisUntilFinished) {
                // Update timer text on each tick with seconds remaining
                timerText.setValue(String.format(resendAvailableWithTime, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timerText.setValue(resendEnabled); // Set final text
                isResendEnabled.setValue(true); // Re-enable button
            }
        };

        countDownTimer.start();
    }



    public void resetTimer(String resendAvailableWithTime, String resendAvailableWithNoTime, String resendEnabled) {
        stopTimer(); // Stop any existing timer
     //   startTimer(); // Restart the timer
        startTimer(resendAvailableWithTime,resendAvailableWithNoTime, resendEnabled);
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
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
