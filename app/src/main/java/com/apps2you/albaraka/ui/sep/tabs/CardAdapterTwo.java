package com.apps2you.albaraka.ui.sep.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apps2you.albaraka.R;

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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardItemProfile item = mCardItemList.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
    }
}