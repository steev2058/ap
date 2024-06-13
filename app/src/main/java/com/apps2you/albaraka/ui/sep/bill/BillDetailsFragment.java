package com.apps2you.albaraka.ui.sep.bill;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.apps2you.albaraka.data.model.City;
import com.apps2you.albaraka.databinding.FragmentBillDetailsBinding;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLForm;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.viewmodels.transfer.BillViewModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.android.support.AndroidSupportInjection;

public class BillDetailsFragment extends BaseTransferFragment<FragmentBillDetailsBinding, BillViewModel> {

    private LinearLayout cardsContainer;
    private String billerCode; // Add this field
    private CheckBox checkboxAllBills;
    private static ProgressBar loader;
    private List<CheckBox> cardCheckboxes;
    private Button buttonSubmitPayBills;
    private JSONObject jsonObject;
    private List<Biller> billersList;
    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = FragmentBillDetailsBinding.inflate(inflater, container, false);
        View rootView = mViewDataBinding.getRoot();
        billersList = (List<Biller>)getArguments().getSerializable("billers");
        cardsContainer = rootView.findViewById(R.id.cards_container);
        checkboxAllBills = rootView.findViewById(R.id.checkbox_all_bills);
        buttonSubmitPayBills = rootView.findViewById(R.id.button_submit_pay_bills);
        cardCheckboxes = new ArrayList<>();
        loader = rootView.findViewById(R.id.progressBar);
        ImageButton backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(BillDetailsFragment.this).navigateUp());

        Bundle bundle = getArguments();
        if (bundle != null) {
            String responseData = bundle.getString("responseData");
            billerCode = bundle.getString("billerCode");
            try {
                jsonObject = new JSONObject(responseData);
                displayResponse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        // Handle the case when responseData is null
        Toast.makeText(requireContext(), "Response data is null", Toast.LENGTH_SHORT).show();
    }

        checkboxAllBills.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CheckBox checkBox : cardCheckboxes) {
                checkBox.setChecked(isChecked);
            }
        });

       // buttonSubmitPayBills.setOnClickListener(v -> handlePayBills());

        return rootView;
    }

    @Override
    public void refresh() {
        mViewModel.setSelectedAccount(null);
        mViewModel.fetchAccounts();
        super.refresh();
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
        return R.layout.fragment_bill_details;
    }

    @Override
    public Class<BillViewModel> setViewModel() {
        return BillViewModel.class;
    }

    private void displayResponse(JSONObject jsonObject) {
        try {
            JSONArray data = jsonObject.getJSONArray("data");
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

                billingNoTextView.setText( billingNo);
                dueAmountTextView.setText( dueAmount);
                feeAmountTextView.setText( feeAmount);
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
                       // String billerCode = getSelectedBillerCode();
                        String accountNumber = getAccountNumber();

                        new SendPostRequestTask2(billingNo, billNo, serviceType, billerCode, accountNumber,dueAmount,paidAmt).execute();
                    }
                }
            } );

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error displaying bills", Toast.LENGTH_SHORT).show();
        }
    }

    public void transfer(){


        Account selectedAccount = mViewModel.selectedAccount.getValue();

        if (selectedAccount == null) {
            showToast(R.string.you_must_select_account);
        }  else {


            nextStep(getString(R.string.ADSL_payment));
        }
    }



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

            try {
                // Parse responseData to JSONObject
                JSONObject responseJson = new JSONObject(responseData.toString());
                String errorCode = responseJson.getString("error_code");
               // String errorCode = responseJson.getString("ErrorDescription");
                // Check the error_code and display corresponding message
                if ("000".equals(errorCode)) {
                    Toast.makeText(requireContext(), "تم الدفع بنجاح", Toast.LENGTH_LONG).show();
                } else if ("25".equals(errorCode)) {
                    Toast.makeText(requireContext(), "هناك خطأ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Unknown error: " + errorCode, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
            }


        }

    }

    private String getAccountNumber() {
        Account selectedFromAccount = mViewModel.selectedAccount.getValue();
        return selectedFromAccount.getNumber();
    }
    private String formatDateString(String dateString) {
        // Example format: yyyy-MM-dd
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}

