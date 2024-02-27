package com.apps2you.albaraka.ui.kyc.fragments;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.apps2you.albaraka.R;

public class finishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_activitykyc);

        // Find views by their IDs
        ImageView imageView = findViewById(R.id.imageView);
//        ImageView checkmarkIcon = findViewById(R.id.checkmarkIcon);
        TextView titleTextView = findViewById(R.id.textView_title);
        TextView additionalTextView = findViewById(R.id.additionalText);

        // Assuming you have an ImageView with the animated-rotate drawable
        ImageView checkmarkIcon = findViewById(R.id.checkmarkIcon);
        checkmarkIcon.setImageResource(R.drawable.ic_checkmark);






    }
}
