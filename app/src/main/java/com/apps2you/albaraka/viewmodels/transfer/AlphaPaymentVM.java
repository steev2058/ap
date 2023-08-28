package com.apps2you.albaraka.viewmodels.transfer;

import android.text.TextUtils;

import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;

import javax.inject.Inject;


public class AlphaPaymentVM extends TransferViewModel {

    private String amount, alphaId;


    @Inject
    public AlphaPaymentVM(UserRepository userRepository, TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_ALPHA;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && !TextUtils.isEmpty(amount) && !TextUtils.isEmpty(alphaId)) {
            if (isLoadingValue())
                return;

            _transferStatus.addSource(transferRepository.alphaTransfer(alphaId,
                    selectedAccount.getValue().getNumber(),
                    selectedAccount.getValue().getAccountCode(),
                    selectedAccount.getValue().getName(),
                    getLocalizedAmount(),
                    selectedAccount.getValue().getCurrency().getCode(),
                    pinCode),
                    this::handleTransferResponse);
        }
    }

    @Override
    public void calculateCommission() {

    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getLocalizedAmount() {
        return com.apps2you.albaraka.utils.text.TextUtils.toEnglishNumber(amount);
    }

    public String getAlphaId() {
        return alphaId;
    }

    public void setAlphaId(String alphaId) {
        this.alphaId = alphaId;
    }

    public boolean isAmountInvalid() {
        return new BigDecimal(getLocalizedAmount()).compareTo(BigDecimal.ZERO) <= 0;
    }
}
