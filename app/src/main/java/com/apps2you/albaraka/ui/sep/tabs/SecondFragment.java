package com.apps2you.albaraka.ui.sep.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
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
public class SecondFragment extends Fragment {
    private GridView gridView;
    private CardAdapterTwo adapter;
    private ProgressBar loader;
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

        loader.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    token = userData.getToken();
                    new FetchProfileBillsTask().execute();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
            }
        });
        return rootView;
    }

    private class FetchProfileBillsTask extends AsyncTask<Void, Void, List<CardItemProfile>> {

        @Override
        protected List<CardItemProfile> doInBackground(Void... voids) {
            List<CardItemProfile> cardItemList = new ArrayList<>();
            InputStream inputStream = null;

            try {
                URL url = new URL(Constants.BASE_URL_SEP+"/Customer/Bills");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("token", token); // Set the token in the header
                connection.connect();

                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String json = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(json);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray bills = data.getJSONArray("bills");

                for (int i = 0; i < bills.length(); i++) {
                    JSONObject bill = bills.getJSONObject(i);
                    String billerNameAr = bill.optString("billerName_ar", "N/A");
                    String billLabel = bill.optString("billLabel", "N/A");
                    String serviceNameAr = bill.optString("serviceName_ar", "N/A");
                    String billingNo = bill.optString("BillingNo", "N/A");
                    String iconUrl = bill.optString("logoName", "N/A");
                    String billerCode = bill.optString("billerCode", "N/A");
                    int isDeleted = bill.optInt("is_deleted", 1);
                    String id = bill.optString("id", "N/A");
                    cardItemList.add(new CardItemProfile(billerNameAr, billLabel,serviceNameAr, billingNo, iconUrl.replace("..", Constants.BASE_URL_SEP_ICON), isDeleted,id,billerCode));
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return cardItemList;
        }
        @Override
        protected void onPostExecute(List<CardItemProfile> cardItemList) {
            super.onPostExecute(cardItemList);
            loader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            adapter = new CardAdapterTwo(getActivity(), cardItemList, token,SecondFragment.this);
            gridView.setAdapter(adapter);
        }


    }
    }

