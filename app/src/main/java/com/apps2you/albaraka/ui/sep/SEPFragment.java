package com.apps2you.albaraka.ui.sep;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;
import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.FragmentSepBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.sep.tabs.TabsAdapter;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;
import com.google.android.material.tabs.TabLayout;

public class SEPFragment extends BaseFragment<FragmentSepBinding, SEPViewModel> {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void setUpView() {
        FragmentActivity activity = getActivity();

        // Initialize views
        viewPager = getViewDataBinding().getRoot().findViewById(R.id.view_pager);
        tabLayout = getViewDataBinding().getRoot().findViewById(R.id.tabLayout2);

        // Add tabs to the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("الدفع الفوري"));
        tabLayout.addTab(tabLayout.newTab().setText("الملف الشخصي"));

        // Set up ViewPager with TabsAdapter
        TabsAdapter tabsAdapter = new TabsAdapter(activity.getSupportFragmentManager(), tabLayout.getTabCount());
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
    public void onDetach() {
        super.onDetach();
        mViewModel.stopContentLoading();
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
