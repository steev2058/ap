package com.apps2you.albaraka.viewmodels.transfer;

import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.zakat.ZakatForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import javax.inject.Inject;

public class ZakatViewModel extends TransferViewModel {

    public final ZakatForm zakatForm = new ZakatForm();

    @Inject
    public ZakatViewModel(UserRepository userRepository,
                          TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_ZAKAT;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && zakatForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }

            hideKeyboard();

            _transferStatus.addSource(
                    transferRepository.zakatTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            zakatForm.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            zakatForm.reason.getValue(),
                            selectedAccount.getValue().getType(),
                            pinCode
                    ),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }
}
