    package com.apps2you.albaraka.ui.sep.tabs;

    import android.app.Dialog;
    import android.app.TimePickerDialog;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.util.Log;
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
    import com.apps2you.albaraka.data.preference.UserUtils;
    import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
    import com.apps2you.albaraka.ui.sep.bill.Biller;
    import com.apps2you.albaraka.ui.sep.bill.BillingNumber;
    import com.apps2you.albaraka.ui.sep.bill.Service;
    import com.apps2you.albaraka.utils.Constants;
    import com.apps2you.albaraka.utils.NumberTextWatcher;
    import com.apps2you.albaraka.viewmodels.SharedViewModel;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;

    import cn.pedant.SweetAlert.SweetAlertDialog;
    import okhttp3.MediaType;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.RequestBody;
    import okhttp3.Response;

    public class AutoPaySepBills extends DialogFragment{
        private Spinner spinnerAccounts;
      //  private EditText pickTimeEditText;

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

        private String defaultAccount;
        private int autoPay;
        private int maxA;

//        private String id;
        private List<BillingNumber> selectedBillingNumbers = new ArrayList<>();

        private SweetAlertDialog progressDialog;

        private EditText billLabelEditText;

//        private static final String ARG_BILL_LABEL = "bill_label";
//        private static final String ARG_ID = "id";
//        private EditText idEditText;
        // Define the views to hide/show
        private View dividerNotification;
        private TextView textView2;

        private ImageView imageViewCatigories;
        private TextView textView3;
        private TextView textView4;
        private TextView textView56;
        private EditText maxAmount;

//        public static AutoPaySepBills newInstance(SharedViewModel sharedViewModel, String billLabel, String id) {
//            AutoPaySepBills fragment = new AutoPaySepBills(sharedViewModel,billLabel,id);
//            Bundle args = new Bundle();
//            args.putString(ARG_BILL_LABEL, billLabel);
//            args.putString(ARG_ID, id);
//            fragment.setArguments(args);
//            return fragment;
//        }
//        @Override
//        public void onCreate(@Nullable Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            if (getArguments() != null) {
//                billLabel = getArguments().getString(ARG_BILL_LABEL);
//                id = getArguments().getString(ARG_ID);
//            }
//        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_auto_pay_bill, container, false);

            spinnerAccounts = view.findViewById(R.id.spinner_accounts);
            Button buttonAddToFile = view.findViewById(R.id.button_submit);
            billLabelEditText = view.findViewById(R.id.billLabel);
       //     pickTimeEditText = view.findViewById(R.id.pick_time);
            maxAmount = view.findViewById(R.id.max_amount);



            // Initialize the views
            dividerNotification = view.findViewById(R.id.divider_notification);
            textView2 = view.findViewById(R.id.textView2);

            imageViewCatigories = view.findViewById(R.id.imageView_catigories);
            textView3 = view.findViewById(R.id.textView3);
            textView4 = view.findViewById(R.id.textView4);
            textView56 = view.findViewById(R.id.textView56);



            billLabelEditText.setText(billLabel);
            maxAmount.setText(String.valueOf(maxA));
            new NumberTextWatcher(maxAmount);
            progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
           // progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            progressDialog.setContentText(getString(R.string.loading));
            progressDialog.setCancelable(false);


         //   pickTimeEditText.setOnClickListener(v -> showTimePickerDialog());

            sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
                if (userData != null) {
                    token = userData.getToken();
                    new FetchAccountsTask().execute();
                }
            });
            // Set OnCheckedChangeListener for the switch
            SwitchCompat switchAutoPay = view.findViewById(R.id.switch_auto_pay);
            switchAutoPay.setChecked(autoPay == 1);
            if (autoPay == 1) {
                dividerNotification.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                spinnerAccounts.setVisibility(View.VISIBLE);
                imageViewCatigories.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);
                textView4.setVisibility(View.VISIBLE);
                textView56.setVisibility(View.VISIBLE);
                maxAmount.setVisibility(View.VISIBLE);
                // pickTimeEditText.setVisibility(View.VISIBLE);
            } else {
                dividerNotification.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                spinnerAccounts.setVisibility(View.GONE);
                imageViewCatigories.setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                textView4.setVisibility(View.GONE);
                textView56.setVisibility(View.GONE);
                maxAmount.setVisibility(View.GONE);
                // pickTimeEditText.setVisibility(View.GONE);
            }

            switchAutoPay.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    dividerNotification.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    spinnerAccounts.setVisibility(View.VISIBLE);
                    imageViewCatigories.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    textView56.setVisibility(View.VISIBLE);
                    maxAmount.setVisibility(View.VISIBLE);
                   // pickTimeEditText.setVisibility(View.VISIBLE);
                } else {
                    dividerNotification.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    spinnerAccounts.setVisibility(View.GONE);
                    imageViewCatigories.setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);
                    textView4.setVisibility(View.GONE);
                    textView56.setVisibility(View.GONE);
                    maxAmount.setVisibility(View.GONE);
                   // pickTimeEditText.setVisibility(View.GONE);
                }
            });
            maxAmount.addTextChangedListener(new NumberTextWatcher(maxAmount));
            buttonAddToFile.setOnClickListener(v -> {
                new SendAutoPayDataTask().execute();

            });

            return view;
        }

//        private void showTimePickerDialog() {
//            Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//
//            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int selectedMinute) -> {
//                pickTimeEditText.setText(String.format("%02d:%02d", hourOfDay, selectedMinute));
//            }, hour, minute, true);
//
//            timePickerDialog.show();
//        }
private class FetchAccountsTask extends AsyncTask<Void, Void, List<String>> {
    private String language = UserUtils.getInstance(requireContext()).getLanguage();
    private String errorMessage;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgress();
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> accounts = new ArrayList<>();
        OkHttpClient client = NetworkBoundResource.provideOkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.BASE_URL_SEP + "/Customer/accounts")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                errorMessage = "Failed to fetch accounts.";
                return null;
            }

            String jsonData = response.body().string();
            JSONObject jsonResponse = new JSONObject(jsonData);
            int errorCode = jsonResponse.getInt("ErrorCode");

            if (errorCode == 200) {
                JSONArray accountsArray = jsonResponse.getJSONArray("data");
                for (int i = 0; i < accountsArray.length(); i++) {
                    JSONObject accountObject = accountsArray.getJSONObject(i);
                    String accountName = language.equals("ar") ?
                            accountObject.optString("BRIEFos_gl_name_arab", "N/A") :
                            accountObject.optString("BRIEF_gl_name_eng", "N/A");
                    String accountReference = accountObject.optString("os_add_reference", "");
                    String accountDisplay = accountReference.isEmpty() ? accountName : accountName;

                    accounts.add(accountDisplay);
                }
            } else {
                errorMessage = "Error fetching accounts: " + jsonResponse.optString("ErrorMessage", "Unknown error.");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            errorMessage = "Error parsing data.";
        }
        return accounts;
    }

    @Override
    protected void onPostExecute(List<String> accounts) {
        super.onPostExecute(accounts);
        hideProgress();

        if (accounts == null) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(errorMessage != null ? errorMessage : "Unknown error.")
                    .show();
        } else {
            accountList = accounts;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAccounts.setAdapter(adapter);

            // Set the default account
            if (defaultAccount != null && !defaultAccount.isEmpty()) {
                for (int i = 0; i < accountList.size(); i++) {
                    if (accountList.get(i).startsWith(defaultAccount)) {
                        spinnerAccounts.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
}


        public AutoPaySepBills(SharedViewModel sharedViewModel, String billLabel,String id, int autoPay, int maxAmount, String defaultAccount) {
            this.sharedViewModel = sharedViewModel;
            this.billLabel = billLabel;
            this.id = id;
            this.autoPay = autoPay;
            this.maxA = maxAmount;
            this.defaultAccount = defaultAccount;

        }

        private class SendAutoPayDataTask extends AsyncTask<Void, Void, Boolean> {
            private int maxAmount;
            private String billLabelR;
            private String pickTime;
            private boolean autoPay;
            private String defaultAccount;
            private String errorMessage;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress();

                // Collect data from UI
                maxAmount = Integer.parseInt(((EditText) getView().findViewById(R.id.max_amount)).getText().toString().replaceAll(",", ""));
                billLabelR = ((EditText) getView().findViewById(R.id.billLabel)).getText().toString();
                autoPay = ((SwitchCompat) getView().findViewById(R.id.switch_auto_pay)).isChecked();
                defaultAccount = ((Spinner) getView().findViewById(R.id.spinner_accounts)).getSelectedItem().toString().split(" ")[0];
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                OkHttpClient client = NetworkBoundResource.provideOkHttpClient();

                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("id", id);
                    jsonRequest.put("auto_pay", autoPay ? 1 : 0);
                    jsonRequest.put("default_account", defaultAccount);
                    jsonRequest.put("max_amount", maxAmount);
                    jsonRequest.put("billLabel", billLabelR);
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMessage = "Failed to create JSON data.";
                    return false;
                }

                RequestBody body = RequestBody.create(jsonRequest.toString(), MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(Constants.BASE_URL_SEP + "/Customer/bill")
                        .put(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        errorMessage = "Error: " + response.message();
                        return false;
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessage = "Network error.";
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                hideProgress();

                if (success) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("نجاح العملية")
                            .setContentText("تم تحديث البيانات بنجاح")
                            .show();
                    dismiss();
                } else {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("خطأ في العملية")
                            .setContentText(errorMessage != null ? errorMessage : "فشل تحديث البيانات")
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
