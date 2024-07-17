package com.apps2you.albaraka.ui.sep.tabs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.sep.bill.Biller;
import com.apps2you.albaraka.ui.sep.bill.BillingNumber;
import com.apps2you.albaraka.ui.sep.bill.Service;
import com.apps2you.albaraka.ui.sep.profile.UserData;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.SharedViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddPaymentDialogFragment extends DialogFragment {

    private LinearLayout inputFieldsContainer;
    private Spinner spinnerBillers;
    private Spinner spinnerCategories;
    private List<Category> categoriesList = new ArrayList<>();
    private Spinner spinnerBillersServices;
    private List<Biller> billersList = new ArrayList<>();
    private SharedViewModel sharedViewModel;
    private String token;
    private Service selectedService;
    private Biller selectedBiller;
    private String billLabel;
    private List<BillingNumber> selectedBillingNumbers = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_payment, container, false);
        inputFieldsContainer = view.findViewById(R.id.input_fields_container);
        spinnerCategories = view.findViewById(R.id.spinner_catigories_add);
        spinnerBillers = view.findViewById(R.id.spinner_billers_add);
        spinnerBillersServices = view.findViewById(R.id.spinner_billers_services_add);
        EditText billLabelEditText = view.findViewById(R.id.billLabel);
        Button buttonAddToFile = view.findViewById(R.id.button_submit);
        progressBar = view.findViewById(R.id.progressBar);
        buttonAddToFile.setOnClickListener(v -> {
            billLabel = billLabelEditText.getText().toString();
            new AddCustomerProfileTask().execute();
        });


        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                token = userData.getToken();
            }
        });

        // Call the API and populate the spinners
        new GetDataTask().execute(Constants.BASE_URL_SEP+"/Customer/all");

        return view;
    }

    public AddPaymentDialogFragment(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private class GetDataTask extends AsyncTask<String, Void, List<Category>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected List<Category> doInBackground(String... urls) {
            // List<Category> categories = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray categoriesArray = data.getJSONArray("categories");

                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryObject = categoriesArray.getJSONObject(i);
                    String categId = categoryObject.getString("categId");
                    String categName = categoryObject.getString("categName_ar");

                    JSONArray billersArray = categoryObject.getJSONArray("billers");
                    List<Biller> billers = new ArrayList<>();

                    for (int j = 0; j < billersArray.length(); j++) {
                        JSONObject billerObject = billersArray.getJSONObject(j);
                        String billerCode = billerObject.getString("billerCode");
                        String billerName = billerObject.getString("billerName_ar");

                        JSONArray servicesArray = billerObject.getJSONArray("services");
                        List<Service> services = new ArrayList<>();

                        for (int k = 0; k < servicesArray.length(); k++) {
                            JSONObject serviceObject = servicesArray.getJSONObject(k);
                            String serviceId = serviceObject.getString("serviceId");
                            String serviceName = serviceObject.getString("serviceName_ar");

                            JSONArray billingNumbersArray = serviceObject.getJSONArray("billingnumbers");
                            List<BillingNumber> billingNumbers = new ArrayList<>();

                            for (int l = 0; l < billingNumbersArray.length(); l++) {
                                JSONObject billingNumberObject = billingNumbersArray.getJSONObject(l);
                                String arabicLabel = billingNumberObject.getString("ArabicLabel");
                                String type = billingNumberObject.getString("Type");
                                String texts = billingNumberObject.optString("Texts", "");

                                billingNumbers.add(new BillingNumber(arabicLabel, type, texts));
                            }

                            services.add(new Service(serviceId, serviceName, billingNumbers));
                        }

                        billers.add(new Biller(billerCode, billerName, services));
                    }

                    categoriesList.add(new Category(categId, categName, billers));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return categoriesList;
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            progressBar.setVisibility(View.GONE);
            categoriesList = categories;
            populateCategoriesSpinner(categoriesList);
        }
    }

    private void populateCategoriesSpinner(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getCategName());
        }
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoriesAdapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Fetch billers for the selected category
                Category selectedCategory = categoriesList.get(position);
                billersList = selectedCategory.getBillers();
                populateBillersSpinner(billersList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void populateBillersSpinner(List<Biller> billers) {
        List<String> billerNames = new ArrayList<>();
        for (Biller biller : billers) {
            billerNames.add(biller.getBillerName());
        }
        ArrayAdapter<String> billersAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, billerNames);
        billersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillers.setAdapter(billersAdapter);

        spinnerBillers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Fetch services for the selected biller
                selectedBiller = billersList.get(position);
                populateServicesSpinner(selectedBiller.getServices());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void populateServicesSpinner(List<Service> services) {
        List<String> serviceNames = new ArrayList<>();
        for (Service service : services) {
            serviceNames.add(service.getServiceName());
        }
        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, serviceNames);
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillersServices.setAdapter(servicesAdapter);

        spinnerBillersServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Fetch billing numbers for the selected service
                selectedService = services.get(position);
                createInputFields(selectedService.getBillingNumbers());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private class AddCustomerProfileTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(Constants.BASE_URL_SEP + "/Services_Interface/add_customer_profile");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("ServiceType", selectedService.getServiceId());
                requestBody.put("BillerCode", selectedBiller.getBillerCode());
                requestBody.put("billLabel", billLabel);

                // Build the BillingNo string
                StringBuilder billingNumbersStringBuilder = new StringBuilder();
                for (BillingNumber billingNumber : selectedBillingNumbers) {
                    if (billingNumbersStringBuilder.length() > 0) {
                        billingNumbersStringBuilder.append("_");
                    }
                    billingNumbersStringBuilder.append(billingNumber.getArabicLabel());
                }

                requestBody.put("BillingNo", billingNumbersStringBuilder.toString());

                OutputStream os = connection.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                return responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            progressBar.setVisibility(View.GONE);
            if (success) {

                Toast.makeText(getContext(), "تم إضافة الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "فشل عملية إضافة الفاتورة", Toast.LENGTH_SHORT).show();
            }
        }

        }




    private void createInputFields(List<BillingNumber> billingNumbers) {
        // Clear previous input fields
        inputFieldsContainer.removeAllViews();

        // Create input fields based on billing numbers type
        for (BillingNumber billingNumber : billingNumbers) {
            if (billingNumber.getType().equalsIgnoreCase("LIST")) {
                // Create a dropdown (Spinner)
                Spinner spinner = new Spinner(requireContext());

                // Extract texts from the billing number
                String texts = billingNumber.getTexts();

                // Split the texts into an array
                String[] textArray = texts.split(",");

                // Set up Spinner adapter and add items
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, textArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setBackgroundResource(R.drawable.bg_edittext_selector); // Set border background

                // Create TextInputLayout for Spinner
                TextInputLayout spinnerTextInputLayout = new TextInputLayout(requireContext());
                LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                spinnerParams.setMargins(0, 20, 0, 20); // Add bottom margin
                spinnerTextInputLayout.setLayoutParams(spinnerParams);
                spinnerTextInputLayout.setBoxBackgroundColor(getResources().getColor(R.color.gray)); // Set background color
                spinnerTextInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE); // Set border mode
                spinner.setPadding(25, 25, 25, 25);

                // Add ArabicLabel as a hint above the Spinner
                TextView arabicLabelTextView = new TextView(requireContext());
                arabicLabelTextView.setText(billingNumber.getArabicLabel());
                arabicLabelTextView.setTextColor(getResources().getColor(R.color.gray)); // Set text color
                arabicLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium)); // Set text size
                arabicLabelTextView.setPadding(0, 5, 0, 10); // Set padding
                spinnerTextInputLayout.addView(arabicLabelTextView);

                // Add Spinner to the TextInputLayout
                spinnerTextInputLayout.addView(spinner);

                // Add TextInputLayout to the container layout
                inputFieldsContainer.addView(spinnerTextInputLayout);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        billingNumber.setArabicLabel(textArray[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });

            } else if (billingNumber.getType().equalsIgnoreCase("TEXT") || billingNumber.getType().equalsIgnoreCase("EDIT")) {
                // Add ArabicLabel TextView
                TextView arabicLabelTextView = new TextView(requireContext());
                arabicLabelTextView.setText(billingNumber.getArabicLabel());
                arabicLabelTextView.setTextColor(getResources().getColor(R.color.gray)); // Set text color
                arabicLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium)); // Set text size
                arabicLabelTextView.setPadding(0, 10, 0, 8); // Set padding

                // Add ArabicLabel TextView to the container layout
                inputFieldsContainer.addView(arabicLabelTextView);

                // Create a text input (EditText)
                EditText editText = new EditText(requireContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen._30sdp) // Set specific height
                );
                editText.setLayoutParams(layoutParams);
                editText.setPadding(25, 25, 25, 25); // Set padding
                editText.setTextColor(getResources().getColor(R.color.dark_gray)); // Set text color
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_small)); // Set text size
                editText.setBackgroundResource(R.drawable.bg_edittext_selector); // Set background
                editText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // Set text alignment
                layoutParams.setMargins(0, 20, 0, 20); // Add bottom margin

                // Create TextInputLayout for EditText
                TextInputLayout textInputLayout = new TextInputLayout(requireContext());
                textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                textInputLayout.setBoxBackgroundColor(getResources().getColor(R.color.gray)); // Set background color
                textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE); // Set border mode
                textInputLayout.addView(editText);

                // Add TextInputLayout to the container layout
                inputFieldsContainer.addView(textInputLayout);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Do nothing
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        billingNumber.setArabicLabel(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Do nothing
                    }
                });

            } else {
                // Handle unknown types if necessary
                continue;
            }
            selectedBillingNumbers.add(billingNumber);
        }
    }

}
