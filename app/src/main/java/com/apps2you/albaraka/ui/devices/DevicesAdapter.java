package com.apps2you.albaraka.ui.devices;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;

import com.apps2you.albaraka.data.model.MyDevices;
import com.apps2you.albaraka.data.remote.networkUtils.Status;
import com.apps2you.albaraka.viewmodels.MyDevicesVM;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.annotations.NonNull;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    final List<MyDevices> deviceList;
    String currentDeviceId;
    private final LifecycleOwner lifecycleOwner;
    private SweetAlertDialog progressDialog;

    MyDevicesVM viewModel;

    public DevicesAdapter(List<MyDevices> deviceList, String currentDeviceId, MyDevicesVM viewModel, LifecycleOwner lifecycleOwner) {
        this.deviceList = deviceList;
        this.currentDeviceId = currentDeviceId;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }
    protected void showProgress() {
        progressDialog.show();
    }
    protected void hideProgress() {
        progressDialog.dismiss();
    }
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {

        progressDialog = new SweetAlertDialog(holder.itemView.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        // progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        progressDialog.getProgressHelper().setBarColor(holder.itemView.getResources().getColor(R.color.colorAccent));
        progressDialog.setContentText(holder.itemView.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        MyDevices device = deviceList.get(position);
        holder.deviceName.setText(device.getDevice());
        holder.lastLogin.setText(device.getLastLogin());

        updateTrustedButton(holder.btnTrusted, device.isTrusted() == 1);

        // Highlight if current device
        if (String.valueOf(device.getDevice()).contains(currentDeviceId)) {
            if (device.isTrusted() == 1) {
                holder.status.setText("الجهاز الحالي");
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                holder.status.setBackgroundResource(R.drawable.rounded_background_g);
            } else {
                holder.status.setText("");
                holder.status.setVisibility(View.VISIBLE); // You can set GONE if you want to hide untrusted
                holder.status.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                holder.status.setBackgroundResource(R.drawable.rounded_background_red);
            }

        }



        holder.btnTrusted.setOnClickListener(v -> {
            boolean isNowTrusted = device.isTrusted() != 1;
            int newStatus = isNowTrusted ? 1 : 0;

            showConfirmationDialog(
                    holder.itemView.getContext(),
                    holder.itemView.getContext().getString(R.string.logout_title),
                    holder.itemView.getContext().getString(R.string.logout_confirmation),
                    () -> {
                        showProgress(); // Show loading

                        viewModel.changeTrusting(device.getId(), newStatus).observe((LifecycleOwner) holder.itemView.getContext(), result -> {
                            switch (result.status) {
                                case LOADING:
                                    break; // Already showing progress
                                case SUCCESS:
                                    hideProgress(); // Hide loading
                                    Toast.makeText(holder.itemView.getContext(), "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                                    device.setTrusted(newStatus);
                                    updateTrustedButton(holder.btnTrusted, isNowTrusted);
                                    notifyItemChanged(position);
                                    break;
                                case ERROR:
                                    hideProgress(); // Hide loading
                                    Toast.makeText(holder.itemView.getContext(), "فشل في التحديث", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        });
                    }
            );
        });






        holder.btnDelete.setOnClickListener(v -> {
            viewModel.deleteDevice(device.getId()).observe(lifecycleOwner, result -> {
                switch (result.status) {
                    case LOADING:
                        showProgress();
                        break;
                    case SUCCESS:
                        Toast.makeText(holder.btnDelete.getContext(), "تم حذف الجهاز", Toast.LENGTH_SHORT).show();
                        hideProgress();
                        deviceList.remove(position);
                        notifyItemRemoved(position);
                        break;
                    case ERROR:
                        Toast.makeText(holder.btnDelete.getContext(), result.message, Toast.LENGTH_SHORT).show();
                        hideProgress();
                        break;
                }
            });
        });

        //holder.trustedIcon.setImageResource(device.isTrusted() == 1 ? R.drawable.checklist : R.drawable.alert);
    }
    private void updateTrustedButton(Button button, boolean isTrusted) {
        if (isTrusted) {
            button.setText("موثوق");
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5800")));

        } else {
            button.setText("غير موثوق");
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5E6167")));

        }
    }

    private void showConfirmationDialog(Context context, String title, String message, Runnable onConfirm) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        //TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button btnConfirm = dialogView.findViewById(R.id.button_confirm);
        Button btnCancel = dialogView.findViewById(R.id.button_cancel);

        dialogTitle.setText(title);
      //  dialogMessage.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnConfirm.setOnClickListener(v -> {
            onConfirm.run();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }



    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, lastLogin;
        ImageView trustedIcon;
        Button btnTrusted;
        ImageButton btnDelete;
        TextView status;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            lastLogin = itemView.findViewById(R.id.lastLogin);
          status  = itemView.findViewById(R.id.card_status);
           // trustedIcon = itemView.findViewById(R.id.trustedIcon);
            btnTrusted = itemView.findViewById(R.id.btnTrusted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
