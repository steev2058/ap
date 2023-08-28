package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.Currency;
import com.apps2you.albaraka.data.model.ProfitsResult;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.CalculatorRepository;
import com.apps2you.albaraka.data.remote.responseModel.CalculatorData;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.text.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;


public class ProfitsCalculatorVM extends BaseViewModel {

    private final CalculatorRepository repository;


    @Inject
    public ProfitsCalculatorVM(CalculatorRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<ArrayList<Currency>>> getProfitsCurrencies() {
        return repository.getProfitsCurrencies();
    }

    public LiveData<Resource<CalculatorData<ProfitsResult>>> calculate(int currencyId, int months, String amount, String date) {
        amount = TextUtils.toEnglishNumber(amount);
        return repository.calculateProfits(currencyId, months, amount, date);
    }
}
