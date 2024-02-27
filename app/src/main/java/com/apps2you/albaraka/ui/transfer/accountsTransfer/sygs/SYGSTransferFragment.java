package com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.data.model.SYGSTransferType;
import com.apps2you.albaraka.data.model.SYGSType;
import com.apps2you.albaraka.databinding.DialogTermsConfirmBinding;
import com.apps2you.albaraka.databinding.FragmentBanksTransferBinding;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.BindingUtils;
import com.apps2you.albaraka.utils.EnglishLettersAndDigitsFilter;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.viewmodels.transfer.SYGSTransferViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class SYGSTransferFragment extends BaseTransferFragment<FragmentBanksTransferBinding, SYGSTransferViewModel>
        implements DatePickerDialog.OnDateSetListener {

    private boolean typesSpinnerTouched;

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonPickBank.setOnClickListener(view -> openBanksFragment());
        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
        mViewDataBinding.etContractDate.setOnClickListener(view -> openDatePicker());
        mViewDataBinding.etPropertyContractDate.setOnClickListener(view -> openDatePicker());
        mViewDataBinding.etVehicleContractDate.setOnClickListener(view -> openDatePicker());

        EnglishLettersAndDigitsFilter accountNumberFilter =
                new EnglishLettersAndDigitsFilter(
                        () -> showToast(R.string.msg_only_english_chars_and_numbers_allowed)
                );

        mViewDataBinding.etAccountNumber.setFilters(new InputFilter[]{ accountNumberFilter });


        setupTransferTypeSpinner();
    }

    private void openBanksFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("banks", new ArrayList<>(getViewModel().sygsData.getValue().getBanks()));
        navController.navigate(R.id.banksFragment, bundle);
//        navController.navigate(
//                SYGSTransferFragmentDirections.actionSygsTransferFragmentToBanksFragment(),bundle
//        );
    }

    private void transfer() {
        SYGSTransferForm form = mViewModel.form;

        if (mViewModel.selectedAccount.getValue() == null) {
            showToast(R.string.you_must_select_account);
        } else if (mViewModel.getSelectedItem() == null) {
            showToast(R.string.err_select_bank);
        } else if (mViewModel.selectedTransferType.getValue() == null) {
            showToast(R.string.err_select_type);
        } else {
            form.setType(SYGSType.getTypeById(mViewModel.selectedTransferType.getValue().getId()));

            if (!form.allowed()) {
                if (form.status.getValue() == SYGSTransferForm.FormStatus.ERROR_CONTRACT_DATE) {
                    showToast(R.string.err_select_date);
                } else showToast(form.getErrorResource());
            } else if (!isAmountValid(form.amount.getLocalizedNumber())) {
                showToast(R.string.invalid_amount);

            } else if (form.type == SYGSType.Vehicles && (form.chassisNo.getValue() == null || form.chassisNo.getValue().length() != 6)) {
                showToast(R.string.invalid_chassis_no);
            } else if (!thereIsEnoughBalance(form.amount.getValue())) {

                showToast(R.string.balance_msg);

            } else {
                openTransferTermsDialog();
            }
        }

    }

    private boolean isAmountValid(String amount) {
        return new BigDecimal(amount).compareTo(BigDecimal.ZERO) > 0
                && new BigDecimal(amount).compareTo(BigDecimal.valueOf(mViewModel.selectedTransferType.getValue().getMinLimit())) >= 0
                && new BigDecimal(amount).compareTo(BigDecimal.valueOf(mViewModel.selectedTransferType.getValue().getMaxLimit())) <= 0;
    }


    private void openTransferTermsDialog() {
        Dialog dialog = new Dialog(getContext());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
           // dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogTermsConfirmBinding dialogDataBinding = DialogTermsConfirmBinding.inflate(LayoutInflater.from(getContext()),
                null,
                false);

        dialog.setContentView(dialogDataBinding.getRoot());
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, mViewModel.sygsData.getValue().getTerms());

//        dialogDataBinding.buttonSubmit.setEnabled(false);

        dialogDataBinding.text.setText(Html.fromHtml(mViewModel.sygsData.getValue().getTerms()));

//        dialogDataBinding.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            dialogDataBinding.buttonSubmit.setEnabled(
//                    !dialogDataBinding.scrollView.canScrollVertically(1)
//            );
//        });

//        dialogDataBinding.scrollView.getViewTreeObserver()
//                .addOnScrollChangedListener(() -> {
//                    dialogDataBinding.buttonSubmit.setEnabled(
//                            !dialogDataBinding.scrollView.canScrollVertically(1)
//                    );
//                });

        dialog.show();

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            mViewModel.calculateCommission();
        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialogDataBinding.btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupTransferTypeSpinner() {
        mViewDataBinding.spinnerSygsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SYGSTransferType selectedItem = (SYGSTransferType) mViewDataBinding.spinnerSygsType.getSelectedItem();
                if (typesSpinnerTouched) {
                    if (mViewModel.selectedTransferType.getValue() != null
                            && mViewModel.selectedTransferType.getValue() != selectedItem) {
                        mViewModel.resetForm();
                    }
                    mViewModel.selectTransferType(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mViewDataBinding.spinnerSygsType.setOnTouchListener((view, motionEvent) -> {
            typesSpinnerTouched = true;
            return false;
        });

        mViewModel.sygsData.observe(getViewLifecycleOwner(), data -> {
            SpinnerAdapter<SYGSTransferType> spinnerAdapter = new SpinnerAdapter<>(data.getTypes());
            mViewDataBinding.spinnerSygsType.setAdapter(spinnerAdapter);

            SYGSTransferType selectedTransferType = mViewModel.selectedTransferType.getValue();
            if (selectedTransferType != null) {
                int selectedItemPos = spinnerAdapter.positionOf(selectedTransferType);
                if (selectedItemPos != -1) {
                    mViewDataBinding.spinnerSygsType.setSelection(selectedItemPos);
                }
            }
        });
    }

    private void openTransferPreviewDialog() {
        SYGSTransferPreviewDialog.show(getChildFragmentManager());
    }

//    private void setupPaymentCategorySpinner() {
//        mViewDataBinding.spinnerPaymentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                mViewModel.selectPaymentCategory((PaymentCategory) mViewDataBinding.spinnerPaymentCategory.getSelectedItem());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }

    @Override
    public void fetchData() {
        super.fetchData();

//        mViewModel.fetchSYGSData();
//        mViewModel.sygsData.observe(getViewLifecycleOwner(), new Observer<SYGSData>() {
//            @Override
//            public void onChanged(SYGSData sygsData) {
//                Log.e("sygs data","");
//
//            }
//        });

        mViewModel.paymentConfirmed.observe(getViewLifecycleOwner(),
                new EventObserver<>(
                        confirmed -> {
                            if (confirmed)
                                openConfirmPinDialog();
                        }
                )
        );


        mViewModel.commissionFetched
                .observe(
                        getViewLifecycleOwner(),
                        new EventObserver<>(result -> {
                            if (result) openTransferPreviewDialog();
                        })
                );
    }

    @Override
    public void refresh() {
        super.refresh();
        mViewModel.fetchSYGSData();
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
        return R.layout.fragment_banks_transfer;
    }

    @Override
    public Class<SYGSTransferViewModel> setViewModel() {
        return SYGSTransferViewModel.class;
    }


    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    private void openDatePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setOkText(getString(R.string.action_ok));
        datePickerDialog.setCancelText(getString(R.string.prompt_info_cancel));
        datePickerDialog.setCancelColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        datePickerDialog.setOkColor(ContextCompat.getColor(getContext(), R.color.colorAccent));


        datePickerDialog.show(getActivity().getSupportFragmentManager(), DatePickerDialog.class.getName());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        mViewModel.form.contractDate.setValue(sdf.format(calendar.getTime()));
    }
}
