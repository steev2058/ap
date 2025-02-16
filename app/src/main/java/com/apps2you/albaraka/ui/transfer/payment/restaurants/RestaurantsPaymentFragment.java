package com.apps2you.albaraka.ui.transfer.payment.restaurants;

import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentRestuaurantPaymentBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.payment.restaurants.RestaurantsPaymentViewModel;

public class RestaurantsPaymentFragment extends BaseTransferFragment<FragmentRestuaurantPaymentBinding, RestaurantsPaymentViewModel> {

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));
        getViewDataBinding().etTips.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etTips));

        mViewDataBinding.buttonPickRestaurant.setOnClickListener(view -> navigateToRestaurantSelectionFragment());
        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    private void navigateToRestaurantSelectionFragment() {
        navController.navigate(
                RestaurantsPaymentFragmentDirections
                        .actionRestaurantsPaymentFragmentToRestaurantSelectionFragment()
        );
    }

    private void transfer() {
        RestaurantsPaymentForm form = mViewModel.form;

        if (mViewModel.getSelectedItem() == null) {
            showToast(R.string.you_must_select_restaurant);
        } else if (!form.allowed()) {
            showToast(form.getErrorResource());
        } else if (form.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(form.amount.getValue(), form.tips.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(getString(R.string.restaurants_payment),null);
        }
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return requireActivity();
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_restuaurant_payment;
    }

    @Override
    public Class<RestaurantsPaymentViewModel> setViewModel() {
        return RestaurantsPaymentViewModel.class;
    }
}
