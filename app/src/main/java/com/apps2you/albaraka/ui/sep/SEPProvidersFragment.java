package com.apps2you.albaraka.ui.sep;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;
import com.apps2you.albaraka.ui.common.model.SEPProviderUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.ADSLViewModel;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;

public class SEPProvidersFragment extends BaseSelectionFragment<SEPProviderUI, SEPViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.SEP_payment);
    }

    @Override
    public Class<SEPViewModel> setViewModel() {
        return SEPViewModel.class;
    }
}
