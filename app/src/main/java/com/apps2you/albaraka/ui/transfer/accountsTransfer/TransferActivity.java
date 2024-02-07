package com.apps2you.albaraka.ui.transfer.accountsTransfer;

import android.content.Context;
import android.content.Intent;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityTransferBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

public class TransferActivity extends BaseActivity<ActivityTransferBinding, TransferViewModel> {
    public static final String EXTRA_SELECTED_ACCOUNT_ID = "selected_account_id_extra",
            EXTRA_HF_ACTIVATED = "EXTRA_HF_ACTIVATED",
            EXTRA_SYGS_ACTIVATED = "EXTRA_SYGS_ACTIVATED";



    public static Intent getIntent(Context context,boolean isHFActivated, boolean isSYGSActivated, String accountNumber) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra(EXTRA_SYGS_ACTIVATED, isSYGSActivated);
        intent.putExtra(EXTRA_HF_ACTIVATED, isHFActivated);
        intent.putExtra(EXTRA_SELECTED_ACCOUNT_ID, accountNumber);
        return intent;
    }

    public static Intent getIntent(Context context, boolean isSYGSActivated, boolean isHFActivated) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra(EXTRA_SYGS_ACTIVATED, isSYGSActivated);
        intent.putExtra(EXTRA_HF_ACTIVATED, isHFActivated);
        return intent;
    }


//    public static Intent getIntent2(Context context,boolean isHFActivated, String accountNumber) {
//        Intent intent = new Intent(context, TransferActivity.class);
//
//        intent.putExtra(EXTRA_HF_ACTIVATED, isHFActivated);
//        intent.putExtra(EXTRA_SELECTED_ACCOUNT_ID, accountNumber);
//        return intent;
//    }
//
//    public static Intent getIntent2(Context context, boolean isHFActivated) {
//        Intent intent = new Intent(context, TransferActivity.class);
//
//        intent.putExtra(EXTRA_HF_ACTIVATED, isHFActivated);
//        return intent;
//    }




    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer;
    }

    @Override
    public Class<TransferViewModel> setViewModel() {
        return null;
    }

    @Override
    public void setUpView() {

    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }

    @Override
    protected void setupBaseObservers() {

    }
}
