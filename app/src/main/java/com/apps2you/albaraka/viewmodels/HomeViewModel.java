package com.apps2you.albaraka.viewmodels;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.QuickService;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.MyResponse;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.data.remote.responseModel.HomeData;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;

public class HomeViewModel extends BaseViewModel {

    private final UserRepository userRepository;

    private User user;

    public String lang;
    public boolean isSYGSActivated = false;
    public ObservableField<Boolean> showTouchIDOption;
    public static ObservableField<Integer> notificationCount = new ObservableField<>(0);
    private MutableLiveData<ArrayList<QuickService>> homeQuickServices = new MutableLiveData<>(new ArrayList<>());
    private Call<MyResponse<ArrayList<Transaction>>> recentTransactionsCall;

    @Inject
    public HomeViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        user = new UserUtils(MyApplication.getAppContext()).getUser();
        this.showTouchIDOption = new ObservableField<>(false);
    }

    public User getUser() {
        return user;
    }

    public String getUserName() {
        if (user == null)
            user = new UserUtils(MyApplication.getAppContext()).getUser();
        return user.getUserName(new UserUtils(MyApplication.getAppContext()).getLanguage());
    }

    public String getInitial() {
        String name = getUserName();
        String nameChars;
        if (name.length() > 0) {
            String[] splited = name.split("\\s+");
            nameChars = splited[0].substring(0, 1);
            if (splited.length > 1)
                nameChars += splited[splited.length - 1].substring(0, 1);
            return nameChars;
        }
        return "";
    }

    public boolean isSoundEnabled() {
        return new UserUtils(MyApplication.getAppContext()).isSoundEnabled();
    }

    public void setSoundEnabled(boolean soundEnabled) {
        new UserUtils(MyApplication.getAppContext()).setSoundEnabled(soundEnabled);
    }

    public LiveData<Resource<HomeData>> getHomeData() {
        return userRepository.getHomeData();
    }


    public LiveData<Resource<ArrayList<Transaction>>> getRecentTransactions() {
        if (recentTransactionsCall != null && !recentTransactionsCall.isCanceled())
            recentTransactionsCall.cancel();
        NetworkBoundResource<ArrayList<Transaction>> request = userRepository.getRecentTransactions();
        recentTransactionsCall = request.getCall();
        return request.getAsLiveServerData();
    }

    public LiveData<Resource<String>> logout() {
        return userRepository.logout();
    }

    public LiveData<Resource<String>> changeLanguage(String lang) {
        return userRepository.changeLanguage(lang);
    }

    public LiveData<Resource<String>> resetNotificationCounter() {
        return userRepository.resetNotificationCounter();
    }

    public LiveData<Resource<String>> turnNotifications(int status) {
        return userRepository.turnNotifications(status);
    }

    public LiveData<Resource<ArrayList<QuickService>>> getQuickServices() {
        return userRepository.getQuickServices();
    }

    public LiveData<Resource<String>> personalizeQuickServices(ArrayList<QuickService> personalizedQuickServices) {

        return userRepository.personalizeQuickServices(personalizedQuickServices.toString());
    }

    public MutableLiveData<ArrayList<QuickService>> getHomeQuickServices() {
        return this.homeQuickServices;
    }

    public void setQuickHomeServices(ArrayList<QuickService> quickHomeServices) {
        this.homeQuickServices.setValue(quickHomeServices);
//        this.quickHomeServices.addAll(quickHomeServices);
    }

}
