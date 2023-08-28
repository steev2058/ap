package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.PaymentCategory;
import com.apps2you.albaraka.data.model.SYGSTransferType;
import com.apps2you.albaraka.data.model.SYGSType;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.data.remote.responseModel.SYGSData;
import com.apps2you.albaraka.ui.common.model.BankUI;
import com.apps2you.albaraka.ui.common.model.mapper.BankUIMapper;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.lifecyle.Event;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SYGSTransferViewModel extends BaseSelectionViewModel<BankUI> {
    public SYGSTransferForm form = new SYGSTransferForm(SYGSType.Default);

    private final MediatorLiveData<SYGSData> _sygsData = new MediatorLiveData<>();
    public final LiveData<SYGSData> sygsData = _sygsData;

    private final MutableLiveData<SYGSTransferType> _selectedTransferType = new MutableLiveData<>();
    public final LiveData<SYGSTransferType> selectedTransferType = _selectedTransferType;

    private final MutableLiveData<PaymentCategory> _selectedPaymentCategory = new MutableLiveData<>();
    public final LiveData<PaymentCategory> selectedPaymentCategory = _selectedPaymentCategory;

    private final MutableLiveData<Boolean> _isOperatorsLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isOperatorsLoading = _isOperatorsLoading;

    private final MutableLiveData<Event<Boolean>> _paymentConfirmed = new MutableLiveData<>();
    public final LiveData<Event<Boolean>> paymentConfirmed = _paymentConfirmed;

    private final MediatorLiveData<Event<Boolean>> _commissionFetched = new MediatorLiveData<>();
    public final LiveData<Event<Boolean>> commissionFetched = _commissionFetched;

    private BigDecimal commission = new BigDecimal(0);
    private BigDecimal totalCost = new BigDecimal(0);

    @Inject
    public SYGSTransferViewModel(UserRepository userRepository,
                                 TransferRepository transferRepository, BankUIMapper bankUIMapper) {
        super(userRepository, transferRepository);

        fetchSYGSData();
    }

    @Override
    public int getTransferTypeId() {
        return -1;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && selectedItemLiveData.getValue() != null && selectedTransferType.getValue() != null && form.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.sygsPaymentTransfer(form,
                            SYGSType.getTypeById(selectedTransferType.getValue().getId()),
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            selectedAccount.getValue().getName(),
                            selectedItemLiveData.getValue().getBankId(),
                            selectedItemLiveData.getValue().getBankCode(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            pinCode),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void fetchFees() {
        // these fees are not needed with SYGS transfer
    }

    @Override
    public void fetchData() {

    }

    public void fetchSYGSData() {
        if (isOperatorsLoading.getValue() != null && isOperatorsLoading.getValue()) {
            return;
        }

        startContentLoading();

        _sygsData.addSource(
                transferRepository.getSYGSData(),
                resource -> {
                    _isOperatorsLoading.setValue(false);
                    switch (resource.status) {
                        case LOADING:
                            stopContentLoading();
                            _isOperatorsLoading.setValue(true);
                            break;
                        case ERROR:
                            setError(resource.error);
                            stopContentLoading();
                            break;
                        case SUCCESS:
//        Partner partner =new Partner(1,"name1","bank","8765432",33,1,"");
//        Partner partner1 =new Partner(2,"name2","bank","8765432",32,1,"");
//        Partner partner2 =new Partner(3,"name3","bank","8765432",34,1,"");
//        List<Partner> banks = new ArrayList<>();
//        banks.add(partner);
//        banks.add(partner1);
//        banks.add(partner2);
//        SYGSData sygsData = new SYGSData(banks,"conditions");
                            _sygsData.setValue(resource.data);
                            handleData(new BankUIMapper().map(resource.data.getBanks()));
                            break;
                    }
                }
        );
    }

    public void calculateCommission() {
        SYGSTransferType selectedType = selectedTransferType.getValue();
        Account account = selectedAccount.getValue();
        if (isLoadingValue() || selectedType == null || account == null) return;

        _commissionFetched.addSource(
                transferRepository.calculateSygsCommission(
                        form.amount.getLocalizedNumber(),
                        selectedType.getTypeCode(),
                        account.getCurrency().getCode()
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
                            totalCost = commission.add(new BigDecimal(form.amount.getLocalizedNumber()));
                            _commissionFetched.setValue(Event.of(true));
                            break;
                    }
                }
        );
    }

    public void selectTransferType(SYGSTransferType sygsTransferType) {
        _selectedTransferType.setValue(sygsTransferType);
    }

    public void selectPaymentCategory(PaymentCategory paymentCategory) {
        _selectedPaymentCategory.setValue(paymentCategory);
    }

    public void confirmPayment() {
        _paymentConfirmed.setValue(Event.of(true));
    }

    public boolean isPostpaid() {
        return _selectedTransferType.getValue() != null && _selectedTransferType.getValue().getName().equals(Constants.POST_PAID);
    }


    @Override
    protected List<BankUI> filter(List<BankUI> data, String searchQuery) {
        if (data == null || searchQuery == null) return Collections.emptyList();
        return data
                .stream()
                .filter(bankUI ->
                        bankUI.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<BankUI>>> provideDataSource() {
        return null;
    }

//    @Override
//    protected void handleData(List<BankUI> data) {
//        _modelList.setValue(data);
//        checkSelected(selectedItem.getValue());
//        filter(_searchQuery.getValue());
//    }

//    @Override
//    protected LiveData<Resource<List<BankUI>>> provideDataSource() {
//        return Transformations.map(
//                new Resource<ArrayList<Partner>>()sygsData,
//                input -> input.mapData(BankUIMapper::map)
//        );
//
//        LiveData<Resource<ArrayList<Partner>>>
//    }


    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getTotalCost() {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(totalCost);
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void resetForm() {
        selectItem(null);
        form.reset();
    }
}
