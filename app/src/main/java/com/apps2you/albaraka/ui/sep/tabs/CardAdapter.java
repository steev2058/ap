package com.apps2you.albaraka.ui.sep.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.remote.networkUtils.NetworkBoundResource;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class CardAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<CardItem> mCardItemList;
    private final Picasso picasso;

    public CardAdapter(Context context, List<CardItem> cardItemList) {
        mContext = context;
        mCardItemList = cardItemList;

        // Setup a cache directory
        File cacheDir = new File(context.getCacheDir(), "picasso-cache");
        long cacheSize = 50 * 1024 * 1024; // 50 MB

        // Configure OkHttpClient with caching
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(cacheDir, cacheSize))
                .build();

        // Initialize Picasso with OkHttpClient
        this.picasso = new Picasso.Builder(mContext)
                .downloader(new OkHttp3Downloader(client))
                .build();
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

        // Load image with Picasso using caching
        picasso.load(item.getIconUrl())
                .placeholder(R.drawable.seplogob) // Optional placeholder
                .error(R.drawable.sep_icon) // Optional error placeholder
                .into(holder.cardImage);

        return convertView;
    }

    private static class ViewHolder {
        ImageView cardImage;
        TextView cardText;
    }

}
