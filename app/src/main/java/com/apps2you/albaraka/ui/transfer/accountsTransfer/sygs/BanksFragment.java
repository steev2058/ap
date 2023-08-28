package com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.BankUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.SYGSTransferViewModel;

import java.util.ArrayList;

public class BanksFragment extends BaseSelectionFragment<BankUI, SYGSTransferViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.choose_bank);
    }

    @Override
    public Class<SYGSTransferViewModel> setViewModel() {
        return SYGSTransferViewModel.class;
    }

    @Override
    public void fetchData() {
        if (getArguments() != null)
        {
            ArrayList<Partner> partners =(ArrayList<Partner>) getArguments().getSerializable("banks");
            ArrayList<BankUI> banks = new ArrayList<>();
            for(Partner partner: partners){
                banks.add(new BankUI(partner.getId(), partner.getName(), partner.getLogo(), partner.getCode()));
            }
            mViewModel.handleData(banks);
            mViewModel.dataList.observe(getViewLifecycleOwner(), data -> {
                itemSelectionAdapter.submitData(data);
            });
        }
    }
}
