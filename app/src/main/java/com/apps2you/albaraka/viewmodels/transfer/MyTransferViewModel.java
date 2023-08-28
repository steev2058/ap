package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.remote.networkUtils.Status;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.myTransfer.MyTransferForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

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

    }

    public String getLastSelectedToAccountId() {
        return lastSelectedToAccountId;
    }
}
