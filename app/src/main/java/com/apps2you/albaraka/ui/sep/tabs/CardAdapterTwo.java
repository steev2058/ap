package com.apps2you.albaraka.ui.sep.tabs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.sep.bill.BillDetailsFragment;
import com.apps2you.albaraka.ui.sep.bill.BillFragment;
import com.apps2you.albaraka.ui.sep.profile.UserData;
import com.apps2you.albaraka.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
public class CardAdapterTwo extends BaseAdapter {
    private Context mContext;
    private List<CardItemProfile> mCardItemList;
    private Fragment mFragment;
    private String token;

    public CardAdapterTwo(Context context, List<CardItemProfile> cardItemList, String token,Fragment fragment) {
        mContext = context;
        mCardItemList = cardItemList;
        this.token = token;
        mFragment = fragment;
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
            holder.buttonDelete = convertView.findViewById(R.id.btn_delete);
            holder.buttonSearch = convertView.findViewById(R.id.btn_search);
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

        // Set delete button listener
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog(item.getId(), position);
            }
        });

        // Set search button listener
        holder.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmSearchDialog(item.getId(), position,item.getBillerCode());
            }
        });


        // Set image and button listeners here if needed
        return convertView;
    }

    private void showConfirmSearchDialog(final String id, final int position,String billerCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_sep_search, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();

        Button buttonSubmit = dialogView.findViewById(R.id.button_submit);
        Button buttonCancel = dialogView.findViewById(R.id.cancel_button);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SearchBillTask().execute(id,billerCode);
                alertDialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private class SearchBillTask extends AsyncTask<String, Void, String> {
        String billerCode;
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            billerCode= params[1];
            try {
                URL url = new URL(Constants.BASE_URL_SEP + "/Services_Interface/bank_bill_presentment?id=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("token", token);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    handleSearchResponse(jsonObject,billerCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed to parse response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Search failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSearchResponse(JSONObject response,String billerCode) {
        Bundle bundle = new Bundle();
        bundle.putString("responseData", response.toString());
        bundle.putString("billerCode", billerCode);
        NavHostFragment.findNavController(mFragment)
                .navigate(R.id.action_firstFragment_to_fragment_sep_details_profile, bundle);
    }


    private void showConfirmDeleteDialog(final String id, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_sep_delete, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();

        Button buttonSubmit = dialogView.findViewById(R.id.button_submit);
        Button buttonCancel = dialogView.findViewById(R.id.cancel_button);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteBillTask().execute(id, String.valueOf(position));
                alertDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private class DeleteBillTask extends AsyncTask<String, Void, Boolean> {
        private int position;
        //String token = userData.getToken();
        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            position = Integer.parseInt(params[1]);
            try {
                URL url = new URL(Constants.BASE_URL_SEP+"/Services_Interface/remove_customer_profile/?id=" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("token", token);
                connection.connect();

                int responseCode = connection.getResponseCode();
                return responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                mCardItemList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(mContext, "تم حذف الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "فشل عملية حذف الفاتورة", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private static class ViewHolder {
        TextView title;
        TextView description;
        TextView serviceName;
        TextView billingNo;
        TextView status;
        ImageView image;
        Button buttonSearch;
        Button buttonDelete;
    }

}
