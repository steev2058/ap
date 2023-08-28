package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.DepositResult;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.CalculatorRepository;
import com.apps2you.albaraka.data.remote.responseModel.CalculatorData;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.text.TextUtils;

import javax.inject.Inject;

public class DepositCalculatorVM extends BaseViewModel {

    private final CalculatorRepository repository;

    @Inject
    public DepositCalculatorVM(CalculatorRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<CalculatorData<DepositResult>>> calculate(int months, String amount, String date) {
        amount = TextUtils.toEnglishNumber(amount);
        return repository.calculateDeposit(months, amount, date);
    }
}
