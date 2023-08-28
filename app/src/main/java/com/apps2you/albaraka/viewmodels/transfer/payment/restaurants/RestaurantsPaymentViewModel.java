package com.apps2you.albaraka.viewmodels.transfer.payment.restaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.apps2you.albaraka.data.remote.networkUtils.Resource;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.data.remote.repository.UserRepository;
import com.apps2you.albaraka.ui.common.model.RestaurantUI;
import com.apps2you.albaraka.ui.common.model.mapper.RestaurantUIMapper;
import com.apps2you.albaraka.ui.transfer.payment.restaurants.RestaurantsPaymentForm;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.transfer.base.BaseSelectionViewModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RestaurantsPaymentViewModel extends BaseSelectionViewModel<RestaurantUI> {
    private final RestaurantUIMapper restaurantUIMapper;

    public final RestaurantsPaymentForm form = new RestaurantsPaymentForm();

    @Inject
    public RestaurantsPaymentViewModel(UserRepository userRepository,
                                       TransferRepository transferRepository,
                                       RestaurantUIMapper restaurantUIMapper) {
        super(userRepository, transferRepository);
        this.restaurantUIMapper = restaurantUIMapper;
    }

    @Override
    public int getTransferTypeId() {
        return Constants.TRANSFER_RESTAURANT;
    }

    @Override
    protected List<RestaurantUI> filter(List<RestaurantUI> data, String searchQuery) {
        return data.stream()
                .filter(
                        restaurant -> restaurant.getName().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    @Override
    protected LiveData<Resource<List<RestaurantUI>>> provideDataSource() {
        return Transformations.map(
                transferRepository.getRestaurants(),
                resource -> resource.mapData(restaurantUIMapper::map)
        );
    }

    @Override
    public void transfer() {
        if (selectedAccount.getValue() != null && selectedItemLiveData.getValue() != null
                && form.allowed()) {
            if (isLoadingValue()) {
                return;
            }
            _transferStatus.addSource(
                    transferRepository.restaurantTransfer(
                            selectedAccount.getValue().getNumber(),
                            selectedAccount.getValue().getAccountCode(),
                            selectedItemLiveData.getValue().getAccountNumber(),
                            form.amount.getLocalizedNumber(),
                            selectedAccount.getValue().getCurrency().getCode(),
                            selectedAccount.getValue().getType(),
                            form.billNumber.getLocalizedValue(),
                            form.tips.getLocalizedNumber(),
                            form.reason.getValue(),
                            selectedItemLiveData.getValue().getId(),
                            pinCode
                    ),
                    this::handleTransferResponse
            );
        }
    }

    @Override
    public void calculateCommission() {

    }
}
