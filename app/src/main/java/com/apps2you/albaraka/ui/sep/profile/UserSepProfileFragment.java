package com.apps2you.albaraka.ui.sep.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.databinding.FragmentSepUserBinding;
import com.apps2you.albaraka.ui.sep.tabs.AddPaymentDialogFragment;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.RangeValidator;
import com.apps2you.albaraka.viewmodels.SharedViewModel;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class UserSepProfileFragment extends Fragment {

    private String fromDate, toDate;

    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private SweetAlertDialog progressDialog;
    private SEPViewModel sepViewModel;
    private FragmentSepUserBinding binding;
    private RecyclerView recyclerViewPayments;
    private PaymentsAdapter paymentsAdapter;

    private List<JSONObject> paymentsList = new ArrayList<>();

    private String  selectedBillerName = "اختر مفوتر";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sep_user, container, false);
        sepViewModel = new ViewModelProvider(requireActivity()).get(SEPViewModel.class);

        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
      //   progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        progressDialog.setContentText(getString(R.string.loading));
        progressDialog.setCancelable(false);

        setBackButtonAction(binding.getRoot());
        selectToday();
        View rootView = binding.getRoot();
        recyclerViewPayments = rootView.findViewById(R.id.recycler_view_payments);
        recyclerViewPayments.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Assuming paymentsList is already defined and populated
        paymentsAdapter = new PaymentsAdapter(requireContext(),new ArrayList<>());
        recyclerViewPayments.setAdapter(paymentsAdapter);

//        Button addButton = rootView.findViewById(R.id.add_button);
//        addButton.setOnClickListener(v -> {
//            AddPaymentDialogFragment dialogFragment = new AddPaymentDialogFragment(sharedViewModel);
//            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
//            dialogFragment.show(ft, "add_payment_dialog");
//        });

        if (getArguments() != null) {
            String arName = getArguments().getString("arName");
            String cif = getArguments().getString("cif");
            String phone = getArguments().getString("phone");
            String address = getArguments().getString("address");
            String token = getArguments().getString("token");

            sepViewModel.arName.setValue(arName);
            sepViewModel.cif.setValue(cif);
            sepViewModel.phone.setValue(phone);
            sepViewModel.address.setValue(address);
            sepViewModel.token.setValue(token);
        }
        binding.setViewModel(sepViewModel);
        binding.setLifecycleOwner(this);

        new FetchPaymentsTask().execute(sepViewModel.token.getValue());
        new FetchCategoriesTask().execute();
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenToVariables();

    }
    protected void showProgress() {
        progressDialog.show();
    }
    protected void hideProgress() {
        progressDialog.dismiss();
    }
    protected void onBackPressed() {
        requireActivity().onBackPressed();
    }
    private void setBackButtonAction(View view) {
        try {
            view.findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());
        } catch (Exception ignored) {
        }
    }

    private void selectToday() {
        Date todayDate = Calendar.getInstance().getTime();
        fromDate = simpleDateFormat.format(todayDate);
        toDate = simpleDateFormat.format(todayDate);

        String today = getDateString(todayDate.getTime());
        binding.tvFrom.setText(today);
        binding.tvTo.setText(today);
    }

    private MaterialDatePicker<Long> getPickerDialog() {
        Locale locale = new Locale(UserUtils.getInstance(requireContext()).getLanguage().equals("ar") ? "ar_SY" : "en");
        Locale.setDefault(locale);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTheme(R.style.Widget_AppTheme_MaterialDatePicker);

     //   builder.setCalendarConstraints(limitRange().build());

        MaterialDatePicker<Long> picker = builder.build();

        picker.show(getParentFragmentManager(), picker.toString());
        picker.setCancelable(false);

        return picker;
    }

    private String getDateString(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

//    private CalendarConstraints.Builder limitRange() {
//        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
//
//        Calendar calendarStart = Calendar.getInstance();
//        Calendar calendarEnd = Calendar.getInstance();
//        calendarStart.add(Calendar.MONTH, -3);
//        long minDate = calendarStart.getTimeInMillis();
//        long maxDate = calendarEnd.getTimeInMillis();
//
//        constraintsBuilderRange.setStart(minDate);
//        constraintsBuilderRange.setEnd(maxDate);
//
//        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));
//        return constraintsBuilderRange;
//    }

    private void listenToVariables() {
        binding.tvFrom.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {
                fromDate = simpleDateFormat.format(selection);
                binding.tvFrom.setText(getDateString(selection));
            });
        });

        binding.tvTo.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = getPickerDialog();
            picker.addOnPositiveButtonClickListener(selection -> {
                toDate = simpleDateFormat.format(selection);
                binding.tvTo.setText(getDateString(selection));
            });
        });


        binding.iBtnSubmit.setOnClickListener(v -> {

            displayPayments( filterPayments(paymentsList, fromDate, toDate, selectedBillerName));

        });

    }

    private void getTransactions() {
        // Implement your transaction fetching logic here
    }

    private void showToast(int resId) {
        // Implement your showToast method here
    }
    private void displayPayments(List<JSONObject> payments) {
        paymentsAdapter = new PaymentsAdapter(requireContext(),payments);
        recyclerViewPayments.setAdapter(paymentsAdapter);
        paymentsAdapter.notifyDataSetChanged();
    }
    private class FetchCategoriesTask extends AsyncTask<Void, Void, String> {
        String language = UserUtils.getInstance(requireContext()).getLanguage();
        OkHttpClient client = NetworkBoundResource.provideOkHttpClient();  // Initialize OkHttpClient

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String apiUrl = Constants.BASE_URL_SEP + "/Customer/all/";

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + sepViewModel.token.getValue())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "Error: Empty response";
                } else {
                    return "Error: " + response.code();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideProgress();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                JSONArray categoriesArray = dataObject.getJSONArray("categories");

                List<String> billerNamesArList = new ArrayList<>();
                String c2 = language.equals("ar") ? "اختر مفوتر" : "Choose Biller";
                billerNamesArList.add(c2);

                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryObject = categoriesArray.getJSONObject(i);
                    JSONArray billersArray = categoryObject.getJSONArray("billers");

                    for (int j = 0; j < billersArray.length(); j++) {
                        JSONObject billerObject = billersArray.getJSONObject(j);
                        String billerName = language.equals("ar") ?
                                billerObject.optString("billerName_ar", "N/A") :
                                billerObject.optString("billerName_en", "N/A");
                        billerNamesArList.add(billerName);
                    }
                }

                // Populate spinner_categories with biller names
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, billerNamesArList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategories.setAdapter(adapter);

                // Handle spinner item selection
                binding.spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedBillerName = (String) parent.getItemAtPosition(position);
                        if (selectedBillerName.equals("اختر مفوتر")) {
                            displayPayments(paymentsList);
                        } else {
                            List<JSONObject> filteredPayments = filterPayments(paymentsList, fromDate, toDate, selectedBillerName);
                            displayPayments(filteredPayments);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle nothing selected logic here
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "الخادم خارج الخدمة", Toast.LENGTH_LONG).show();
            }
        }
    }


    private List<JSONObject> filterPayments(List<JSONObject> payments, String fromDate, String toDate, String selectedBillerName) {
        List<JSONObject> filteredPayments = new ArrayList<>();
        try {
            Toast.makeText(requireContext(), "جاري فلترة البيانات", Toast.LENGTH_LONG).show();
            Date from = simpleDateFormat.parse(fromDate);
            Date to = simpleDateFormat.parse(toDate);

            for (JSONObject payment : payments) {
                String paymentDateStr = payment.getString("payment_date");
                Date paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(paymentDateStr);

                String billerNameAr = payment.getString("billerName_ar");
                String billerName = payment.getString("billerName_en");

                if ((paymentDate.equals(from) || paymentDate.after(from)) && (paymentDate.equals(to) || paymentDate.before(to))) {
                    if (selectedBillerName.equals("اختر مفوتر") || billerNameAr.equals(selectedBillerName) || billerName.equals(selectedBillerName)) {
                        filteredPayments.add(payment);
                    }
                }
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }finally {
            Toast.makeText(requireContext(), "تم فلترة البيانات بنجاح", Toast.LENGTH_LONG).show();
        }
        //hideProgress();
        return filteredPayments;
    }

    private class FetchPaymentsTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = NetworkBoundResource.provideOkHttpClient();  // Initialize OkHttpClient

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            String apiUrl = Constants.BASE_URL_SEP + "/Customer/my_payments";

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    return "Error: " + (response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideProgress();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                JSONArray paymentsArray = dataObject.getJSONArray("payments");

                paymentsList.clear();
                for (int i = 0; i < paymentsArray.length(); i++) {
                    paymentsList.add(paymentsArray.getJSONObject(i));
                }

                // Display all payments regardless of category filter
                displayPayments(paymentsList);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "الخادم خارج الخدمة", Toast.LENGTH_LONG).show();
            }
        }
    }


}
