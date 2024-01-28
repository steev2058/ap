package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.model.HFModel;
import com.apps2you.albaraka.data.model.HfTransferType;
import com.apps2you.albaraka.data.model.SYGSTransferType;
import com.apps2you.albaraka.data.preference.UserUtils;
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



public class HFViewModel extends BaseSelectionViewModel<HFProviderUI>  {
    private MutableLiveData<HFModel> hfItem = new MutableLiveData<>();
    private MutableLiveData<HfTransferType> selectedTransferType = new MutableLiveData<>();
    private MutableLiveData<HFProviderUI> trans_code = new MutableLiveData<>();
    public LiveData<HFModel> getHfItem() {
        return hfItem;
    }

    public void setHfItem(HFModel item) {
        hfItem.setValue(item);
    }
    public HFForm hfForm = new HFForm();
    protected final AppRepository appRepository;

    private final HFProviderUIMapper hfProviderUIMapper;
    private final MediatorLiveData<ArrayList<City>> _citiesList = new MediatorLiveData<>();
    public final LiveData<ArrayList<City>> citiesList = _citiesList;

    private final MutableLiveData<City> _selectedCity = new MutableLiveData<>();
    public final LiveData<City> selectedCity = _selectedCity;



    @Inject
    public HFViewModel(UserRepository userRepository, TransferRepository transferRepository, AppRepository appRepository,
                         HFProviderUIMapper hfProviderUIMapper) {
        super(userRepository, transferRepository);
        this.hfProviderUIMapper = hfProviderUIMapper;
        this.appRepository= appRepository;
        fetchCities();
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_HF;
    }
    @Override
    public void calculateCommission() {
        //HfTransferType selectedType = selectedTransferType.getValue();
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
                                BigDecimal commission = new BigDecimal(resource.data.getCommission());
                                BigDecimal totalCost = commission.add(new BigDecimal(hfForm.amount.getLocalizedNumber()));
                                _commissionFetched.setValue(Event.of(true));
                            }
                            break;
                    }
                }
        );
    }

//    public void calculateCommission() {
//        SYGSTransferType selectedType = selectedTransferType.getValue();
//        Account account = selectedAccount.getValue();
//        if (isLoadingValue() || selectedType == null || account == null) return;
//
//        _commissionFetched.addSource(
//                transferRepository.calculateHfCommission(
//                        form.amount.getLocalizedNumber(),
//                        selectedType.getTypeCode(),
//                        account.getCurrency().getCode()
//                ),
//                resource -> {
//                    stopLoading();
//                    switch (resource.status) {
//                        case LOADING:
//                            startLoading();
//                            break;
//                        case ERROR:
//                            setError(resource.error);
//                            break;
//                        case SUCCESS:
//                            if (resource.data != null)
//                                commission = new BigDecimal(resource.data.getCommission());
//                            totalCost = commission.add(new BigDecimal(form.amount.getLocalizedNumber()));
//                            _commissionFetched.setValue(Event.of(true));
//                            break;
//                    }
//                }
//        );
//    }
    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null
                && getSelectedItem() != null
                && hfForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.hfTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            getSelectedItem().getId(),
                            hfForm.phoneNumber.getLocalizedValue(),
                            hfForm.amount.getLocalizedNumber(),
                            hfForm.reason.getValue(),
//                            code.getValue(),
                            selectedCity.getValue().getId(),
                            UserUtils.getInstance(MyApplication.getAppContext()).getUser().getPhone(),
                            pinCode),
                    this::handleTransferResponse
            );
        }
    }

//    @Override
//    public void calculateCommission() {
//
//    }

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

    private void fetchCities(){
//        if (isContentLoadingValue()) {
//            return;
//        }
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
                            if (resource.data==null) return;
                            _citiesList.setValue(resource.data);
                            break;
                    }
                });
    }


    public void setSelectedCity(City city) {
        _selectedCity.setValue(city);
    }

//    public void setSelectedhf(City city) {
//        _selectedCity.setValue(city);
//    }

}
