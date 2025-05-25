package com.apps2you.albaraka.ui.devices;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Build;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.MyDevices;
import com.apps2you.albaraka.databinding.ActivityMyDevicesBinding;
import com.apps2you.albaraka.databinding.ActivityPrivacyPolicyBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.MyDevicesVM;
import com.apps2you.albaraka.viewmodels.PrivacyPolicyVM;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MyDevicesActivity extends BaseActivity<ActivityMyDevicesBinding, MyDevicesVM> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_devices;
    }

    @Override
    public Class<MyDevicesVM> setViewModel() {
        return MyDevicesVM.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.my_devices));


    }

    @Override
    public void fetchData() {
        RecyclerView recyclerView = getViewDataBinding().recyclerViewDevices;
        DevicesAdapter adapter = new DevicesAdapter(new ArrayList<>(), Build.MODEL, getViewModel(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getViewModel().getMyDevices().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    if (resource.data != null && resource.data != null) {
                        adapter.deviceList.clear();
                        adapter.deviceList.addAll(resource.data);
                        adapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    getViewDataBinding().progressBar.setVisibility(View.GONE);
                    showToast(resource.message);
            }
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't support moving items
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                MyDevices device = adapter.deviceList.get(position);

                // Optional: Show confirmation dialog
                new AlertDialog.Builder(MyDevicesActivity.this)
                        .setTitle("تأكيد الحذف")
                        .setMessage("هل أنت متأكد أنك تريد حذف هذا الجهاز؟")
                        .setPositiveButton("نعم", (dialog, which) -> {
                            adapter.showProgress();
                            getViewModel().deleteDevice(device.getId()).observe(MyDevicesActivity.this, result -> {
                                switch (result.status) {
                                    case SUCCESS:
                                        adapter.hideProgress();
                                        Toast.makeText(MyDevicesActivity.this, "تم حذف الجهاز", Toast.LENGTH_SHORT).show();
                                        adapter.deviceList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        break;
                                    case ERROR:
                                        adapter.hideProgress();
                                        Toast.makeText(MyDevicesActivity.this, "فشل الحذف: " + result.message, Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemChanged(position); // Restore item
                                        break;
                                }
                            });
                        })
                        .setNegativeButton("إلغاء", (dialog, which) -> {
                            dialog.dismiss();
                            adapter.notifyItemChanged(position); // Cancel deletion
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // Optionally customize swipe background (e.g. red delete background)
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MyDevicesActivity.this, R.color.red))
                        .addActionIcon(R.drawable.ic_delete_white) // Replace with your delete icon
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);



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
