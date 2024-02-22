package com.apps2you.albaraka.viewmodels.transfer.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.Transaction;
import com.apps2you.albaraka.data.model.TransferData;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.utils.lifecyle.Event;

import java.math.BigDecimal;
import java.util.List;

public abstract class TransferViewModel extends BaseViewModel {

    protected final UserRepository userRepository;
    protected final TransferRepository transferRepository;

    private final MutableLiveData<Account> _selectedAccount = new MutableLiveData<>();
    public final LiveData<Account> selectedAccount = _selectedAccount;



    protected final MediatorLiveData<Event<Transaction>> _transferStatus = new MediatorLiveData<>();
    public final LiveData<Event<Transaction>> transferStatus = _transferStatus;
//    protected final MediatorLiveData<Event<Transaction>> _code = new MediatorLiveData<>();
//    public final LiveData<Event<Transaction>> code = _code;
    protected final MediatorLiveData<List<Account>> _accountList = new MediatorLiveData<>();
    public final LiveData<List<Account>> accountList = _accountList;

    private final MediatorLiveData<List<TransferData>> _fees = new MediatorLiveData<>();
    public final LiveData<List<TransferData>> fees = _fees;

    private final MutableLiveData<Boolean> _isFeeLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isFeeLoading = _isFeeLoading;

    protected String pinCode;
    protected int transferFee;
    protected int minlimit;
    protected int maxlimit;
    private String lastSelectedAccountId;


    public TransferViewModel(UserRepository userRepository, TransferRepository transferRepository) {
        this.userRepository = userRepository;
        this.transferRepository = transferRepository;

        fetchAccounts();
        fetchFees();
    }

    public abstract int getTransferTypeId();

    public void setTransferFee(int transferFee) {
        this.transferFee = transferFee;
    }

    public int getTransferFee() {
        return transferFee;
    }

    public void setTransferMinLimit(int minLimit) {
        this.minlimit = minLimit;
    }
    public void setTransferMaxLimit(int maxLimit) {
        this.maxlimit = maxLimit;
    }


    public int getTransferMinLimit() {
        return minlimit;
    }
    public int getTransferMaxLimit() {
        return maxlimit;
    }

    public LiveData<List<TransferData>> getTransferFees() {
        return fees;
    }

    public void fetchFees() {
        if (isFeeLoadingValue())
            return;
        if (fees == null || fees.getValue() == null || fees.getValue().isEmpty()) {
            _fees.addSource(transferRepository.getTransferSettings(), resource -> {
                stopFeeLoading();
                switch (resource.status) {
                    case LOADING:
                        startFeeLoading();
                        break;
                    case ERROR:
                        if (error().getValue() == null)
                            setError(resource.error);
                        break;
                    case SUCCESS:
                        _fees.setValue(resource.data);

                        if (resource.data != null)
                            for (TransferData transferData : resource.data) {
                                if (transferData.getId() == getTransferTypeId()) {
                                    setTransferFee(transferData.getFee());
                                    setTransferMinLimit(Integer.parseInt(transferData.getMinLimit()));
                                    setTransferMaxLimit(Integer.parseInt(transferData.getMaxLimit()));
                                    break;
                                }
                            }
                        break;
                }
            });
        }
    }

    public void fetchAccounts() {

        if (isContentLoadingValue()) {
            return;
        }
        if (accountList == null || accountList.getValue() == null || accountList.getValue().isEmpty()) {
            setSelectedAccount(null);
            _accountList.addSource(userRepository.getAccounts(),
                    resource -> {
                        stopContentLoading();
                        switch (resource.status) {
                            case LOADING:
                                startContentLoading();
                                break;
                            case ERROR:
                                setError(resource.error);
                                break;
                            case SUCCESS:
                                _accountList.setValue(resource.data);
                                break;
                        }
                    });
        }
    }

    public void setSelectedAccount(Account account) {
        _selectedAccount.setValue(account);
    }

    protected void handleTransferResponse(Resource<Transaction> resource) {
        stopLoading();
        pinCode = "";
        switch (resource.status) {
            case LOADING:
                startLoading();
                break;
            case SUCCESS:
                if (resource.data != null) {
                    lastSelectedAccountId = resource.data.getAccountNumber();
                    _transferStatus.setValue(new Event<>(resource.data));
                }
                break;
            case ERROR:
                showMessage(resource.message);
                break;
        }
    }

    public String getLastSelectedAccountId() {
        return lastSelectedAccountId;
    }

    public abstract void transfer();
    public final MediatorLiveData<Event<Boolean>> _commissionFetched = new MediatorLiveData<>();
    public final LiveData<Event<Boolean>> commissionFetched = _commissionFetched;

    public BigDecimal commission = new BigDecimal(0);

    public String toFullName = new String();


    public abstract void calculateCommission();

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void startFeeLoading() {
        _isFeeLoading.setValue(true);
    }

    public void stopFeeLoading() {
        _isFeeLoading.setValue(false);
    }

    public boolean isFeeLoadingValue() {
        return isFeeLoading.getValue() != null && isFeeLoading.getValue();
    }
}
