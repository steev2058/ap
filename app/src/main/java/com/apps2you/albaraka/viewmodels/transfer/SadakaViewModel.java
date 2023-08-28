package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.CharityUI;
import com.apps2you.albaraka.ui.common.model.mapper.CharityUIMapper;
import com.apps2you.albaraka.ui.transfer.sadaka.SadakaForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SadakaViewModel extends BaseSelectionViewModel<CharityUI> {

    public SadakaForm sadakaForm = new SadakaForm();

    private final CharityUIMapper charityUIMapper;

    @Inject
    public SadakaViewModel(UserRepository userRepository, TransferRepository transferRepository,
                           CharityUIMapper charityUIMapper) {
        super(userRepository, transferRepository);
        this.charityUIMapper = charityUIMapper;
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_SADAKA;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null
                && getSelectedItem() != null
                && sadakaForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.sadakaTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            getSelectedItem().getId(),
                            getSelectedItem().getAccountNumber(),
                            sadakaForm.phoneNumber.getLocalizedValue(),
                            sadakaForm.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            sadakaForm.reason.getValue(),
                            selectedAccount.getValue().getType(),
                            pinCode),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }

    @Override
    protected List<CharityUI> filter(List<CharityUI> data, String searchQuery) {
        if (data == null || searchQuery == null) return Collections.emptyList();
        return data
                .stream()
                .filter(charityUI ->
                        charityUI.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<CharityUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getCharities(),
                input -> input.mapData(charityUIMapper::map)
        );
    }
}
