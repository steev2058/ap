package com.apps2you.albaraka.ui.transfer.accountsTransfer;

import android.view.View;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.FragmentTransferOptionsBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

public class TransferOptionsFragment extends BaseFragment<FragmentTransferOptionsBinding, TransferViewModel> {
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer_options;
    }

    @Override
    public Class<TransferViewModel> setViewModel() {
        return null;
    }

    @Override
    public void setUpView() {

        if (requireActivity().getIntent().getBooleanExtra(TransferActivity.EXTRA_SYGS_ACTIVATED, false))
            getViewDataBinding().layoutSygsTransfer.getRoot().setVisibility(View.VISIBLE);
        else
            getViewDataBinding().layoutSygsTransfer.getRoot().setVisibility(View.GONE);

        if (requireActivity().getIntent().getBooleanExtra(TransferActivity.EXTRA_HF_ACTIVATED, false))
            getViewDataBinding().layoutHaramFouadTransfer.getRoot().setVisibility(View.VISIBLE);
        else
            getViewDataBinding().layoutHaramFouadTransfer.getRoot().setVisibility(View.GONE);

        mViewDataBinding.layoutAlbarakaTransfer.getRoot().setOnClickListener( view ->
                        navigateToAlBarakaTransferFragment()
        );

        mViewDataBinding.layoutMyTransfer.getRoot().setOnClickListener(view ->
                navigateToMyTransferFragment()
        );
        mViewDataBinding.layoutSygsTransfer.getRoot().setOnClickListener(view ->
                navigateToSYGSTransferFragment()
        );

        mViewDataBinding.layoutHaramFouadTransfer.getRoot().setOnClickListener(view ->
                navigateToHFTransferFragment()
        );
    }

    private void navigateToAlBarakaTransferFragment() {
        navController.navigate(
                TransferOptionsFragmentDirections.actionTransferOptionsFragmentToAlBarakaTransferFragment()
        );
    }

    private void navigateToMyTransferFragment() {
        navController.navigate(
                TransferOptionsFragmentDirections.actionTransferOptionsFragmentToMYTransferFragment()
        );
    }
    private void navigateToSYGSTransferFragment() {
        navController.navigate(
                TransferOptionsFragmentDirections.actionTransferOptionsFragmentToSYGSActivity()
        );
    }

    private void navigateToHFTransferFragment() {
        navController.navigate(
                TransferOptionsFragmentDirections.actionTransferOptionsFragmentToHFFragment()
        );
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void createViewModel() {

    }

    @Override
    protected void setupBaseObservers() {

    }
}
