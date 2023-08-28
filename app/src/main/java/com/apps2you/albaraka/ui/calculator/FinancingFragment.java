package com.apps2you.albaraka.ui.calculator;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.FinancingType;
import com.apps2you.albaraka.databinding.FragmentFinancingBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.FinancingCalculatorVM;
import com.google.android.material.snackbar.Snackbar;


public class FinancingFragment extends BaseFragment<FragmentFinancingBinding, FinancingCalculatorVM> {


    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_financing;
    }

    @Override
    public Class<FinancingCalculatorVM> setViewModel() {
        return FinancingCalculatorVM.class;
    }

    @Override
    public void setUpView() {
        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        getViewDataBinding().container.setVisibility(View.GONE); // hide result section by default

        getViewDataBinding().etDate.addTextChangedListener(new CustomTextWatcher(getViewDataBinding().tiDate));
        getViewDataBinding().etAmount.addTextChangedListener(new CustomTextWatcher(getViewDataBinding().tiAmount));

        getViewDataBinding().buttonSubmit.setOnClickListener(v -> {

            if (validInput()) {

                View view = getViewDataBinding().etAmount; // hide keypad
                ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), 0);

                getViewModel().calculate(((FinancingType) getViewDataBinding().spinnerType.getSelectedItem()).getId(),
                        getViewDataBinding().etDate.getText().toString(),
                        getViewDataBinding().etAmount.getText().toString())
                        .observe(this, resource -> {

                            switch (resource.status) {
                                case LOADING:
                                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                                    getViewDataBinding().container.setVisibility(View.GONE);
                                    break;

                                case SUCCESS:
                                    getViewDataBinding().container.setVisibility(View.VISIBLE);
                                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                                    if (resource.data != null) {
                                        getViewDataBinding().setResult(resource.data.getDetails());
                                        getViewDataBinding().tvCalculatorMsg.setText(resource.data.getCalculatorMessage());
                                    }
                                    break;

                                case ERROR:
                                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                                    showToast(resource.message);
                                    break;
                            }
                        });
            }
        });
    }

    @Override
    public void fetchData() {

        getViewModel().getFinancingTypes().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().layoutForm.setVisibility(View.GONE);
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    getViewDataBinding().layoutForm.setVisibility(View.VISIBLE);
                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                    getViewDataBinding().spinnerType.setAdapter(new SpinnerAdapter<>(resource.data));
                    break;

                case ERROR:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                    Snackbar.make(getViewDataBinding().layoutForm, resource.message, Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(getResources().getColor(R.color.white))
                            .setAction(getString(R.string.retry), view -> fetchData())
                            .show();
                    break;
            }
        });
    }

    private boolean validInput() {
        if (TextUtils.isEmpty(getViewDataBinding().etDate.getText()))
            setInputError(getViewDataBinding().tiDate, getString(R.string.error_required));
        else if (TextUtils.isEmpty(getViewDataBinding().etAmount.getText()))
            setInputError(getViewDataBinding().tiAmount, getString(R.string.error_required));
        else
            return true;
        return false;
    }
}
