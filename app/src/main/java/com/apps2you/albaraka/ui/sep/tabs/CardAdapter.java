package com.apps2you.albaraka.ui.sep.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps2you.albaraka.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardAdapter extends BaseAdapter {

    private Context mContext;
    private List<CardItem> mCardItemList;

    public CardAdapter(Context context, List<CardItem> cardItemList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
            holder = new ViewHolder();
            holder.cardImage = convertView.findViewById(R.id.card_image);
            holder.cardText = convertView.findViewById(R.id.card_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CardItem item = mCardItemList.get(position);
        holder.cardText.setText(item.getText());

        // Load image using Picasso
        Picasso.get().load(item.getIconUrl()).into(holder.cardImage);
        return convertView;
    }


    private static class ViewHolder {
        ImageView cardImage;
        TextView cardText;
    }
}
