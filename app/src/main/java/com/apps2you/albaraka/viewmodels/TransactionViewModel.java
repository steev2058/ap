package com.apps2you.albaraka.viewmodels;


import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;

public class TransactionViewModel extends BaseViewModel {

    private final AppRepository appRepository;
    public ObservableField<Transaction> transaction;

    @Inject
    public TransactionViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;

        transaction = new ObservableField<>();
    }

    public LiveData<Resource<Transaction>> getTransactionDetails(int id, String branchCode) {
        return appRepository.getTransactionDetails(id, branchCode);
    }
}
