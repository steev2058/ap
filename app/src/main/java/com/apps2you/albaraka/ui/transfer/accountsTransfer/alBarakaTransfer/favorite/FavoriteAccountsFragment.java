package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.databinding.FragmentFavoriteAccountsBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.common.busEvent.FavoriteAccountSelectedEvent;
import com.apps2you.albaraka.utils.bus.Bus;
import com.apps2you.albaraka.viewmodels.transfer.FavoriteAccountsViewModel;

public class FavoriteAccountsFragment extends BaseFragment<FragmentFavoriteAccountsBinding, FavoriteAccountsViewModel> {

    @Override
    public void setUpView() {
        setupFavoriteAccountsRecycler();
    }

    private final OnItemClickListener<FavoriteAccount> onFavoriteAccountSelected = (item, position) -> {
        Bus.instance().publish(new FavoriteAccountSelectedEvent(item));
        navController.popBackStack();
    };

    private final OnItemClickListener<FavoriteAccount> onFavoriteDeleteClicked = (item, position) -> {
        showDialogMessage(
                getString(R.string.delete_account_title),
                getString(R.string.delete_account_msg),
                getString(R.string.Yes),
                getString(R.string.No),
                (dialogInterface, i) -> mViewModel.deleteFavoriteAccount(item),
                (dialogInterface, i) -> dialogInterface.dismiss()
        );
    };

    private final OnItemClickListener<FavoriteAccount> onFavoriteEditClicked = (item, position) -> {
        navController.navigate(
                FavoriteAccountsFragmentDirections
                        .actionFavoriteAccountsFragmentToUpdateFavoriteAccountDialog()
                        .setFavoriteAccount(item)
        );
    };

    private void setupFavoriteAccountsRecycler() {
        FavoriteAccountListAdapter favoriteAccountListAdapter =
                new FavoriteAccountListAdapter(
                        requireContext(),
                        onFavoriteAccountSelected,
                        onFavoriteDeleteClicked,
                        onFavoriteEditClicked
                );

        mViewDataBinding.recyclerViewFavoriteAccounts.setAdapter(favoriteAccountListAdapter);
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    public void fetchData() {
        mViewModel.fetchMyFavoriteAccounts();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_favorite_accounts;
    }

    @Override
    public Class<FavoriteAccountsViewModel> setViewModel() {
        return FavoriteAccountsViewModel.class;
    }
}
