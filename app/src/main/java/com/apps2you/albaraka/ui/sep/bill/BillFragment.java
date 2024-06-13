package com.apps2you.albaraka.ui.sep.bill;

import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentBillBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.common.adapters.AccountsRecyclerAdapter;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.viewmodels.transfer.BillViewModel;
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

import dagger.android.support.AndroidSupportInjection;

public class BillFragment extends BaseFragment<FragmentBillBinding, BillViewModel> {

    private ImageView iconImageView;
    private TextView categNameTextView;
    private Spinner spinnerBillers;
    private Spinner spinnerBillersServices;
    private CheckBox checkbox;
    private Button buttonSubmit;
    private static ProgressBar loader;
    private List<Biller> billersList;
    private String categoryName;
    private LinearLayout inputFieldsContainer;
    private CheckBox checkboxAllBills;
    private List<CheckBox> cardCheckboxes;
    private Button buttonSubmitPayBills;


    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = FragmentBillBinding.inflate(inflater, container, false);
        View rootView = mViewDataBinding.getRoot();

        // Retrieve the necessary views
        categoryName = getArguments().getString("categoryName");
        billersList = (List<Biller>)getArguments().getSerializable("billers");


        loader = rootView.findViewById(R.id.loader);
        spinnerBillers = rootView.findViewById(R.id.spinner_billers);
        spinnerBillersServices = rootView.findViewById(R.id.spinner_billers_services);
        checkbox = rootView.findViewById(R.id.checkbox);
        buttonSubmit = rootView.findViewById(R.id.button_submit);
        inputFieldsContainer = rootView.findViewById(R.id.input_fields_container);
        ImageButton backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(BillFragment.this).navigateUp());
        // Submit button click listener
        buttonSubmit.setOnClickListener(v -> handleSubmitButtonClick());

        // Initialize additional views
        iconImageView = rootView.findViewById(R.id.image_center_right);
        categNameTextView = rootView.findViewById(R.id.text_first_row);
        buttonSubmitPayBills = rootView.findViewById(R.id.button_submit_pay_bills);
        checkboxAllBills = rootView.findViewById(R.id.checkbox_all_bills);
        cardCheckboxes = new ArrayList<>();
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
        //new FetchBillersTask().execute();

        // Setup the RecyclerView after the view is properly inflated

        populateBillersSpinner(billersList);

        return rootView;
    }





    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bill;
    }

    @Override
    public Class<BillViewModel> setViewModel() {
        return BillViewModel.class;
    }

    @Override
    public void setUpView() {

    }

    @Override
    public void fetchData() {

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
                )
                );
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
        loader.setVisibility(View.GONE);

        // Convert StringBuilder to String
        String jsonString = responseData.toString();
        String bc= getSelectedBillerCode().toString();
        if (!jsonString.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Bundle bundle = new Bundle();
                bundle.putString("responseData", jsonObject.toString());

                bundle.putString("billerCode", bc.toString());

                NavHostFragment.findNavController(BillFragment.this)
                        .navigate(R.id.action_fragment_bill_to_fragment_bill_details, bundle);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "لا يوجد فواتير للدفع", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "حدث خطأ اثناء الاتصال في السيرفر حاول لاحقا", Toast.LENGTH_SHORT).show();
        }

    }
}


    //for pay
    class SendPostRequestTask2 extends AsyncTask<String, Void, StringBuilder> {
        private String billingNo;
        private String billNo;
        private String serviceType;
        private String billerCode;
        private String accountNumber;

        private String dueAmount;

        private String paidAmt;

        public SendPostRequestTask2(String billingNo, String billNo, String serviceType, String billerCode, String accountNumber,String dueAmount,String paidAmt) {
            this.billingNo = billingNo;
            this.billNo = billNo;
            this.serviceType = serviceType;
            this.billerCode = billerCode;
            this.accountNumber = accountNumber;
            this.dueAmount = dueAmount;
            this.paidAmt = paidAmt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show progress loader
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected StringBuilder doInBackground(String... params) {
            String apiUrl = "http://epaytest.albaraka.com.sy:4433/SEP/Services_Interface/bank_bill_Payment2";
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Construct JSON payload
                JSONObject postData = new JSONObject();
                try {
                    postData.put("BillerCode", billerCode);
                    postData.put("BillingNo", billingNo);
                    postData.put("BillNo", billNo);
                    postData.put("ServiceType", serviceType);
                    postData.put("accountNumber", accountNumber);
                    postData.put("BillAmount", dueAmount);
                    postData.put("paidAmt", paidAmt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseStrBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
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

            Toast.makeText(requireContext(), responseData.toString(), Toast.LENGTH_LONG).show();

            // Handle response data
            // Note: You can add further handling here if needed
        }
    }

    private void displayResponse(JSONObject jsonObject) {
        try {
            JSONArray data = jsonObject.getJSONArray("data");
            LinearLayout cardsContainer = requireView().findViewById(R.id.cards_container);
            ConstraintLayout billsCard = requireView().findViewById(R.id.bills_card);
            CheckBox checkboxAllBills = requireView().findViewById(R.id.checkbox_all_bills);
            Button buttonSubmitPayBills = requireView().findViewById(R.id.button_submit_pay_bills);

            cardsContainer.removeAllViews();

            for (int i = 0; i < data.length(); i++) {
                JSONObject bill = data.getJSONObject(i);

                View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.card_bill_item, cardsContainer, false);

                String billingNo = bill.getString("billingNo");
                String billNo = bill.getString("billNo");
                String serviceType = bill.getString("serviceType");
                String dueAmount = bill.getString("dueAmount");
                String feeAmount = bill.getString("feeAmount");
                String issueDate = bill.getString("issueDate");
                String dueDate = bill.getString("dueDate");
                String paidAmt = String.valueOf(Double.parseDouble(dueAmount) + Double.parseDouble(feeAmount));
                String formattedDueDate = formatDateString(dueDate);
                String formattedIssueDate = formatDateString(issueDate);

                CheckBox cardCheckbox = cardView.findViewById(R.id.cardCheckbox);
                TextView billingNoTextView = cardView.findViewById(R.id.billingNoTextView);
                TextView dueAmountTextView = cardView.findViewById(R.id.dueAmountTextView);
                TextView feeAmountTextView = cardView.findViewById(R.id.feeAmountTextView);
                TextView issueDateTextView = cardView.findViewById(R.id.issueDateTextView);
                TextView dueDateTextView = cardView.findViewById(R.id.dueDateTextView);

                billingNoTextView.setText(getString(R.string.billing_no, billingNo));
                dueAmountTextView.setText( getString(R.string.due_amount,dueAmount));
                feeAmountTextView.setText(getString(R.string.fee_amount, feeAmount));
                issueDateTextView.setText(getString(R.string.issue_date, formattedIssueDate));
                dueDateTextView.setText(getString(R.string.due_date, formattedDueDate));
                cardCheckboxes.add(cardCheckbox);
                cardsContainer.addView(cardView);
            }

            buttonSubmitPayBills.setOnClickListener(v -> {
                for (int i = 0; i < cardsContainer.getChildCount(); i++) {
                    View cardView = cardsContainer.getChildAt(i);

                    CheckBox cardCheckbox = cardView.findViewById(R.id.cardCheckbox);
                    if (cardCheckbox.isChecked()) {
                        String billingNo = ((TextView) cardView.findViewById(R.id.billingNoTextView)).getText().toString();
                        String billNo = null;
                        String dueAmount = ((TextView) cardView.findViewById(R.id.dueAmountTextView)).getText().toString();
                        String feeAmount = ((TextView) cardView.findViewById(R.id.feeAmountTextView)).getText().toString();
                        String paidAmt = String.valueOf(Double.parseDouble(dueAmount) + Double.parseDouble(feeAmount));


                        try {
                            billNo = data.getJSONObject(i).getString("billNo");
                            dueAmount = data.getJSONObject(i).getString("dueAmount");
                         //   paidAmt = data.getJSONObject(i).getString("paidAmt");

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String serviceType = null;
                        try {
                            serviceType = data.getJSONObject(i).getString("serviceType");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String billerCode = getSelectedBillerCode();
                        String accountNumber = getAccountNumber();

                        new SendPostRequestTask2(billingNo, billNo, serviceType, billerCode, accountNumber,dueAmount,paidAmt).execute();
                    }
                }
            }
            );

            if (cardsContainer.getVisibility() == View.VISIBLE) {
                billsCard.setVisibility(View.VISIBLE);
                checkboxAllBills.setVisibility(View.VISIBLE);
                buttonSubmitPayBills.setVisibility(View.VISIBLE);
            } else {
                billsCard.setVisibility(View.GONE);
                checkboxAllBills.setVisibility(View.GONE);
                buttonSubmitPayBills.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "لا يوجد فواتير للعرض", Toast.LENGTH_SHORT).show();
        }
    }


    private String getSelectedBillerCode() {
        Biller selectedBiller = billersList.get(spinnerBillers.getSelectedItemPosition());
        return selectedBiller.getBillerCode();
    }

    private String getAccountNumber() {
        Account selectedFromAccount = mViewModel.selectedAccount.getValue();
        return selectedFromAccount.getNumber();
    }




}
