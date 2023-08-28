package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;
import com.apps2you.albaraka.ui.common.model.mapper.ADSLProviderUIMapper;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ADSLViewModel extends BaseSelectionViewModel<ADSLProviderUI> {

    public ADSLForm adslForm = new ADSLForm();
    protected final AppRepository appRepository;

    private final ADSLProviderUIMapper adslProviderUIMapper;
    private final MediatorLiveData<ArrayList<City>> _citiesList = new MediatorLiveData<>();
    public final LiveData<ArrayList<City>> citiesList = _citiesList;

    private final MutableLiveData<City> _selectedCity = new MutableLiveData<>();
    public final LiveData<City> selectedCity = _selectedCity;
    @Inject
    public ADSLViewModel(UserRepository userRepository, TransferRepository transferRepository, AppRepository appRepository,
                         ADSLProviderUIMapper adslProviderUIMapper) {
        super(userRepository, transferRepository);
        this.adslProviderUIMapper = adslProviderUIMapper;
        this.appRepository= appRepository;
        fetchCities();
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_ADSL;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null
                && getSelectedItem() != null
                && adslForm.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.adslTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            getSelectedItem().getId(),
                            adslForm.phoneNumber.getLocalizedValue(),
                            adslForm.amount.getLocalizedNumber(),
                            adslForm.reason.getValue(),
                            selectedCity.getValue().getId(),
                            UserUtils.getInstance(MyApplication.getAppContext()).getUser().getPhone(),
                            pinCode),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }

    @Override
    protected List<ADSLProviderUI> filter(List<ADSLProviderUI> data, String searchQuery) {
        if (data == null || searchQuery == null) return Collections.emptyList();
        return data
                .stream()
                .filter(adslProviderUI ->
                        adslProviderUI.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<ADSLProviderUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getADSLProviders(),
                input -> input.mapData(adslProviderUIMapper::map)
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

}
