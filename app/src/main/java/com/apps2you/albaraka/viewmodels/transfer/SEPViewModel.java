package com.apps2you.albaraka.viewmodels.transfer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.AppRepository;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.SEPProviderUI;
import com.apps2you.albaraka.ui.common.model.mapper.SEPProviderUIMapper;
import com.apps2you.albaraka.ui.sep.SEPForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SEPViewModel extends BaseSelectionViewModel<SEPProviderUI> {

    public final MutableLiveData<String> arName = new MutableLiveData<>();
    public final MutableLiveData<String> cif = new MutableLiveData<>();
    public final MutableLiveData<String> phone = new MutableLiveData<>();
    public final MutableLiveData<String> address = new MutableLiveData<>();
    public final MutableLiveData<String> token = new MutableLiveData<>();

    public SEPForm sepForm = new SEPForm();
    protected final AppRepository appRepository;
    private final SEPProviderUIMapper sepProviderUIMapper;

    private final MediatorLiveData<ArrayList<City>> _citiesList = new MediatorLiveData<>();
    public final LiveData<ArrayList<City>> citiesList = _citiesList;

    private final MutableLiveData<City> _selectedCity = new MutableLiveData<>();
    public final LiveData<City> selectedCity = _selectedCity;

    @Inject
    public SEPViewModel(
            UserRepository userRepository,
            TransferRepository transferRepository,
            AppRepository appRepository,
            SEPProviderUIMapper sepProviderUIMapper
    ) {
        super(userRepository, transferRepository);
        this.sepProviderUIMapper = sepProviderUIMapper;
        this.appRepository = appRepository;

        fetchData();
        fetchCities();
    }

    @Override
    public int getTransferTypeId() {
        return Constants.SEP;
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null
                && getSelectedItem() != null
                && sepForm.allowed()) {

            if (isLoadingValue()) {
                return;
            }

            // TODO: implement transfer logic here when needed
        }
    }

    @Override
    public void calculateCommission() {
        // TODO: implement commission calculation if needed
    }

    @Override
    protected List<SEPProviderUI> filter(List<SEPProviderUI> data, String searchQuery) {
        if (data == null || searchQuery == null) {
            return Collections.emptyList();
        }

        return data.stream()
                .filter(item ->
                        item.getName() != null &&
                                item.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<SEPProviderUI>>> provideDataSource() {
        MediatorLiveData<Resource<List<SEPProviderUI>>> result = new MediatorLiveData<>();
        result.setValue(Resource.success(new ArrayList<>(), null));        return result;
    }

    protected LiveData<Resource<ArrayList<City>>> getCities() {
        return appRepository.getCities();
    }

    private void fetchCities() {
        _citiesList.addSource(getCities(), resource -> {
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

    public void selectCity(City city) {
        _selectedCity.setValue(city);
    }

    public City getSelectedCity() {
        return _selectedCity.getValue();
    }
}