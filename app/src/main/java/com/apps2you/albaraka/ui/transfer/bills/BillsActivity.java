package com.apps2you.albaraka.ui.transfer.bills;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityBillsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.common.busEvent.RefreshAccountsEvent;
import com.apps2you.albaraka.utils.bus.Bus;
import com.apps2you.albaraka.viewmodels.transfer.BillsVM;

import java.util.ArrayList;
import java.util.List;

import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;


public class BillsActivity extends BaseActivity<ActivityBillsBinding, BillsVM> {

    @Override
    public Class<BillsVM> setViewModel() {
        return BillsVM.class;
    }

    @Override
    public void fetchData() {

            getViewModel().getBillsLink().observe(this, resource -> {
                switch (resource.status) {
                    case LOADING:
                        showProgress();
                        break;

                    case SUCCESS:
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                        Drawable drawable = ContextCompat.getDrawable(BillsActivity.this, R.drawable.ic_arrow_back);
                        builder.setCloseButtonIcon(bitmapFromVectorDrawable((VectorDrawable) drawable));

                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(this, Uri.parse((resource.data)));
                        finish();
                        break;

                    case ERROR:
                        showToast(R.string.error_occurred);
                        finish();
                        break;
                }
            });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideProgress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bus.instance().publish(RefreshAccountsEvent.getInstance());
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public void setUpView() {
    }

    @Override
    public void listenToVariables() {

    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    private ArrayList<ResolveInfo> getCustomTabsPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.fromParts("http", "", null));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList<ResolveInfo> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info);
            }
        }
        return packagesSupportingCustomTabs;
    }

    private Bitmap bitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
