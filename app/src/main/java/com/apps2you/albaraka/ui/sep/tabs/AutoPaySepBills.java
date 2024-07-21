    package com.apps2you.albaraka.ui.sep.tabs;

    import android.app.Dialog;
    import android.app.TimePickerDialog;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.WindowManager;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.TimePicker;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.widget.SwitchCompat;
    import androidx.fragment.app.DialogFragment;

    import com.apps2you.albaraka.R;
    import com.apps2you.albaraka.ui.sep.bill.Biller;
    import com.apps2you.albaraka.ui.sep.bill.BillingNumber;
    import com.apps2you.albaraka.ui.sep.bill.Service;
    import com.apps2you.albaraka.utils.Constants;
    import com.apps2you.albaraka.viewmodels.SharedViewModel;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;

    import cn.pedant.SweetAlert.SweetAlertDialog;

    public class AutoPaySepBills extends DialogFragment{
        private Spinner spinnerAccounts;
        private EditText pickTimeEditText;

        private List<String> accountList = new ArrayList<>();
        private List<Category> categoriesList = new ArrayList<>();
        private Spinner spinnerBillersServices;
        private List<Biller> billersList = new ArrayList<>();
        private SharedViewModel sharedViewModel;
        private String token;
        private Service selectedService;
        private Biller selectedBiller;
        private String billLabel;
        private String id;
        private List<BillingNumber> selectedBillingNumbers = new ArrayList<>();

        private SweetAlertDialog progressDialog;

        private EditText billLabelEditText;

        private EditText idEditText;
        // Define the views to hide/show
        private View dividerNotification;
        private TextView textView2;

        private ImageView imageViewCatigories;
        private TextView textView3;
        private EditText maxAmount;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_auto_pay_bill, container, false);

            spinnerAccounts = view.findViewById(R.id.spinner_accounts);
            Button buttonAddToFile = view.findViewById(R.id.button_submit);
            billLabelEditText = view.findViewById(R.id.billLabel);
            pickTimeEditText = view.findViewById(R.id.pick_time);



            // Initialize the views
            dividerNotification = view.findViewById(R.id.divider_notification);
            textView2 = view.findViewById(R.id.textView2);

            imageViewCatigories = view.findViewById(R.id.imageView_catigories);
            textView3 = view.findViewById(R.id.textView3);
            maxAmount = view.findViewById(R.id.max_amount);


            billLabelEditText.setText(billLabel);
            idEditText.setText(id);
            progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            progressDialog.setContentText(getString(R.string.loading));
            progressDialog.setCancelable(false);


            pickTimeEditText.setOnClickListener(v -> showTimePickerDialog());

            sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
                if (userData != null) {
                    token = userData.getToken();
                    new FetchAccountsTask().execute();
                }
            });
            // Set OnCheckedChangeListener for the switch
            SwitchCompat switchAutoPay = view.findViewById(R.id.switch_auto_pay);
            switchAutoPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    dividerNotification.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    spinnerAccounts.setVisibility(View.VISIBLE);
                    imageViewCatigories.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                    maxAmount.setVisibility(View.VISIBLE);
                    pickTimeEditText.setVisibility(View.VISIBLE);
                } else {
                    dividerNotification.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    spinnerAccounts.setVisibility(View.GONE);
                    imageViewCatigories.setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);
                    maxAmount.setVisibility(View.GONE);
                    pickTimeEditText.setVisibility(View.GONE);
                }
            });
            buttonAddToFile.setOnClickListener(v -> {
                new SendAutoPayDataTask().execute();
            });

            return view;
        }

        private void showTimePickerDialog() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int selectedMinute) -> {
                pickTimeEditText.setText(String.format("%02d:%02d", hourOfDay, selectedMinute));
            }, hour, minute, true);

            timePickerDialog.show();
        }
        private class FetchAccountsTask extends AsyncTask<Void, Void, List<String>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress();
            }

            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> accounts = new ArrayList<>();
                try {
                    URL url = new URL(Constants.BASE_URL_SEP + "/Customer/accounts");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONArray accountsArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < accountsArray.length(); i++) {
                            JSONObject accountObject = accountsArray.getJSONObject(i);
                            String accountName = accountObject.getString("BRIEFos_gl_name_arab");
                            String accountReference = accountObject.getString("os_add_reference");
                            String accountDisplay = "" + accountReference + " "+accountName ;

                            accounts.add(accountDisplay);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return accounts;
            }

            @Override
            protected void onPostExecute(List<String> accounts) {
                super.onPostExecute(accounts);
                hideProgress();
                accountList = accounts;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAccounts.setAdapter(adapter);
            }
        }

        public AutoPaySepBills(SharedViewModel sharedViewModel, String billLabel,String id) {
            this.sharedViewModel = sharedViewModel;
            this.billLabel = billLabel;
            this.id = id;

        }

        private class SendAutoPayDataTask extends AsyncTask<Void, Void, Boolean> {
            private String maxAmount;
            private String pickTime;
            private boolean autoPay;
            private String defaultAccount;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress();

                // Collect data from UI
                maxAmount = ((EditText) getView().findViewById(R.id.max_amount)).getText().toString();
                pickTime = ((EditText) getView().findViewById(R.id.pick_time)).getText().toString() + ":00";
                autoPay = ((SwitchCompat) getView().findViewById(R.id.switch_auto_pay)).isChecked();
                defaultAccount = ((Spinner) getView().findViewById(R.id.spinner_accounts)).getSelectedItem().toString().split(" ")[0];
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(Constants.BASE_URL_SEP + "/Customer/bill");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setDoOutput(true);

                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("id", idEditText.getText().toString());
                    jsonRequest.put("billLabel", billLabelEditText.getText().toString());
                    jsonRequest.put("max_amount", maxAmount);
                    jsonRequest.put("time_to_check", pickTime);
                    jsonRequest.put("auto_pay", autoPay ? "1" : "0");
                    jsonRequest.put("default_account", defaultAccount);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonRequest.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    return responseCode == HttpURLConnection.HTTP_OK;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                hideProgress();

                if (success) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("AutoPay data sent successfully")
                            .show();
                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Failed to send AutoPay data")
                            .show();
                }
            }
        }

        protected void showProgress() {
            progressDialog.show();
        }
        protected void hideProgress() {
            progressDialog.dismiss();
        }


        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }
