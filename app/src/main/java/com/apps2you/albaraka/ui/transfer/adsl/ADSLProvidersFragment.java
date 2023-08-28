package com.apps2you.albaraka.ui.transfer.adsl;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.ADSLViewModel;

public class ADSLProvidersFragment extends BaseSelectionFragment<ADSLProviderUI, ADSLViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.ADSL_payment);
    }

    @Override
    public Class<ADSLViewModel> setViewModel() {
        return ADSLViewModel.class;
    }
}
