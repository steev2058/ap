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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.apps2you.albaraka.ui.sep.SEPFragmentDirections;
import com.apps2you.albaraka.ui.sep.bill.Biller;
import com.apps2you.albaraka.ui.sep.bill.BillingNumber;
import com.apps2you.albaraka.ui.sep.bill.Service;
import com.apps2you.albaraka.ui.transfer.adsl.ADSLFragmentDirections;
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

public class FirstFragment extends Fragment {

    private GridView gridView;
    private CardAdapter adapter;
    private ProgressBar loader;
    private SharedViewModel sharedViewModel;
    private String token;
    private NavController navController;

    public FirstFragment(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.first_tab, container, false);
        gridView = rootView.findViewById(R.id.grid_view);
        loader = rootView.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);
       // navController = NavHostFragment.findNavController(this);
        // Execute AsyncTask to fetch data from the API
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                token = userData.getToken();
            }
        });
        new FetchCategoriesTask().execute();

//        if (navController != null) {  gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Get the clicked card item
//                CardItem clickedItem = (CardItem) parent.getItemAtPosition(position);
//
//                // Create a bundle to pass data to the fragment
//                Bundle bundle = new Bundle();
//                bundle.putString("iconUrl", clickedItem.getIconUrl());
//                bundle.putString("categName_ar", clickedItem.getText());
//                bundle.putString("categoryName", clickedItem.getText());
//                bundle.putSerializable("billers", (Serializable) clickedItem.getBillerList());
//
//                navController.navigate(SEPFragmentDirections.actionFirstFragmentToFragmentBill() );
//
//                // Navigate to the fragment_bill.xml fragment
////                NavHostFragment.findNavController(FirstFragment.this)
////                        .navigate(R.id.action_firstFragment_to_fragment_bill, bundle);
//
//               // NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_firstFragment_to_fragment_bill, bundle);
//
////                NavHostFragment.findNavController(FirstFragment.this)
////                   .navigate(R.id.action_firstFragment_to_fragment_bill, bundle);
//
////                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_bill);
////                navController.navigate(R.id.action_firstFragment_to_fragment_bill, bundle);
//            }
//        });}
//        else {
//            Log.e("FirstFragment", "NavController is null");
//        }
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup NavController
        navController = NavHostFragment.findNavController(this);

        // Ensure NavController is not null before using it
        if (navController != null) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the clicked card item
                    CardItem clickedItem = (CardItem) parent.getItemAtPosition(position);

                    // Create a bundle to pass data to the fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("iconUrl", clickedItem.getIconUrl());
                    bundle.putString("categName_ar", clickedItem.getText());
                    bundle.putString("categoryName", clickedItem.getText());
                    bundle.putSerializable("billers", (Serializable) clickedItem.getBillerList());

                 //   navController.navigate(SEPFragmentDirections.actionFirstFragmentToFragmentBill());

                 // Alternative way to navigate with bundle
                     NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_firstFragment_to_fragment_bill, bundle);
                }
            });
        } else {
            Log.e("FirstFragment", "NavController is null");
            refreshFragment();
        }
    }
    private void refreshFragment() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    private class FetchCategoriesTask extends AsyncTask<Void, Void, List<CardItem>> {

        @Override
        protected List<CardItem> doInBackground(Void... voids) {
            List<CardItem> cardItemList = new ArrayList<>();

            if (token == null || token.isEmpty()) {
                Log.e("FetchCategoriesTask", "Token is null or empty");
                return cardItemList; // Return empty list if token is invalid
            }

            try {
                URL url = new URL(Constants.BASE_URL_SEP + "/Customer/all");
                OkHttpClient client = NetworkBoundResource.provideOkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("token", token)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray categories = data.getJSONArray("categories");

                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject category = categories.getJSONObject(i);
                        String categName_ar = category.optString("categName_ar", "N/A");
                        String iconUrl = category.optString("icon", "N/A");
                        List<Biller> billers = parseBillersJson(category.getJSONArray("billers"));
                        cardItemList.add(new CardItem(categName_ar, iconUrl.replace("..", Constants.BASE_URL_SEP_ICON), billers));
                    }
                } else {
                    Log.e("FetchCategoriesTask", "Request failed. Response code: " + response.code());
                }
            } catch (IOException | JSONException e) {
                Log.e("FetchCategoriesTask", "Error fetching categories", e);
            }

            return cardItemList;
        }

        @Override
        protected void onPostExecute(List<CardItem> cardItemList) {
            super.onPostExecute(cardItemList);

            loader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

            adapter = new CardAdapter(getContext(), cardItemList);
            gridView.setAdapter(adapter);
        }
    }


    private List<Biller> parseBillersJson(JSONArray billersArray) throws JSONException {
        List<Biller> billers = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject(jsonData);
//       JSONArray billersArray = jsonObject.getJSONArray("billers");

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
        return billers;
    }

}
