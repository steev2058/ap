package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.data.model.SYGSTransferType;
import com.apps2you.albaraka.data.model.TransferChannelType;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.AlBarakaTransferForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.lifecyle.Event;
import com.apps2you.albaraka.utils.text.TextUtils;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;

import javax.inject.Inject;

public class AlBarakaTransferViewModel extends TransferViewModel {
    public final AlBarakaTransferForm alBarakaTransferForm = new AlBarakaTransferForm();

    @Inject
    public AlBarakaTransferViewModel(UserRepository userRepository, TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_AL_BARAKA;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && alBarakaTransferForm.allowed()) {
            if (isLoadingValue())
                return;
            hideKeyboard();

            FavoriteAccount favoriteAccount = alBarakaTransferForm.selectedFavoriteAccount.getValue();

            String number = favoriteAccount == null ? alBarakaTransferForm.getNumber() : favoriteAccount.getNumber();
            if (getTransferChannelType().equals(TransferChannelType.GSM))
                number = TextUtils.withoutCountryCode(number);

            _transferStatus.addSource(
                    transferRepository.alBarakaTransfer(
                            getTransferChannelType(),
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            number,
                            alBarakaTransferForm.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            alBarakaTransferForm.reason.getValue(),
                            selectedAccount.getValue().getType(),
                            pinCode
                    ),
                    this::handleTransferResponse
            );
        }
    }

    private BigDecimal totalCost = new BigDecimal(0);

    public void calculateCommission() {

        Account account = selectedAccount.getValue();
        if (isLoadingValue() ||  account == null) return;
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();

        _commissionFetched.addSource(
                transferRepository.getBarakaTransferFees(
                        getTransferChannelType(),
                        selectedAccount.getValue().getNumber(),
                        selectedAccount.getValue().getAccountCode(),
                        alBarakaTransferForm.amount.getLocalizedNumber(),
                        user.getCif_number()
                ),
                resource -> {
                    stopLoading();
                    switch (resource.status) {
                        case LOADING:
                            startLoading();
                            break;
                        case ERROR:
                            setError(resource.error);
                            break;
                        case SUCCESS:
                            if (resource.data != null)
                                commission = new BigDecimal(resource.data.getCommission());
                            totalCost = commission.add(new BigDecimal(alBarakaTransferForm.amount.getLocalizedNumber()));
                            _commissionFetched.setValue(Event.of(true));
                            break;
                    }
                }
        );
    }

    private TransferChannelType getTransferChannelType() {
        FavoriteAccount favoriteAccount = alBarakaTransferForm.selectedFavoriteAccount.getValue();
        if (favoriteAccount != null) {
            return favoriteAccount.getTransferChannelType();
        } else {
            return alBarakaTransferForm.isCIFSelected() ? TransferChannelType.CIF : TransferChannelType.GSM;
        }
    }

    public void selectFavoriteAccount(FavoriteAccount favoriteAccount) {
        alBarakaTransferForm.selectedFavoriteAccount.setValue(favoriteAccount);
    }
}
