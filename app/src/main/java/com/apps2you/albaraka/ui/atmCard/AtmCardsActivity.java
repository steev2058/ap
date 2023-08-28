package com.apps2you.albaraka.ui.atmCard;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityAtmCardsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.AtmViewModel;


public class AtmCardsActivity extends BaseActivity<ActivityAtmCardsBinding, AtmViewModel> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_atm_cards;
    }

    @Override
    public Class<AtmViewModel> setViewModel() {
        return AtmViewModel.class;
    }

    @Override
    public void setUpView() {

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.atmCardsFragment) {
                setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.my_cards));
            } else if (id == R.id.atmLimitsFragment)
                setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.update_limit));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }
}
