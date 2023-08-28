package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.About;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;

public class AboutViewModel extends BaseViewModel {

    private final AppRepository appRepository;

    @Inject
    public AboutViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
    }


    public LiveData<Resource<About>> getAboutModel() {
        return appRepository.getAbout();
    }

    //    public void setAbout(About about) {
//        this.about = about;
//    }
//
//    public About getAbout() {
//        return about;
//    }
}
