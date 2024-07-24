package com.apps2you.albaraka.ui.sep.bill;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
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
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.SharedViewModel;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.support.AndroidSupportInjection;
import com.apps2you.albaraka.ui.sep.SEPFragment;
public class BillDetailsFragment extends BaseTransferFragment<FragmentBillDetailsBinding, BillViewModel> {

    private LinearLayout cardsContainer;
    private String billerCode; // Add this field
    private CheckBox checkboxAllBills;
    private SharedViewModel sharedViewModel;
    private String token;
    private List<CheckBox> cardCheckboxes;
    private Button buttonSubmitPayBills;
    private JSONObject jsonObject;
    private List<Biller> billersList;
    private BillViewModel viewModel;
    private TextView textViewResponse;
    private SweetAlertDialog progressDialog;

    public BillDetailsFragment() {
       this.sharedViewModel = new SharedViewModel();
    }

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
//        viewModel = new ViewModelProvider(this).get(BillViewModel.class);
//        mViewDataBinding.setViewModel(viewModel);
//        mViewDataBinding.setLifecycleOwner(this);
        billersList = (List<Biller>)getArguments().getSerializable("billers");
        cardsContainer = rootView.findViewById(R.id.cards_container);
        checkboxAllBills = rootView.findViewById(R.id.checkbox_all_bills);
        buttonSubmitPayBills = rootView.findViewById(R.id.button_submit_pay_bills);
        cardCheckboxes = new ArrayList<>();

        ImageButton backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(BillDetailsFragment.this).navigateUp());

        SEPFragment.sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                token = userData.getToken();
            }
        });
        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        progressDialog.setContentText(getString(R.string.loading));
        progressDialog.setCancelable(false);



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
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("خطأ في الخادم,يرجى المحاولة لاحقا")
                    .show();

    }


        setBackButtonAction(mViewDataBinding.getRoot());

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
    protected void showProgress() {
        progressDialog.show();
    }
    protected void hideProgress() {
        progressDialog.dismiss();
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
                TextView statusTextView = cardView.findViewById(R.id.statusTextView);

                billingNoTextView.setText(billingNo);
                dueAmountTextView.setText(dueAmount);
                feeAmountTextView.setText(feeAmount);
                issueDateTextView.setText(getString(R.string.issue_date, formattedIssueDate));
                dueDateTextView.setText(getString(R.string.due_date, formattedDueDate));
                statusTextView.setText("فاتورة جديدة");
                statusTextView.setTextColor(getContext().getResources().getColor(android.R.color.white));
                statusTextView.setBackgroundResource(R.drawable.rounded_background_orange);
                statusTextView.setVisibility(View.VISIBLE);
                cardCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        checkboxAllBills.setOnCheckedChangeListener(null);
                        checkboxAllBills.setChecked(false);
                        checkboxAllBills.setOnCheckedChangeListener((buttonView1, isChecked1) -> {
                            for (CheckBox checkBox : cardCheckboxes) {
                                checkBox.setChecked(isChecked1);
                            }
                        });
                    } else {
                        boolean allChecked = true;
                        for (CheckBox checkBox : cardCheckboxes) {
                            if (!checkBox.isChecked()) {
                                allChecked = false;
                                break;
                            }
                        }
                        if (allChecked) {
                            checkboxAllBills.setOnCheckedChangeListener(null);
                            checkboxAllBills.setChecked(true);
                            checkboxAllBills.setOnCheckedChangeListener((buttonView1, isChecked1) -> {
                                for (CheckBox checkBox : cardCheckboxes) {
                                    checkBox.setChecked(isChecked1);
                                }
                            });
                        }
                    }
                });
                cardCheckboxes.add(cardCheckbox);
                cardsContainer.addView(cardView);
            }
            buttonSubmitPayBills.setOnClickListener(v -> {
                List<JSONObject> selectedBills = new ArrayList<>();
                double totalCost = 0.0;

                for (int i = 0; i < cardsContainer.getChildCount(); i++) {
                    View cardView = cardsContainer.getChildAt(i);
                    CheckBox cardCheckbox = cardView.findViewById(R.id.cardCheckbox);

                    if (cardCheckbox.isChecked()) {
                        try {
                            JSONObject bill = data.getJSONObject(i);
                            selectedBills.add(bill);

                            double dueAmount = bill.getDouble("dueAmount");
                            double feeAmount = bill.getDouble("feeAmount");
                            totalCost += (dueAmount + feeAmount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!selectedBills.isEmpty()) {
                    showConfirmationDialog(selectedBills, totalCost);
                } else {
                    Toast.makeText(getContext(), "Please select at least one bill.", Toast.LENGTH_SHORT).show();
                }
            });

//            buttonSubmitPayBills.setOnClickListener(v -> {
//                for (int i = 0; i < cardsContainer.getChildCount(); i++) {
//                    View cardView = cardsContainer.getChildAt(i);
//                    CheckBox cardCheckbox = cardView.findViewById(R.id.cardCheckbox);
//                    TextView statusTextView = cardView.findViewById(R.id.statusTextView);
//
//                    if (cardCheckbox.isChecked()) {
//                        String billingNo = ((TextView) cardView.findViewById(R.id.billingNoTextView)).getText().toString();
//                        String billNo = null;
//                        String dueAmount = ((TextView) cardView.findViewById(R.id.dueAmountTextView)).getText().toString();
//                        String feeAmount = ((TextView) cardView.findViewById(R.id.feeAmountTextView)).getText().toString();
//                        String paidAmt = String.valueOf(Double.parseDouble(dueAmount) + Double.parseDouble(feeAmount));
//
//                        try {
//                            billNo = data.getJSONObject(i).getString("billNo");
//                            dueAmount = data.getJSONObject(i).getString("dueAmount");
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                        String serviceType = null;
//                        try {
//                            serviceType = data.getJSONObject(i).getString("serviceType");
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                        String accountNumber = getAccountNumber();
//
//                        new SendPostRequestTask2(billingNo, billNo, serviceType, billerCode, accountNumber, dueAmount, paidAmt, statusTextView).execute();
//                    }
//                }
//            });

        } catch (JSONException e) {
            e.printStackTrace();
            new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("استعلام")
                    .setContentText("لا يوجد فواتير لعرضها")
                    .show();

        }
    }
    private void showConfirmationDialog(List<JSONObject> selectedBills, double totalCost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_sep_payment, null);
        builder.setView(dialogView);

        LinearLayout billDetailsContainer = dialogView.findViewById(R.id.bill_details_container);
        TextView totalCostTextView = dialogView.findViewById(R.id.total_cost_text_view);
        totalCostTextView.setText(String.format(Locale.getDefault(), "%.2f", totalCost));

        for (JSONObject bill : selectedBills) {
            View billDetailView = inflater.inflate(R.layout.layout_key_value_item_sep, billDetailsContainer, false);
            TextView keyTextView = billDetailView.findViewById(R.id.key);
            TextView valueTextView = billDetailView.findViewById(R.id.value);

            try {
                double dueAmount = bill.getDouble("dueAmount");
                double feeAmount = bill.getDouble("feeAmount");
                double totalAmount = dueAmount + feeAmount;

                keyTextView.setText(getString(R.string.due_amount));
                valueTextView.setText(String.format(Locale.getDefault(), "%.2f", totalAmount));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            billDetailsContainer.addView(billDetailView);
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.button_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            handlePayBills(selectedBills);
        });

        dialog.show();
    }
    private void handlePayBills(List<JSONObject> selectedBills) {
        for (JSONObject bill : selectedBills) {
            try {
                String billingNo = bill.getString("billingNo");
                String billNo = bill.getString("billNo");
                String serviceType = bill.getString("serviceType");
                String dueAmount = bill.getString("dueAmount");
                String feeAmount = bill.getString("feeAmount");
                String paidAmt = String.valueOf(Double.parseDouble(dueAmount) + Double.parseDouble(feeAmount));
                String accountNumber = getAccountNumber();

                TextView statusTextView = new TextView(getContext()); // Placeholder, find the actual view in your layout

                new SendPostRequestTask2(billingNo, billNo, serviceType, billerCode, accountNumber, dueAmount, paidAmt, statusTextView).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    protected void onBackPressed() {
        requireActivity().onBackPressed();
    }
    private void setBackButtonAction(View view) {
        try {
            view.findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());
        } catch (Exception ignored) {
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
        private TextView statusTextView;

        public SendPostRequestTask2(String billingNo, String billNo, String serviceType, String billerCode, String accountNumber, String dueAmount, String paidAmt, TextView statusTextView) {
            this.billingNo = billingNo;
            this.billNo = billNo;
            this.serviceType = serviceType;
            this.billerCode = billerCode;
            this.accountNumber = accountNumber;
            this.dueAmount = dueAmount;
            this.paidAmt = paidAmt;
            this.statusTextView = statusTextView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected StringBuilder doInBackground(String... params) {
            String apiUrl = Constants.BASE_URL_SEP+"/Services_Interface/bank_bill_Payment2";
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setDoOutput(true);

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
            hideProgress();

            try {
                JSONObject responseJson = new JSONObject(responseData.toString());
                String errorCode = responseJson.getString("ErrorCode");
                String errorDescription = responseJson.getString("ErrorDescription");

                if ("000".equals(errorCode)) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("تم الدفع بنجاح")
                            .show();

                    statusTextView.setText("تم دفع الفاتورة بنجاح");
                    statusTextView.setTextColor(getContext().getResources().getColor(android.R.color.white));
                    statusTextView.setBackgroundResource(R.drawable.rounded_background_g);
                    statusTextView.setVisibility(View.VISIBLE);
                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("هناك خطأ: " + errorDescription)
                            .show();
                    statusTextView.setText("فشلت العملية");
                    statusTextView.setTextColor(getContext().getResources().getColor(android.R.color.white));
                    statusTextView.setBackgroundResource(R.drawable.rounded_background_red);
                    statusTextView.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("خطأ")
                        .setContentText("يرجى المحاولة مرة أخرى")
                        .show();


            }
        }
    }


    private String getAccountNumber() {
        Account selectedFromAccount = mViewModel.selectedAccount.getValue();
        return selectedFromAccount.getNumber();
    }
    private String formatDateString(String dateString) {
        // Example input format: yyyyMMddHHmm
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        // Desired output format: yyyy-MM-dd
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

}

