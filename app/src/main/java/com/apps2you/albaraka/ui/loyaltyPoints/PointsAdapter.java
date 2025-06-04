package com.apps2you.albaraka.ui.loyaltyPoints;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.PointTransaction;

import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {

    private List<PointTransaction> pointList;

    public PointsAdapter(List<PointTransaction> pointList) {
        this.pointList = pointList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvPoints, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    @NonNull
    @Override
    public PointsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PointsAdapter.ViewHolder holder, int position) {
        PointTransaction item = pointList.get(position);
        holder.tvDescription.setText(item.description);
        holder.tvPoints.setText(item.points);
        holder.tvDate.setText(item.date);
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }
}
