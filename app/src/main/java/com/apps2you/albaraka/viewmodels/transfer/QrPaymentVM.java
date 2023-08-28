package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;

import com.apps2you.albaraka.data.model.QrCode;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import javax.inject.Inject;


public class QrPaymentVM extends TransferViewModel {

    public BaseTransferForm form = new BaseTransferForm();

    private QrCode qrCode;

    @Inject
    public QrPaymentVM(UserRepository userRepository, TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_QR;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && form.allowed()) {
            if (isLoadingValue()) {
                return;
            }

            _transferStatus.addSource(transferRepository.qrPaymentTransfer(qrCode.getCode(),
                    selectedAccount.getValue().getNumber(),
                    selectedAccount.getValue().getAccountCode(),
                    selectedAccount.getValue().getName(),
                    form.amount.getLocalizedNumber(),
                    selectedAccount.getValue().getCurrency().getCode(),
                    pinCode,
                    form.reason.getValue()),
                    this::handleTransferResponse);
        }
    }

    @Override
    public void calculateCommission() {

    }

    public QrCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    public LiveData<Resource<QrCode>> checkQrValidity(String code) {
        return transferRepository.checkQrValidity(code);
    }
}
