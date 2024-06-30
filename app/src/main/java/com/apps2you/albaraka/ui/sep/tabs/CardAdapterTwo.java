package com.apps2you.albaraka.ui.sep.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps2you.albaraka.R;
import com.squareup.picasso.Picasso;

import java.util.List;
public class CardAdapterTwo extends BaseAdapter {
    private Context mContext;
    private List<CardItemProfile> mCardItemList;

    public CardAdapterTwo(Context context, List<CardItemProfile> cardItemList) {
        mContext = context;
        mCardItemList = cardItemList;
    }

    @Override
    public int getCount() {
        return mCardItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCardItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_profile_item, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.card_title);
            holder.description = convertView.findViewById(R.id.card_description);
            holder.serviceName = convertView.findViewById(R.id.card_service_name);
            holder.billingNo = convertView.findViewById(R.id.card_billing_no);
            holder.status = convertView.findViewById(R.id.card_status);
            holder.image = convertView.findViewById(R.id.left_image);
            holder.button = convertView.findViewById(R.id.btn_delete);
            holder.button = convertView.findViewById(R.id.btn_search);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CardItemProfile item = mCardItemList.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.serviceName.setText(item.getServiceNameAr());
        Picasso.get().load(item.getIconUrl()).into(holder.image);
        holder.billingNo.setText(item.getBillingNo());
    //    holder.status.setText(item.getIsDeleted() == 0 ? "مفعلة" : "غير مفعلة");
        if (item.getIsDeleted() == 0) {
            holder.status.setText("مفعلة");
            holder.status.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.status.setBackgroundResource(R.drawable.rounded_background_g);
        } else {
            holder.status.setText("غير مفعلة");
            holder.status.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.status.setBackgroundResource(R.drawable.rounded_background_red);
        }



        // Set image and button listeners here if needed
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
        TextView serviceName;
        TextView billingNo;
        TextView status;
        ImageView image;
        Button button;
    }

}
