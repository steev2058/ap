package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentAlbarakaTransferBinding;
import com.apps2you.albaraka.ui.common.busEvent.FavoriteAccountSelectedEvent;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.utils.bus.Bus;
import com.apps2you.albaraka.utils.bus.EventBusObserver;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.viewmodels.transfer.AlBarakaTransferViewModel;

import org.jetbrains.annotations.NotNull;


public class AlBarakaTransferFragment extends BaseTransferFragment<FragmentAlbarakaTransferBinding, AlBarakaTransferViewModel> {

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
        mViewDataBinding.buttonPickContact.setOnClickListener(view -> pickContact());
        mViewDataBinding.buttonAddToFavorites.setOnClickListener(view -> openAddFavoriteDialog());
        mViewDataBinding.buttonFavoriteList.setOnClickListener(view -> navigateToFavoriteAccountsFragment());

        mViewDataBinding.layoutSelectedFavoriteAccount.buttonClose.setOnClickListener(view -> mViewModel.selectFavoriteAccount(null));

    }

    private void transfer() {
        AlBarakaTransferForm transferForm = mViewModel.alBarakaTransferForm;
        if (mViewModel.selectedAccount.getValue() == null) {
            showToast(R.string.you_must_select_account);
        } else if (!transferForm.allowed()) {
            showToast(transferForm.getErrorResource());

            if (transferForm.isCIFSelected() && transferForm.isCIFEmpty()) {
                mViewDataBinding.etCifNumber.requestFocus();
            } else if (!transferForm.isCIFSelected() && transferForm.isGSSMEmpty()) {
                mViewDataBinding.etGsmNumber.requestFocus();
            } else if (transferForm.isAmountEmpty()) {
                mViewDataBinding.etAmount.requestFocus();
            } else if (transferForm.isReasonEmpty()) {
                mViewDataBinding.etReason.requestFocus();
            }
        } else if (transferForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(transferForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(getString(R.string.alBaraka_transfer));
        }
    }

    private void openAddFavoriteDialog() {
        navController.navigate(
                AlBarakaTransferFragmentDirections
                        .actionAlBarakaTransferFragmentToAddFavoriteAccountDialog()
                        .setCIF(mViewModel.alBarakaTransferForm.toCIFNumber.getValue())
                        .setGSM(mViewModel.alBarakaTransferForm.toGSMNumber.getValue())
        );
    }

    private void navigateToFavoriteAccountsFragment() {
        navController.navigate(
                AlBarakaTransferFragmentDirections
                        .actionAlBarakaTransferFragmentToFavoriteAccountsFragment()
        );
    }

    private final EventBusObserver<FavoriteAccountSelectedEvent> onAccountSelected = event -> {
        mViewModel.selectFavoriteAccount(event.getSelectedAccount());
    };

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        Bus.instance().register(FavoriteAccountSelectedEvent.class, onAccountSelected, true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Bus.instance().unregister(onAccountSelected);
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);

        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected void onContactSelected(String number) {
        if (number != null) {
            mViewModel.alBarakaTransferForm.toGSMNumber.setValue(number);
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_albaraka_transfer;
    }

    @Override
    public Class<AlBarakaTransferViewModel> setViewModel() {
        return AlBarakaTransferViewModel.class;
    }
}
