package com.apps2you.albaraka.ui.loyaltyPoints;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.PointTransaction;


import java.util.ArrayList;

public class LoyaltyPointsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PointsAdapter adapter;
    private ArrayList<PointTransaction> pointList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty_points);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.loyalty_points);
        }
        recyclerView = findViewById(R.id.rv_points_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pointList = new ArrayList<>();
        pointList.add(new PointTransaction("Purchase at ABC Store", "+100", "2025-05-20"));
        pointList.add(new PointTransaction("Used for Discount", "200-", "2025-05-18"));
        pointList.add(new PointTransaction("Referral Bonus", "+500", "2025-05-15"));
        pointList.add(new PointTransaction("Transfer Bonus", "+100", "2025-05-15"));
        pointList.add(new PointTransaction("SEP Bonus", "+50", "2025-05-15"));
        pointList.add(new PointTransaction("Zakat Bonus", "+700", "2025-05-15"));
        pointList.add(new PointTransaction("Payment Bonus", "+300", "2025-05-15"));
        pointList.add(new PointTransaction("Baraka Bonus", "+100", "2025-05-15"));
        pointList.add(new PointTransaction("Eid Bonus", "+200", "2025-05-15"));
        pointList.add(new PointTransaction("Referral Bonus", "+900", "2025-05-15"));
        pointList.add(new PointTransaction("Referral Bonus", "+700", "2025-05-15"));
        pointList.add(new PointTransaction("Referral Bonus", "+500", "2025-05-15"));
        pointList.add(new PointTransaction("Referral Bonus", "+600", "2025-05-15"));


        adapter = new PointsAdapter(pointList);
        recyclerView.setAdapter(adapter);
    }
}
