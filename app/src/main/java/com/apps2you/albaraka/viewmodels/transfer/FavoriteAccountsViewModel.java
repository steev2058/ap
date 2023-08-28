package com.apps2you.albaraka.viewmodels.transfer;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.data.remote.repository.TransferRepository;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.ui.common.list.listlivedata.ListLiveData;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ListEvent;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs.AddFavoriteAccountForm;
import com.apps2you.albaraka.utils.lifecyle.Event;

import javax.inject.Inject;

public class FavoriteAccountsViewModel extends BaseViewModel {
    private final TransferRepository transferRepository;

    public final AddFavoriteAccountForm addFavoriteAccountForm = new AddFavoriteAccountForm();

    private final ListLiveData<FavoriteAccount> _myFavoriteAccounts = new ListLiveData<>();
    public final LiveData<ListEvent<FavoriteAccount>> myFavoriteAccounts = _myFavoriteAccounts;

    private final MediatorLiveData<Event<Boolean>> _addFavoriteAccountStatus = new MediatorLiveData<>();
    public final LiveData<Event<Boolean>> addFavoriteAccountStatus = _addFavoriteAccountStatus;

    private final MediatorLiveData<Event<Boolean>> _updateFavoriteAccountStatus = new MediatorLiveData<>();
    public final LiveData<Event<Boolean>> updateFavoriteAccountStatus = _updateFavoriteAccountStatus;
    @Inject
    public FavoriteAccountsViewModel(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public void fetchMyFavoriteAccounts() {
        if (isContentLoadingValue()) {
            return;
        }
        _myFavoriteAccounts.addSource(
                transferRepository.getMyFavoriteAccounts(),
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
                            _myFavoriteAccounts.replaceData(resource.data);
                            break;
                    }
                }
        );
    }

    public void addFavoriteAccount() {
        if (isContentLoadingValue()) {
            return;
        }
        hideKeyboard();

        boolean thereIsCIF = !TextUtils.isEmpty(addFavoriteAccountForm.accountCIF.getValue());
        boolean thereIsGSM = !TextUtils.isEmpty(addFavoriteAccountForm.accountGSM.getValue());

        _addFavoriteAccountStatus.addSource(
                transferRepository.addFavoriteAccount(addFavoriteAccountForm.accountName.getValue(),
                        thereIsCIF ? addFavoriteAccountForm.accountCIF.getValue() : null,
                        thereIsGSM ? addFavoriteAccountForm.accountGSM.getValue() : null),
                resource -> {
                    startContentLoading();
                    switch (resource.status) {
                        case LOADING:
                            startContentLoading();
                            break;

                        case ERROR:
                            showMessage(resource.message);
                            break;

                        case SUCCESS:
                            showMessage(resource.message);
                            _addFavoriteAccountStatus.setValue(Event.of(true));
                            break;
                    }
                }
        );
    }

    public void updateFavoriteAccount(FavoriteAccount favoriteAccount) {
        if (isContentLoadingValue() || favoriteAccount == null)
            return;

        FavoriteAccount updatedFavoriteAccount =
                new FavoriteAccount(favoriteAccount.getId(),
                        addFavoriteAccountForm.accountName.getValue(),
                        addFavoriteAccountForm.accountGSM.getValue(),
                        addFavoriteAccountForm.accountCIF.getValue(),
                        favoriteAccount.getClientId()
                );

        _myFavoriteAccounts.addSource(
                transferRepository.updateFavoriteAccount(favoriteAccount.getId(), updatedFavoriteAccount),
                resource -> {
                    stopContentLoading();
                    switch (resource.status) {
                        case LOADING:
                            startContentLoading();
                            break;
                        case ERROR:
                            showMessage(resource.message);
                            break;
                        case SUCCESS:
                            showMessage(resource.message);
                            _myFavoriteAccounts.update(updatedFavoriteAccount);
                            _updateFavoriteAccountStatus.setValue(Event.of(true));
                            break;
                    }
                }
        );
    }

    public void deleteFavoriteAccount(FavoriteAccount favoriteAccount) {
        if (isContentLoadingValue())
            return;
        _myFavoriteAccounts.addSource(
                transferRepository.deleteFavoriteAccount(favoriteAccount.getId()),
                resource -> {
                    stopContentLoading();
                    switch (resource.status) {
                        case LOADING:
                            startContentLoading();
                            break;
                        case ERROR:
                            showMessage(resource.message);
                            break;
                        case SUCCESS:
                            int index = _myFavoriteAccounts.getData().indexOf(favoriteAccount);
                            _myFavoriteAccounts.remove(index);

                            showMessage(resource.message);
                            break;
                    }
                }
        );
    }
}
