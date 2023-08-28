package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class TransactionsViewModel extends BaseViewModel {

    private final UserRepository userRepository;

    private final ArrayList<Transaction> transactions;

    @Inject
    public TransactionsViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        transactions = new ArrayList<>();
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public LiveData<Resource<ArrayList<Account>>> getAccounts() {
        return userRepository.getAccounts();
    }

    public LiveData<Resource<ArrayList<Transaction>>> getTransactions(String accountNumber, String accountName,String fromDate, String toDate) {
        return userRepository.getTransactions(accountNumber, accountName,fromDate,toDate);
    }

    public MediatorLiveData<ResponseBody> getStatementFile() {
        return userRepository.getStatementFile();
    }
}
