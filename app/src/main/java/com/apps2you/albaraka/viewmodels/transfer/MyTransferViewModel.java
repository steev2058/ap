package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.data.model.TransferChannelType;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.networkUtils.Status;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.data.remote.responseModel.SygsCommissionData;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.myTransfer.MyTransferForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.lifecyle.Event;
import com.apps2you.albaraka.utils.text.TextUtils;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;

import javax.inject.Inject;

public class MyTransferViewModel extends TransferViewModel {

    public MyTransferForm myTransferForm = new MyTransferForm();

    private String lastSelectedToAccountId;

    private final MutableLiveData<Account> _selectedToAccount = new MutableLiveData<>();
    public final LiveData<Account> selectedToAccount = _selectedToAccount;

    @Inject
    public MyTransferViewModel(UserRepository userRepository, TransferRepository transferRepository) {
        super(userRepository, transferRepository);
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_MY_TRANSFER;
    }

    public void setSelectedToAccount(Account account){
        _selectedToAccount.setValue(account);
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && selectedToAccount.getValue() != null && myTransferForm.allowed()) {

            if (isLoadingValue()) {
                return;
            }
            hideKeyboard();

            _transferStatus.addSource(
                    transferRepository.myTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            selectedToAccount.getValue().getNumber(),
                            selectedToAccount.getValue().getAccountCode(),
                            myTransferForm.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            myTransferForm.reason.getValue(),
                            selectedAccount.getValue().getType(),
                            pinCode
                    ),
                    resource -> {
                        if (resource.status == Status.SUCCESS){
                            lastSelectedToAccountId = selectedToAccount.getValue().getNumber();
                        }
                        handleTransferResponse(resource);
                    }
            );
        }
    }

    @Override
    public void calculateCommission() {


        LiveData<Resource<SygsCommissionData>> source =  transferRepository.getExchangeInfo(

                selectedAccount.getValue().getCurrency().getCode(),
                selectedToAccount.getValue().getCurrency().getCode(),
                myTransferForm.amount.getLocalizedNumber()

        );
        _exchangeMessageFetched.addSource(
                source,
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
                                exchangeMessage = new String(resource.data.getFullName());

                            _exchangeMessageFetched.setValue(Event.of(true));
                            break;
                    }
                }
        );
    }

    public String getLastSelectedToAccountId() {
        return lastSelectedToAccountId;
    }
}
