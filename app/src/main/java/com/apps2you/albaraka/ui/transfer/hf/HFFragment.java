package com.apps2you.albaraka.ui.transfer.hf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.databinding.FragmentHfBinding;
import com.apps2you.albaraka.ui.common.model.HFProviderUI;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSTransferPreviewDialog;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.viewmodels.transfer.HFViewModel;

import java.util.ArrayList;


public class HFFragment extends BaseTransferFragment<FragmentHfBinding, HFViewModel> {
    ArrayList<City> cities =new ArrayList<>();
    SpinnerAdapter<City> citySpinnerAdapter;


    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));
        getViewDataBinding().etFirstName.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etFirstName));
        getViewDataBinding().etSecondName.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etSecondName));
        getViewDataBinding().etThirdName.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etThirdName));

        mViewDataBinding.buttonPickProvider.setOnClickListener(view -> openHFProvidersFragment());
        mViewDataBinding.buttonSubmit.setOnClickListener(view ->  transfer());
        cities.add(0,new City(-1,getResources().getString(R.string.city),""));
        citySpinnerAdapter=new SpinnerAdapter<>(cities);
        mViewDataBinding.citiesSpinner.setAdapter(citySpinnerAdapter);

        mViewModel.citiesList.observe(getViewLifecycleOwner(), data -> {
            cities.clear();
            cities.add(0,new City(-1,getResources().getString(R.string.city),""));
            cities.addAll(data);
            citySpinnerAdapter.refreshList(cities);
        });

        mViewModel.selectedItemLiveData.observe(getViewLifecycleOwner(), hfProviderUI -> {
            if (hfProviderUI != null) {
                mViewDataBinding.buttonPickProvider.setText(hfProviderUI.getName());
            }
        });
    }

    private void openHFProvidersFragment(){


        navController.navigate(
                HFFragmentDirections.actionHfFragmentToProvidersFragment()
        );
    }

    public void transfer(){
        HFForm HFForm = mViewModel.hfForm;

        Account selectedAccount = mViewModel.selectedAccount.getValue();
        HFProviderUI selectedProvider = mViewModel.getSelectedItem();


        if (selectedAccount == null) {
            showToast(R.string.you_must_select_account);

        } else if (selectedProvider == null) {
            mViewDataBinding.buttonPickProvider.requestFocus();
            showToast(R.string.you_must_select_provider_hf);
        }
        else if(((City)mViewDataBinding.citiesSpinner.getSelectedItem()).getId()==-1) {
            mViewDataBinding.citiesSpinner.requestFocus();
            showToast(R.string.you_must_select_city);
        }
        else if (HFForm.isBFirstNameEmpty()) {
            showToast(R.string.you_must_select_fname);
            mViewDataBinding.etFirstName.requestFocus();
        }
        else if (HFForm.isBSecNameEmpty()) {
            showToast(R.string.you_must_select_secname);
            mViewDataBinding.etSecondName.requestFocus();
        }
        else if (HFForm.isBLastNameEmpty()) {
            showToast(R.string.you_must_select_lastname);
            mViewDataBinding.etThirdName.requestFocus();
        }

        else if (HFForm.phoneNumber.getLocalizedNumber().length()!=10) {
            showToast(R.string.you_must_select_phonenumber);
            mViewDataBinding.etPhone.requestFocus();
        }
        else if (HFForm.isAmountEmpty()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        }

        else if (HFForm.isReasonEmpty()) {
            showToast(R.string.you_must_select_reason);
            mViewDataBinding.etReason.requestFocus();
        }

        else if (!thereIsEnoughBalance(HFForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        }else if (HFForm.isAmountInvalid(mViewModel.getMinLimit(),mViewModel.getMaxLimit())) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        }

        else {
            mViewModel.setSelectedCity((City)mViewDataBinding.citiesSpinner.getSelectedItem());

            nextStep(getString(R.string.HF_payment),null);
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
    private void openTransferPreviewDialog() {
        HFTransferPreviewDialog.show(getChildFragmentManager());
    }
    @Override
    public void fetchData() {
        super.fetchData();

        mViewModel.commissionFetched
                .observe(
                        getViewLifecycleOwner(),
                        new EventObserver<>(result -> {
                            if (result) openTransferPreviewDialog();
                        })
                );
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
        return R.layout.fragment_hf;
    }

    @Override
    public Class<HFViewModel> setViewModel() {
        return HFViewModel.class;
    }

}
