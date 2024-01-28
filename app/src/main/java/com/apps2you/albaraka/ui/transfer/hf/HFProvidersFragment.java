package com.apps2you.albaraka.ui.transfer.hf;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.HFProviderUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.HFViewModel;


public class HFProvidersFragment extends BaseSelectionFragment<HFProviderUI, HFViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.HF_payment);
    }

    @Override
    public Class<HFViewModel> setViewModel() {
        return HFViewModel.class;
    }
}
