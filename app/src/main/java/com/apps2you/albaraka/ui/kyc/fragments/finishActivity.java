package com.apps2you.albaraka.ui.kyc.fragments;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.utils.navigation.ActivityNavigation;

public class finishActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_activitykyc);


        Intent intent = getIntent();
        String responseMessage = intent.getStringExtra("response_message");

        // Find the TextView where you want to display the message
        TextView additionalText = findViewById(R.id.additionalText);

        // Set the message in the TextView
        additionalText.setText(responseMessage);
        Button buttonOk = findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(v -> redirectToHome());
        // Find views by their IDs
        ImageView imageView = findViewById(R.id.imageView);
//        ImageView checkmarkIcon = findViewById(R.id.checkmarkIcon);
        TextView titleTextView = findViewById(R.id.textView_title);
     //   TextView additionalTextView = findViewById(R.id.additionalText);

        // Assuming you have an ImageView with the animated-rotate drawable
        ImageView checkmarkIcon = findViewById(R.id.checkmarkIcon);
        checkmarkIcon.setImageResource(R.drawable.ic_checkmark);



    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }



}
