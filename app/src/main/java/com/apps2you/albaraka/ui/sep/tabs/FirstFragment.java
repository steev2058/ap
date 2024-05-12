package com.apps2you.albaraka.ui.sep.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps2you.albaraka.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private GridView gridView;
    private CardAdapter adapter;
    private ProgressBar loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.first_tab, container, false);
        gridView = rootView.findViewById(R.id.grid_view);
        loader = rootView.findViewById(R.id.loader);


        loader.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        // Execute AsyncTask to fetch data from the API
        new FetchCategoriesTask().execute();

        return rootView;
    }

    private class FetchCategoriesTask extends AsyncTask<Void, Void, List<CardItem>> {

        @Override
        protected List<CardItem> doInBackground(Void... voids) {
            List<CardItem> cardItemList = new ArrayList<>();
            InputStream inputStream = null;

            try {
                URL url = new URL("http://epaytest.albaraka.com.sy:4433/SEP/Customer/all");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String json = stringBuilder.toString();

                JSONObject jsonObject = new JSONObject(json);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray categories = data.getJSONArray("categories");

                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);
                    String categName_ar = category.getString("categName_ar");
                    String iconUrl = category.getString("icon");
                    // Load the image using Picasso library
                    // Make sure to add Picasso dependency in your build.gradle file
                    // implementation 'com.squareup.picasso:picasso:2.71828'
                    cardItemList.add(new CardItem(categName_ar, iconUrl.replace("..", "http://epaytest.albaraka.com.sy:4433")));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return cardItemList;
        }

        @Override
        protected void onPostExecute(List<CardItem> cardItemList) {
            super.onPostExecute(cardItemList);

            // Hide loader
            loader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

            // Set up the adapter for the GridView
            adapter = new CardAdapter(getContext(), cardItemList);
            gridView.setAdapter(adapter);
        }

    }
}
