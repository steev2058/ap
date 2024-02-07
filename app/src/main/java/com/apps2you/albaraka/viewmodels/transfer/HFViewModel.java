package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.model.HFModel;
import com.apps2you.albaraka.data.model.HfTransferType;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.HFProviderUI;
import com.apps2you.albaraka.ui.common.model.mapper.HFProviderUIMapper;
import com.apps2you.albaraka.ui.transfer.hf.HFForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.lifecyle.Event;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class HFViewModel extends BaseSelectionViewModel<HFProviderUI> {
    private MutableLiveData<HFModel> hfItem = new MutableLiveData<>();
    private MutableLiveData<HfTransferType> selectedTransferType = new MutableLiveData<>();

    private BigDecimal minLimit = BigDecimal.ZERO;
    private BigDecimal maxLimit = BigDecimal.ZERO;


    private MutableLiveData<HFProviderUI> trans_code = new MutableLiveData<>();
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal commission = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;

    public LiveData<HFModel> getHfItem() {
        return hfItem;
    }

    public void setHfItem(HFModel item) {
        hfItem.setValue(item);
    }

    public HFForm hfForm = new HFForm();

    protected final HFProviderUIMapper hfProviderUIMapper;

    protected final AppRepository appRepository;
    private final MediatorLiveData<ArrayList<City>> _citiesList = new MediatorLiveData<>();
    public final LiveData<ArrayList<City>> citiesList = _citiesList;

    private final MutableLiveData<City> _selectedCity = new MutableLiveData<>();
    public final LiveData<City> selectedCity = _selectedCity;

    private final MutableLiveData<Event<Boolean>> _paymentConfirmed = new MutableLiveData<>();

    private MutableLiveData<BigDecimal> _amountLiveData = new MutableLiveData<>();
    private MutableLiveData<BigDecimal> _commissionLiveData = new MutableLiveData<>();
    private MutableLiveData<BigDecimal> _totalCostLiveData = new MutableLiveData<>();

    public LiveData<BigDecimal> amountLiveData = _amountLiveData;

    public LiveData<BigDecimal> commissionLiveData = _commissionLiveData;
    public LiveData<BigDecimal> totalCostLiveData = _totalCostLiveData;

    @Inject
    public HFViewModel(UserRepository userRepository, TransferRepository transferRepository, AppRepository appRepository,
                       HFProviderUIMapper hfProviderUIMapper) {
        super(userRepository, transferRepository);
        this.hfProviderUIMapper = hfProviderUIMapper;
        this.appRepository= appRepository;
        fetchCities();
    }

    public void confirmPayment() {
        _paymentConfirmed.setValue(Event.of(true));
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_HF;
    }

    @Override
    public void calculateCommission() {
        String code = getSelectedItem().getProviderCode();
        Account account = selectedAccount.getValue();
        if (isLoadingValue() || code == null || account == null) return;

        _commissionFetched.addSource(
                transferRepository.calculateHfCommission(
                        hfForm.amount.getLocalizedNumber(),
                        Integer.parseInt(code),
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
                            if (resource.data != null) {
                                commission = new BigDecimal(resource.data.getCommission());

                                totalCost = commission.add(new BigDecimal(hfForm.amount.getLocalizedNumber()));

                                _amountLiveData.setValue(new BigDecimal(hfForm.amount.getLocalizedNumber()));
                                _commissionLiveData.setValue(commission);
                                _totalCostLiveData.setValue(totalCost);

                                hfForm.setAmount(new BigDecimal(hfForm.amount.getLocalizedNumber()));
                                hfForm.setCommission(commission);
                                hfForm.setTotalCost(totalCost);

                                _commissionFetched.setValue(Event.of(true));
                            }
                            break;
                    }
                }
        );
    }

    public int getMinLimit() {
        return getTransferMinLimit();
    }

    public int getMaxLimit() {
        return getTransferMaxLimit();
    }

    public void setMinLimit(BigDecimal minLimit) {
      this.minLimit=minLimit;
    }


    public void setMaxLimit(BigDecimal maxLimit) {
        this.maxLimit=maxLimit;
    }

    public void setAmount(BigDecimal amount) {
        hfForm.setAmount(amount);
        _amountLiveData.setValue(amount);
    }



    public void setCommission(BigDecimal commission) {
        hfForm.setCommission(commission);
        _commissionLiveData.setValue(commission);
    }

    public void setTotalCost(BigDecimal totalCost) {
        hfForm.setTotalCost(totalCost);
        _totalCostLiveData.setValue(totalCost);
    }

    public String getAmount() {
        return hfForm.amount.getLocalizedValue() + " ";
    }

    public String getCommission() {
        return commissionLiveData + " ";
    }

    public String getTotalCost() {
        return totalCostLiveData + " ";
    }


    @Override
    public void transfer() {

        if (selectedAccount.getValue() != null
                && getSelectedItem() != null
                && hfForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            String benefAddressValue = "";

            if (selectedCity.getValue() != null) {
                benefAddressValue = selectedCity.getValue().getName();

                hfForm.benefAddress.setValue(benefAddressValue);
            }

            _transferStatus.addSource(
                    transferRepository.hfTransfer(
                            hfForm.amount.getLocalizedNumber(),
                            getSelectedItem().getProviderCode(),
                            getSelectedItem().getId(),
                            hfForm.reason.getValue(),
                            hfForm.bfirsname.getValue(),
                            hfForm.bsecname.getValue(),
                            hfForm.blastname.getValue(),
                            hfForm.phoneNumber.getValue(),
                            benefAddressValue,
                            pinCode,
                            selectedCity.getValue().getId(),
                            selectedAccount.getValue().getNumber()

                    ),
                    this::handleTransferResponse
            );
        }
    }


    @Override
    protected List<HFProviderUI> filter(List<HFProviderUI> data, String searchQuery) {
        if (data == null || searchQuery == null) return Collections.emptyList();
        return data
                .stream()
                .filter(hfProviderUI ->
                        hfProviderUI.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<HFProviderUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getHFProviders(),
                input -> input.mapData(hfProviderUIMapper::map)
        );
    }

    protected LiveData<Resource<ArrayList<City>>> getCities() {
        return appRepository.getCities();
    }

    private void fetchCities() {
        _citiesList.addSource(getCities(),
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
                            if (resource.data == null) return;
                            _citiesList.setValue(resource.data);
                            break;
                    }
                });
    }

    public void setSelectedCity(City city) {
        _selectedCity.setValue(city);
    }
}

