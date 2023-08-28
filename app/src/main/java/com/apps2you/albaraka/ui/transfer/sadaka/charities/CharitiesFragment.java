package com.apps2you.albaraka.ui.transfer.sadaka.charities;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.CharityUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.SadakaViewModel;

public class CharitiesFragment extends BaseSelectionFragment<CharityUI, SadakaViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.charities);
    }

    @Override
    public Class<SadakaViewModel> setViewModel() {
        return SadakaViewModel.class;
    }
}
