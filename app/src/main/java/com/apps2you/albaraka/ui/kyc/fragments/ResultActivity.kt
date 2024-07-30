package com.apps2you.albaraka.ui.kyc.fragments

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.apps2you.albaraka.R

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Retrieve the decodedString from the intent
        val decodedString = intent.getStringExtra("decodedString")

        // Split the decodedString using the '#' character as a delimiter
        val parts = decodedString!!.split("#".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()

        // Find the TextViews or other UI elements in your new layout
        val part1TextView = findViewById<TextView>(R.id.part1TextView)
        val part2TextView = findViewById<TextView>(R.id.part2TextView)
        val part3TextView = findViewById<TextView>(R.id.part3TextView)
        val part4TextView = findViewById<TextView>(R.id.part4TextView)
        val part5TextView = findViewById<TextView>(R.id.part5TextView)
        val part6TextView = findViewById<TextView>(R.id.part6TextView)

        // Display the parts in the respective TextViews
        if (parts.size >= 1) {
            part1TextView.text = "الاسم : " + parts[0]
        }
        if (parts.size >= 2) {
            part2TextView.text = "الكنيه : " + parts[1]
        }
        if (parts.size >= 3) {
            part3TextView.text = "اسم الاب : " + parts[2]
        }
        if (parts.size >= 4) {
            part4TextView.text = "اسم الام والنسبه : " + parts[3]
        }
        if (parts.size >= 5) {
            part5TextView.text = "محل وتاريخ الولادة : " + parts[4]
        }
        if (parts.size >= 6) {
            part6TextView.text = "الرقم الوطني : " + parts[5]
        }
    }
}