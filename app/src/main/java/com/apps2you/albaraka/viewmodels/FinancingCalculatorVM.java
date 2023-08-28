package com.apps2you.albaraka.viewmodels;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.FinancingResult;
import com.apps2you.albaraka.data.model.FinancingType;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.CalculatorRepository;
import com.apps2you.albaraka.data.remote.responseModel.CalculatorData;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.text.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;

public class FinancingCalculatorVM extends BaseViewModel {

    private final CalculatorRepository repository;


    @Inject
    public FinancingCalculatorVM(CalculatorRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<ArrayList<FinancingType>>> getFinancingTypes() {
        return repository.getFinancingTypes();
    }

    public LiveData<Resource<CalculatorData<FinancingResult>>> calculate(int typeId, String months, String amount) {
        amount = TextUtils.toEnglishNumber(amount);
        return repository.calculateFinancing(typeId, months, amount);
    }
}
