package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;

import javax.inject.Inject;

public class BillsVM extends BaseViewModel {

    private final TransferRepository transferRepository;

    @Inject
    public BillsVM(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public LiveData<Resource<String>> getBillsLink() {
        return transferRepository.getBillsLink();
    }
}
