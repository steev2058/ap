package com.apps2you.albaraka.ui.calculator;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Currency;
import com.apps2you.albaraka.data.model.MonthDuration;
import com.apps2you.albaraka.databinding.FragmentProfitsBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.ProfitsCalculatorVM;
import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class ProfitsFragment extends BaseFragment<FragmentProfitsBinding, ProfitsCalculatorVM> implements DatePickerDialog.OnDateSetListener {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profits;
    }

    @Override
    public Class<ProfitsCalculatorVM> setViewModel() {
        return ProfitsCalculatorVM.class;
    }

    @Override
    public void setUpView() {
        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        getViewDataBinding().container.setVisibility(View.GONE); // hide result section by default

        getViewDataBinding().etAmount.addTextChangedListener(new CustomTextWatcher(getViewDataBinding().tiAmount));
        getViewDataBinding().etDate.addTextChangedListener(new CustomTextWatcher(getViewDataBinding().tiDate));

        getViewDataBinding().iBtnDate.setOnClickListener(v -> openDatePicker());
        getViewDataBinding().etDate.setOnClickListener(v -> openDatePicker());


        ArrayList<MonthDuration> durations = new ArrayList<>();
        durations.add(new MonthDuration(6, getString(R.string.months_6)));
        durations.add(new MonthDuration(12, getString(R.string.months_12)));
        durations.add(new MonthDuration(24, getString(R.string.months_24)));
        getViewDataBinding().spinnerMonths.setAdapter(new SpinnerAdapter<>(durations));


        getViewDataBinding().buttonSubmit.setOnClickListener(v -> {
            if (validInput()) {

                View view = getViewDataBinding().etAmount; // hide keypad
                ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), 0);

                getViewModel().calculate(((Currency) getViewDataBinding().spinnerCurrency.getSelectedItem()).getId(),
                        ((MonthDuration) getViewDataBinding().spinnerMonths.getSelectedItem()).getId(),
                        getViewDataBinding().etAmount.getText().toString(),
                        getViewDataBinding().etDate.getText().toString())
                        .observe(this, resource -> {

                            switch (resource.status) {
                                case LOADING:
                                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                                    getViewDataBinding().container.setVisibility(View.GONE);
                                    break;

                                case SUCCESS:
                                    getViewDataBinding().container.setVisibility(View.VISIBLE);
                                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                                    getViewDataBinding().setResult(resource.data.getDetails());

                                    getViewDataBinding().tvCalculatorMsg.setText(resource.data.getCalculatorMessage());
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
        getViewModel().getProfitsCurrencies().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().layoutForm.setVisibility(View.GONE);
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    getViewDataBinding().layoutForm.setVisibility(View.VISIBLE);
                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                    getViewDataBinding().spinnerCurrency.setAdapter(new SpinnerAdapter<>(resource.data));
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year
                + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1))
                + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
        getViewDataBinding().etDate.setText(date);
    }

    private boolean validInput() {
        if (TextUtils.isEmpty(getViewDataBinding().etAmount.getText()))
            setInputError(getViewDataBinding().tiAmount, getString(R.string.error_required));
        else
            return true;
        return false;
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
}
