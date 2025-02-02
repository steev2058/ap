package com.apps2you.albaraka.ui.transfer.adsl;

import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.databinding.FragmentAdslBinding;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.ADSLViewModel;

import java.util.ArrayList;

public class ADSLFragment extends BaseTransferFragment<FragmentAdslBinding, ADSLViewModel> {
    ArrayList<City> cities =new ArrayList<>();
    SpinnerAdapter<City> citySpinnerAdapter;
    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonPickProvider.setOnClickListener(view -> openADSLProvidersFragment());
        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
        cities.add(0,new City(-1,getResources().getString(R.string.city),""));
        citySpinnerAdapter=new SpinnerAdapter<>(cities);
        mViewDataBinding.citiesSpinner.setAdapter(citySpinnerAdapter);

        mViewModel.citiesList.observe(getViewLifecycleOwner(), data -> {
            cities.clear();
            cities.add(0,new City(-1,getResources().getString(R.string.city),""));
            cities.addAll(data);
            citySpinnerAdapter.refreshList(cities);
        });
    }

    private void openADSLProvidersFragment(){
        navController.navigate(
                ADSLFragmentDirections.actionAdslFragmentToProvidersFragment()
        );
    }

    public void transfer(){
        ADSLForm ADSLForm = mViewModel.adslForm;

        Account selectedAccount = mViewModel.selectedAccount.getValue();
        ADSLProviderUI selectedProvider = mViewModel.getSelectedItem();


        if (selectedAccount == null) {
            showToast(R.string.you_must_select_account);
        } else if (selectedProvider == null) {
            showToast(R.string.you_must_select_provider);
        }else if(((City)mViewDataBinding.citiesSpinner.getSelectedItem()).getId()==-1) {
            showToast(R.string.you_must_select_city);
        }else if (!ADSLForm.allowed()) {
            showToast(ADSLForm.getErrorResource());
            if (ADSLForm.isAmountEmpty()) {
                mViewDataBinding.etAmount.requestFocus();
            } else if (ADSLForm.isPhoneEmpty()) {
                mViewDataBinding.etPhone.requestFocus();
            }else if (ADSLForm.isReasonEmpty()) {
                mViewDataBinding.etReason.requestFocus();
            }
        } else if (ADSLForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(ADSLForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            mViewModel.setSelectedCity((City)mViewDataBinding.citiesSpinner.getSelectedItem());

            nextStep(getString(R.string.ADSL_payment),null);
        }
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewModel.stopContentLoading();
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_adsl;
    }

    @Override
    public Class<ADSLViewModel> setViewModel() {
        return ADSLViewModel.class;
    }

}
