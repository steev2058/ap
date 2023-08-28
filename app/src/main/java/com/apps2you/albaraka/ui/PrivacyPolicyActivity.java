package com.apps2you.albaraka.ui;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityPrivacyPolicyBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.PrivacyPolicyVM;


public class PrivacyPolicyActivity extends BaseActivity<ActivityPrivacyPolicyBinding, PrivacyPolicyVM> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    public Class<PrivacyPolicyVM> setViewModel() {
        return PrivacyPolicyVM.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.privacy_policy));
    }

    @Override
    public void fetchData() {
        getViewModel().getPrivacyPolicy().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);

                    if (resource.data != null)
                        getViewDataBinding().textView.setText(HtmlCompat.fromHtml(resource.data.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    break;

                default:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    showToast(resource.message);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void listenToVariables() {

    }
}
