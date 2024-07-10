package com.apps2you.albaraka.ui.sep.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder> {
    private List<JSONObject> paymentsList;

    public PaymentsAdapter(List<JSONObject> paymentsList) {
        this.paymentsList = paymentsList;
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
            holder.billerName.setText(payment.getString("billerName_ar"));
            holder.paymentDate.setText(payment.getString("payment_date"));
            holder.paidAmount.setText(payment.getString("PaidAmt"));
            holder.dueAmount.setText(payment.getString("DueAmt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView billerName, paymentDate, paidAmount, dueAmount;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            billerName = itemView.findViewById(R.id.tv_biller_name);
            paymentDate = itemView.findViewById(R.id.tv_payment_date);
            paidAmount = itemView.findViewById(R.id.tv_paid_amount);
            dueAmount = itemView.findViewById(R.id.tv_due_amount);
        }
    }
}
