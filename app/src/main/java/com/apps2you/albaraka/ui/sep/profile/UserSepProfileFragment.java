package com.apps2you.albaraka.ui.sep.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentSepUserBinding;
import com.apps2you.albaraka.utils.RangeValidator;
import com.apps2you.albaraka.viewmodels.SharedViewModel;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserSepProfileFragment extends Fragment {

    private String fromDate, toDate;
    private SharedViewModel sharedViewModel;
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private FragmentSepUserBinding mViewDataBinding;
    private SEPViewModel sepViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewDataBinding = FragmentSepUserBinding.inflate(inflater, container, false);
        return mViewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sepViewModel = new ViewModelProvider(this).get(SEPViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setUpView();
        listenToVariables();
    }

    private void setUpView() {
        mViewDataBinding.toolbar.setTitle(getString(R.string.sep_profile));

        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    mViewDataBinding.arNameEditText.setText(userData.getArName());
                    mViewDataBinding.addressEditText.setText(userData.getAddress());
                    mViewDataBinding.phoneEditText.setText(userData.getPhone());
                    mViewDataBinding.cifEditText.setText(userData.getCif());
                }
            }
        });

        selectToday();
    }

    private void selectToday() {
        Date todayDate = Calendar.getInstance().getTime();
        fromDate = simpleDateFormat.format(todayDate);
        toDate = simpleDateFormat.format(todayDate);

        String today = getDateString(todayDate.getTime());
        mViewDataBinding.tvFrom.setText(today);
        mViewDataBinding.tvTo.setText(today);
    }

    private MaterialDatePicker<Long> getPickerDialog() {
        Locale locale = new Locale(UserUtils.getInstance(requireContext()).getLanguage().equals("ar") ? "ar_SY" : "en");
        Locale.setDefault(locale);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTheme(R.style.Widget_AppTheme_MaterialDatePicker);

        builder.setCalendarConstraints(limitRange().build());

        MaterialDatePicker<Long> picker = builder.build();

        picker.show(getParentFragmentManager(), picker.toString());
        picker.setCancelable(false);

        return picker;
    }

    private String getDateString(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    private CalendarConstraints.Builder limitRange() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -3);
        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();

        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);

        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));
        return constraintsBuilderRange;
    }

    private void listenToVariables() {
        mViewDataBinding.tvFrom.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {
                fromDate = simpleDateFormat.format(selection);
                mViewDataBinding.tvFrom.setText(getDateString(selection));
            });
        });

        mViewDataBinding.tvTo.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {
                toDate = simpleDateFormat.format(selection);
                mViewDataBinding.tvTo.setText(getDateString(selection));
            });
        });

        mViewDataBinding.iBtnSubmit.setOnClickListener(v -> {
            try {
                Date from = simpleDateFormat.parse(fromDate);
                Date to = simpleDateFormat.parse(toDate);

                if (to != null && from != null) {
                    if (to.before(from)) {
                        showToast(R.string.invalid_date);
                    } else {
                        getTransactions();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void getTransactions() {
        // Implement your transaction fetching logic here
    }

    private void showToast(int resId) {
        // Implement your showToast method here
    }
}
