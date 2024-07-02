package com.apps2you.albaraka.ui.sep;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentSepBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.sep.profile.UserData;
import com.apps2you.albaraka.ui.sep.tabs.TabsAdapter;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.SharedViewModel;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SEPFragment extends BaseFragment<FragmentSepBinding, SEPViewModel> {
    private SEPViewModel sepViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private SharedViewModel sharedViewModel;
    private LinearLayout profileCard;

    private UserData userData;


    public SEPFragment() {
        this.sharedViewModel =new SharedViewModel();
    }


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Initialize SEPViewModel
//        sepViewModel = new ViewModelProvider(this).get(SEPViewModel.class);
//    }

    @Override
    public void setUpView() {
        FragmentActivity activity = getActivity();
        User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
        String token = user.getBillsPaymentToken();

        // Send the token to the API
        new AssignTokenTask().execute(token);
        View rootView = mViewDataBinding.getRoot();


        profileCard = (LinearLayout) rootView.findViewById (R.id.cardLayout);
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigateToUserProfile(userData);
            }
        });



        // Initialize views
        viewPager = getViewDataBinding().getRoot().findViewById(R.id.view_pager);
        tabLayout = getViewDataBinding().getRoot().findViewById(R.id.tabLayout2);

        // Add tabs to the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("الدفع الفوري"));
        tabLayout.addTab(tabLayout.newTab().setText("الفواتير الشخصية"));

        // Set up ViewPager with TabsAdapter
         tabsAdapter = new TabsAdapter(activity.getSupportFragmentManager(), tabLayout.getTabCount(),sharedViewModel);
        viewPager.setAdapter(tabsAdapter);

        // Connect TabLayout and ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Set tab text colors
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), getResources().getColor(R.color.orange));


    }

    @Override
    public void fetchData() {

        // You can implement fetching data if needed

    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the tab content by reloading the adapter's data
        if (tabsAdapter != null) {
            tabsAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mViewModel.stopContentLoading();
    }
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if (sepViewModel != null) {
//            sepViewModel.stopContentLoading();
//        } else {
//            Log.e("SEPFragment", "sepViewModel is null in onDetach");
//        }
//    }

    private void navigateToUserProfile(UserData userData) {
        Bundle bundle = new Bundle();
        bundle.putString("arName", userData.getArName());
        bundle.putString("cif", userData.getCif());
        bundle.putString("phone", userData.getPhone());
        bundle.putString("address", userData.getAddress());
        bundle.putString("token", userData.getToken());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_firstFragment_to_fragment_sep_user, bundle);
    }
    private class AssignTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            String apiUrl = Constants.BASE_URL_SEP+"/Users/assignToken?token=" + token;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                JSONObject responseObject = new JSONObject(result);
                JSONObject data = responseObject.getJSONObject("data");

                 userData = new UserData();
                userData.setArName(data.getString("ArName"));
                userData.setAddress(data.getString("Address"));
                userData.setPhone(data.getString("Phone"));
                userData.setCif(data.getString("cif"));
                userData.setToken(data.getString("token"));
                sharedViewModel.setUserData(userData);


                Toast.makeText(requireContext(), "Data fetched successfully", Toast.LENGTH_LONG).show();
            }  catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sep;
    }

    @Override
    public Class<SEPViewModel> setViewModel() {
        return SEPViewModel.class;
    }
}
