package com.apps2you.albaraka.ui.sep.bill;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.apps2you.albaraka.R;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BillFragment extends Fragment {

    private ImageView iconImageView;
    private TextView categNameTextView;
    private Spinner spinnerBillers;
    private Spinner spinnerBillersServices;
    private TextInputLayout tiAmount;
    private EditText etAmount;
    private CheckBox checkbox;
    private Button buttonSubmit;
    private ProgressBar loader;

    // List to store billers and services
    private List<Biller> billersList;
    private String categoryName;
    private LinearLayout inputFieldsContainer;
    private CheckBox checkboxAllBills;
    private List<CheckBox> cardCheckboxes;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bill, container, false);
        categoryName = getArguments().getString("categoryName");
        loader = rootView.findViewById(R.id.loader);
        spinnerBillers = rootView.findViewById(R.id.spinner_billers);
        spinnerBillersServices = rootView.findViewById(R.id.spinner_billers_services);
//        tiAmount = rootView.findViewById(R.id.ti_amount);
//        etAmount = rootView.findViewById(R.id.et_amount);
        checkbox = rootView.findViewById(R.id.checkbox);
        buttonSubmit = rootView.findViewById(R.id.button_submit);
        inputFieldsContainer = rootView.findViewById(R.id.input_fields_container);

        // Initialize views
        iconImageView = rootView.findViewById(R.id.image_center_right);
        categNameTextView = rootView.findViewById(R.id.text_first_row);

        checkboxAllBills = rootView.findViewById(R.id.checkbox_all_bills);
        cardCheckboxes = new ArrayList<>();
        // Set the listener for the all bills checkbox
        checkboxAllBills.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox checkBox : cardCheckboxes) {
                checkBox.setChecked(isChecked);
            }
        });

        // Retrieve data passed from FirstFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String iconUrl = bundle.getString("iconUrl");
            String categNameAr = bundle.getString("categName_ar");

            Picasso.get().load(iconUrl).into(iconImageView);
            categNameTextView.setText(categNameAr);
        }

        // Fetch billers and populate spinner
        new FetchBillersTask().execute();

        return rootView;
    }

    private void setupListeners() {
        // Back button click listener
        ImageButton backButton = requireView().findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(BillFragment.this).navigateUp());

        // Submit button click listener
        buttonSubmit.setOnClickListener(v -> handleSubmitButtonClick());
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
                Biller selectedBiller = billersList.get(position);
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
                Service selectedService = services.get(position);
                createInputFields(selectedService.getBillingNumbers());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    private void createInputFields(List<BillingNumber> billingNumbers) {
        // Clear previous input fields
        inputFieldsContainer.removeAllViews();

        // Create input fields based on billing numbers type
        for (BillingNumber billingNumber : billingNumbers) {
            if (billingNumber.getType().equals("LIST")) {
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
            } else if (billingNumber.getType().equals("TEXT")) {
                // Create a text input (EditText)
                EditText editText = new EditText(requireContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen._30sdp) // Set specific height
                );
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                editText.setHint(billingNumber.getArabicLabel());
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
            }
        }
    }




    private class FetchBillersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show progress loader
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "http://epaytest.albaraka.com.sy:4433/SEP/Customer/all/?category=" + categoryName;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            // Hide progress loader
            loader.setVisibility(View.GONE);

            if (!jsonData.isEmpty()) {
                try {
                    // Parse JSON data and populate the billersList
                    billersList = parseBillersJson(jsonData);

                    // Populate spinner with billers names
                    populateBillersSpinner(billersList);

                    // Set up listeners after fetching data
                    setupListeners();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch data from the server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Biller> parseBillersJson(String jsonData) throws JSONException {
        List<Biller> billers = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray categories = jsonObject.getJSONObject("data").getJSONArray("categories");

        for (int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);
            if (category.getString("categName_ar").equals(categoryName) || category.getString("categName").equals(categoryName)) { // Check if the category matches the selected category
                JSONArray billersArray = category.getJSONArray("billers");

                for (int j = 0; j < billersArray.length(); j++) {
                    JSONObject billerObj = billersArray.getJSONObject(j);

                    // Parse biller details
                    String billerName = billerObj.optString("billerName_ar");
                    String billerCode = billerObj.optString("billerCode");

                    // Parse services
                    List<Service> services = new ArrayList<>();
                    JSONArray servicesArray = billerObj.getJSONArray("services");

                    for (int k = 0; k < servicesArray.length(); k++) {
                        JSONObject serviceObj = servicesArray.getJSONObject(k);

                        // Parse service details
                        String serviceName = serviceObj.optString("serviceName_ar");
                        String serviceId = serviceObj.optString("serviceId");

                        // Parse billing numbers
                        List<BillingNumber> billingNumbers = new ArrayList<>();
                        JSONArray billingNumbersArray = serviceObj.getJSONArray("billingnumbers");
                        for (int l = 0; l < billingNumbersArray.length(); l++) {
                            JSONObject billingNumberObj = billingNumbersArray.getJSONObject(l);
                            String arabicLabel = billingNumberObj.optString("ArabicLabel");
                            String type = billingNumberObj.optString("Type");
                            String texts = billingNumberObj.optString("Texts"); // Extract texts
                            billingNumbers.add(new BillingNumber(arabicLabel, type, texts));
                        }

                        services.add(new Service(serviceId, serviceName, billingNumbers));
                    }

                    // Create Biller object and add to the list
                    Biller biller = new Biller(billerCode, billerName, services);
                    billers.add(biller);
                }
            }
        }
        return billers;
    }

    private void handleSubmitButtonClick() {
        // Retrieve selected values from spinners and input fields
        Biller selectedBiller = billersList.get(spinnerBillers.getSelectedItemPosition());
        Service selectedService = selectedBiller.getServices().get(spinnerBillersServices.getSelectedItemPosition());

        // Gather required data for POST request
        String serviceId = selectedService.getServiceId();
        String billerCode = selectedBiller.getBillerCode();
        String billingNo = getBillingNoFromInputFields(selectedService.getBillingNumbers());

        // Get checkbox value
        String withPaid = checkbox.isChecked() ? "Y" : "N";

        // Construct JSON payload
        JSONObject postData = new JSONObject();
        try {
            postData.put("ServiceType", serviceId);
            postData.put("BillerCode", billerCode);
            postData.put("BillingNo", billingNo);
            postData.put("withPaid", withPaid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send POST request to API URL
        new SendPostRequestTask().execute(postData.toString());
    }

    private String formatDateString(String date) {
        if (date.length() >= 8) { // Ensure the date has at least 8 characters
            date = date.substring(0, 8); // Trim to the first 8 characters
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        } else {
            // Handle invalid date strings
            return date;
        }
    }


    private void displayResponse(JSONObject jsonObject) {
        try {
            JSONArray data = jsonObject.getJSONArray("data");
            LinearLayout cardsContainer = requireView().findViewById(R.id.cards_container);

            // Clear previous cards
            cardsContainer.removeAllViews();

            // Loop through each bill and create a card for each
            for (int i = 0; i < data.length(); i++) {
                JSONObject bill = data.getJSONObject(i);

                // Inflate the card layout
                View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.card_bill_item, cardsContainer, false);

                // Extract data for the current bill
                String billingNo = bill.getString("billingNo");
                String dueAmount = bill.getString("dueAmount");
                String feeAmount = bill.getString("feeAmount");
                String issueDate = bill.getString("issueDate");
                String dueDate = bill.getString("dueDate");


                String formattedDueDate = formatDateString(dueDate);
                String formattedIssueDate = formatDateString(issueDate);


                // Find the views within the inflated layout
                CheckBox cardCheckbox = cardView.findViewById(R.id.cardCheckbox);
                // Find TextViews within the card
                TextView billingNoTextView = cardView.findViewById(R.id.billingNoTextView);
                TextView dueAmountTextView = cardView.findViewById(R.id.dueAmountTextView);
                TextView feeAmountTextView = cardView.findViewById(R.id.feeAmountTextView);
                TextView issueDateTextView = cardView.findViewById(R.id.issueDateTextView);
                TextView dueDateTextView = cardView.findViewById(R.id.dueDateTextView);


                // Set data to TextViews
                billingNoTextView.setText(getString(R.string.billing_no, billingNo));
                dueAmountTextView.setText(getString(R.string.due_amount, dueAmount));
                feeAmountTextView.setText(getString(R.string.fee_amount, feeAmount));
                issueDateTextView.setText(getString(R.string.issue_date, formattedIssueDate));
                dueDateTextView.setText(getString(R.string.due_date, formattedDueDate));

//                issueDateTextView.setText(getString(R.string.issue_date, issueDate));
//                dueDateTextView.setText(getString(R.string.due_date, dueDate));



                // Add the card to the container layout

                cardCheckboxes.add(cardCheckbox);

                cardsContainer.addView(cardView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
        }
    }


    private String getBillingNoFromInputFields(List<BillingNumber> billingNumbers) {
        StringBuilder billingNoBuilder = new StringBuilder();
        for (int i = 0; i < inputFieldsContainer.getChildCount(); i++) {
            View childView = inputFieldsContainer.getChildAt(i);
            if (childView instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) childView;
                EditText editText = textInputLayout.getEditText();
                if (editText != null) {
                    String text = editText.getText().toString().trim();

                    billingNoBuilder.append(text);
                    if(i != inputFieldsContainer.getChildCount()-1)
                    {
                        billingNoBuilder.append("_");
                    }
                }
            }
        }

        return billingNoBuilder.toString();
    }

    private class SendPostRequestTask extends AsyncTask<String, Void, StringBuilder> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show progress loader
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(String... params) {
            String apiUrl = "http://epaytest.albaraka.com.sy:4433/SEP/Services_Interface/bank_bill_presentment2";
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                outputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseStrBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                   // response.append(line);
                    responseStrBuilder.append(line);
                }
                reader.close();
                response = new StringBuilder(responseStrBuilder.toString());
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(StringBuilder responseData) {
            super.onPostExecute(responseData);
            // Hide progress loader
            loader.setVisibility(View.GONE);

            // Convert StringBuilder to String
            String jsonString = responseData.toString();

            // Check if the JSON string is not empty
            if (!jsonString.isEmpty()) {
                try {
                    // Create JSONObject from the JSON string
                    JSONObject jsonObject = new JSONObject(jsonString);

                    // Call displayResponse with the JSONObject

                    requireView().findViewById(R.id.checkbox_all_bills).setVisibility(View.VISIBLE);
                    requireView().findViewById(R.id.button_submit_pay_bills).setVisibility(View.VISIBLE);
                    requireView().findViewById(R.id.bills_card).setVisibility(View.VISIBLE);
                    displayResponse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to get response from server", Toast.LENGTH_SHORT).show();
            }
        }

    }


    static class BillingNumber {
        private String arabicLabel;
        private String type;
        private String texts; // Added texts field

        public BillingNumber(String arabicLabel, String type, String texts) {
            this.arabicLabel = arabicLabel;
            this.type = type;
            this.texts = texts;
        }

        public String getArabicLabel() {
            return arabicLabel;
        }

        public String getType() {
            return type;
        }

        public String getTexts() {
            return texts;
        }
    }

}
