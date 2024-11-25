package com.apps2you.albaraka.ui.sep.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.ui.sep.SEPFragment;
import com.apps2you.albaraka.ui.sep.bill.Biller;
import com.apps2you.albaraka.ui.sep.bill.BillingNumber;
import com.apps2you.albaraka.ui.sep.bill.Service;
import com.apps2you.albaraka.ui.sep.profile.UserData;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.SharedViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondFragment extends Fragment {
    private GridView gridView;
    private CardAdapterTwo adapter;
    private ProgressBar loader;
    private EditText searchBar;
    private SharedViewModel sharedViewModel;
    private String token;

    public SecondFragment(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.second_tab, container, false);

        gridView = rootView.findViewById(R.id.grid_view);
        loader = rootView.findViewById(R.id.loader);
        searchBar = rootView.findViewById(R.id.search_bar);
        loader.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        Button addButton = rootView.findViewById(R.id.add_button);

        sharedViewModel.getRefreshData().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh) {
                loadData();
                sharedViewModel.setRefreshData(false); // Reset the flag
            }
        });


        addButton.setOnClickListener(v -> {
            AddPaymentDialogFragment dialogFragment = new AddPaymentDialogFragment(sharedViewModel);
            dialogFragment.setOnDialogDismissListener(() -> {
                // Reload the data
                loadData();
            });
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            dialogFragment.show(ft, "add_payment_dialog");
        });
        loadData();
//        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<UserData>() {
//            @Override
//            public void onChanged(UserData userData) {
//                if (userData != null) {
//                    token = userData.getToken();
//                    new FetchProfileBillsTask().execute();
//                }
//            }
//        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
            }
        });


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return rootView;
    }
    private void loadData() {
        loader.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                token = userData.getToken();
                new FetchProfileBillsTask().execute();
            }
        });
    }
    private class FetchProfileBillsTask extends AsyncTask<Void, Void, List<CardItemProfile>> {
        String language = UserUtils.getInstance(requireContext()).getLanguage();
        @Override
        protected List<CardItemProfile> doInBackground(Void... voids) {
            List<CardItemProfile> cardItemList = new ArrayList<>();
            InputStream inputStream = null;
            OkHttpClient client = NetworkBoundResource.provideOkHttpClient();
            String url = Constants.BASE_URL_SEP + "/Customer/Bills";

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json");

            if (token != null && !token.isEmpty()) {
                requestBuilder.addHeader("token", token);
            } else {
                Log.e("FetchCategoriesTask", "Token is null or empty");
                return cardItemList; // Return empty list if token is missing
            }

            Request request = requestBuilder.build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Get the JSON response as a string
                String json = response.body().string();

                // Parse the JSON response
                JSONObject jsonObject = new JSONObject(json);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray bills = data.getJSONArray("bills");

                for (int i = 0; i < bills.length(); i++) {
                    JSONObject bill = bills.getJSONObject(i);
                    String billerNameAr = language.equals("ar") ?
                            bill.optString("billerName_ar", "N/A") :
                            bill.optString("billerName", "N/A");
                    String billLabel = bill.optString("billLabel", "N/A");
                    String serviceNameAr = language.equals("ar") ?
                            bill.optString("serviceName_ar", "N/A") :
                            bill.optString("serviceName", "N/A");
                    String billingNo = bill.optString("BillingNo", "N/A");
                    String iconUrl = bill.optString("logoName", "N/A");
                    String billerCode = bill.optString("billerCode", "N/A");
                    int isDeleted = bill.optInt("is_deleted", 1);
                    String id = bill.optString("id", "N/A");
                    int isAutoPay = bill.optInt("auto_pay", 1);
                    int max_amount = Integer.parseInt(bill.optString("max_amount", "N/A"));
                    String default_account = bill.optString("default_account", "N/A");

                    // Add item to cardItemList
                    cardItemList.add(new CardItemProfile(billerNameAr, billLabel, serviceNameAr, billingNo,
                            iconUrl.replace("..", Constants.BASE_URL_SEP_ICON), isDeleted, id, billerCode, isAutoPay, default_account, max_amount));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return cardItemList;

        }
        @Override
        protected void onPostExecute(List<CardItemProfile> cardItemList) {
            super.onPostExecute(cardItemList);
            loader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            if (getActivity() != null) {
            adapter = new CardAdapterTwo(getActivity(), cardItemList, token,SecondFragment.this,sharedViewModel);
            gridView.setAdapter(adapter);
        }
        }


    }
}

