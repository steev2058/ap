package com.apps2you.albaraka.ui.sep.tabs;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.apps2you.albaraka.viewmodels.SharedViewModel;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    SharedViewModel sharedModel;
    public TabsAdapter(FragmentManager fm, int numOfTabs,SharedViewModel sharedModel){
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.sharedModel = sharedModel;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new FirstFragment();
            case 1:
                return new SecondFragment(sharedModel);
            default:
                return null;
        }
    }

    public void setSharedModel(SharedViewModel sharedModel) {
        this.sharedModel = sharedModel;
    }
}