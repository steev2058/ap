package com.apps2you.albaraka.ui.sep.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentSepUserBinding;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSepProfileFragment extends Fragment {

    private String fromDate, toDate;

    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private SEPViewModel sepViewModel;
    private FragmentSepUserBinding binding;
    private RecyclerView recyclerViewPayments;
    private PaymentsAdapter paymentsAdapter;

    private List<JSONObject> paymentsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sep_user, container, false);
        sepViewModel = new ViewModelProvider(requireActivity()).get(SEPViewModel.class);
        selectToday();
        View rootView = binding.getRoot();
        recyclerViewPayments = rootView.findViewById(R.id.recycler_view_payments);
        recyclerViewPayments.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Assuming paymentsList is already defined and populated
        paymentsAdapter = new PaymentsAdapter(new ArrayList<>());
        recyclerViewPayments.setAdapter(paymentsAdapter);
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
        new FetchCategoriesTask().execute();
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenToVariables();

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
            try {
                Date from = simpleDateFormat.parse(fromDate);
                Date to = simpleDateFormat.parse(toDate);

                if (to != null && from != null) {
                    if (to.before(from)) {
                        showToast(R.string.invalid_date);
                    } else {
                        new FetchPaymentsTask().execute(sepViewModel.token.getValue());
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
    private void displayPayments(List<JSONObject> payments) {
        paymentsAdapter = new PaymentsAdapter(payments);
        recyclerViewPayments.setAdapter(paymentsAdapter);
        paymentsAdapter.notifyDataSetChanged();
    }
    private class FetchCategoriesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            String apiUrl = Constants.BASE_URL_SEP + "/Customer/all/";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            binding.progressBar.setVisibility(View.GONE);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                JSONArray categoriesArray = dataObject.getJSONArray("categories");

                List<String> billerNamesArList = new ArrayList<>();
                billerNamesArList.add("اختر مفوتر");
                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryObject = categoriesArray.getJSONObject(i);
                    JSONArray billersArray = categoryObject.getJSONArray("billers");

                    for (int j = 0; j < billersArray.length(); j++) {
                        JSONObject billerObject = billersArray.getJSONObject(j);
                        String billerNameAr = billerObject.getString("billerName_ar");
                        billerNamesArList.add(billerNameAr);
                    }
                }

                // Populate spinner_categories with biller names in Arabic
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, billerNamesArList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategories.setAdapter(adapter);

                // Handle spinner item selection if needed
                binding.spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedBillerName = (String) parent.getItemAtPosition(position);
                        if (selectedBillerName.equals("اختر مفوتر")) {
                            displayPayments(paymentsList); // Display all payments
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
                Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
            }
        }
    }


    private List<JSONObject> filterPayments(List<JSONObject> payments, String fromDate, String toDate, String selectedBillerName) {
        List<JSONObject> filteredPayments = new ArrayList<>();
        try {
            Date from = simpleDateFormat.parse(fromDate);
            Date to = simpleDateFormat.parse(toDate);

            for (JSONObject payment : payments) {
                String paymentDateStr = payment.getString("payment_date");
                Date paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(paymentDateStr);

                String billerNameAr = payment.getString("billerName_ar");

                if ((paymentDate.equals(from) || paymentDate.after(from)) && (paymentDate.equals(to) || paymentDate.before(to))) {
                    if (selectedBillerName.equals("اختر مفوتر") || billerNameAr.equals(selectedBillerName)) {
                        filteredPayments.add(payment);
                    }
                }
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }
        return filteredPayments;
    }

    private class FetchPaymentsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            String apiUrl = "http://epaytest.albaraka.com.sy:4433/SEP/Customer/my_payments";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            binding.progressBar.setVisibility(View.GONE);
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
                Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
            }
        }
    }

}
