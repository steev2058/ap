package com.apps2you.albaraka.ui.sep.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.StorageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder> {
    private List<JSONObject> paymentsList;
    private Context context;
    private final int RC_PERMISSION_WRITE = 123;

    public PaymentsAdapter(Context context,List<JSONObject> paymentsList) {
        this.paymentsList = paymentsList;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        JSONObject payment = paymentsList.get(position);

        try {
            String paidAmountStr = payment.getString("PaidAmt");
            String dueAmountStr = payment.getString("DueAmt");

            double paidAmount = Double.parseDouble(paidAmountStr);
            double dueAmount = Double.parseDouble(dueAmountStr);
            double totalAmount = paidAmount + dueAmount;
            holder.billerName.setText("المفوتر: " + payment.getString("billerName_ar"));
            holder.billingNo.setText("معرف الخدمة: " + payment.getString("BillingNo"));
            holder.paymentDate.setText("تاريخ الدفع: " + payment.getString("payment_date"));
            holder.totalPaidAmount.setText("المجموع: " + totalAmount);

//            holder.pdfButton.setOnClickListener(v -> {
//                try {
//                    String billId = payment.getString("id");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions((Activity) context,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSION_WRITE);
//                        } else {
//                            downloadPdfUrl(billId);
//                        }
//                    } else {
//                        downloadPdfUrl(billId);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            });
            View pdfButton = holder.itemView.findViewById(R.id.btn_pdf);
            pdfButton.setOnClickListener(v -> {
                try {
                    String billId = payment.getString("id");

                        downloadPdfUrl(billId);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void downloadPdfUrl(String billId) {
        String url = Constants.BASE_URL_SEP + "/Customer/print_bill2?id=" + billId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("فشل في التحميل");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String fileUrl = jsonObject.getString("data");
                        downloadFile(fileUrl, billId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("فشل في معالجة البيانات");
                    }
                } else {
                    showToast("فشل في الاتصال بالخادم");
                }
            }
        });
    }

    private void downloadFile(String fileUrl, String billId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fileUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("فشل في التحميل");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Use StorageUtils to save the file
                    StorageUtils storageUtils = new StorageUtils();
                    if (storageUtils.saveToFile(context, response.body(), "bill_" + billId + "_")) {
                        showToast("تم تحميل الملف في التنزيلات");
                    } else {
                        showToast("فشل في حفظ الملف");
                    }
                } else {
                    showToast("فشل في الاتصال بالخادم");
                }
            }
        });
    }

    private void showToast(String message) {
        ((Activity) context).runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

//    private void downloadPdfUrl(String billId) {
//        String url = Constants.BASE_URL_SEP + "/Customer/print_bill2?id=" + billId;
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(url).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    try {
//                        String jsonData = response.body().string();
//                        JSONObject jsonObject = new JSONObject(jsonData);
//                        String fileUrl = jsonObject.getString("data");
//                        downloadFile(fileUrl, billId);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    // Handle the error case
//                }
//            }
//        });
//    }
//
//    private void downloadFile(String fileUrl, String billId) {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(fileUrl).build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    byte[] pdfData = response.body().bytes();
//                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                    if (!downloadDir.exists()) {
//                        downloadDir.mkdirs();
//                    }
//                    File pdfFile = new File(downloadDir, "bill_" + billId + ".pdf");
//                    try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                        fos.write(pdfData);
//                        fos.flush();
//                        showToast("تم تحميل الملف في التنزيلات");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    // Handle the error case
//                }
//            }
//        });
//    }
//
//    private void showToast(String message) {
//        ((Activity) context).runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
//    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView billerName, billingNo,paymentDate, paidAmount, dueAmount,totalPaidAmount,pdfButton;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            billerName = itemView.findViewById(R.id.tv_biller_name);
            billingNo = itemView.findViewById(R.id.tv_billing_no);
            paymentDate = itemView.findViewById(R.id.tv_payment_date);
          //  paidAmount = itemView.findViewById(R.id.tv_paid_amount);
         //   dueAmount = itemView.findViewById(R.id.tv_due_amount);
            totalPaidAmount = itemView.findViewById(R.id.tv_total_paid_amount);
            pdfButton=itemView.findViewById(R.id.btn_pdf);
        }
    }
}
