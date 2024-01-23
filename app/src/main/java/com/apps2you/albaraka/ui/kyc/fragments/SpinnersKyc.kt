package com.apps2you.albaraka.ui.kyc.fragments

//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.webkit.JavascriptInterface
//import android.webkit.WebChromeClient
//import android.webkit.WebResourceRequest
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Spinner
//import androidx.appcompat.app.AppCompatActivity
//import com.apps2you.albaraka.R
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import org.json.JSONArray
//
//class SpinnersKyc : AppCompatActivity() {
//
//    private var issueCard = false
//    private var deliverCard = false
//
//    // Declare your Spinners
//    private lateinit var genderSpinner: Spinner
//    private lateinit var gender2Spinner: Spinner
//    private lateinit var typeIdSpinner: Spinner
//    private lateinit var governorateSpinner: Spinner
//    private lateinit var universitySpinner: Spinner
//    private lateinit var collegeSpinner: Spinner
//    private lateinit var qanonSpinner: Spinner
//    private lateinit var countrySpinner: Spinner
//    private lateinit var branchSpinner: Spinner
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_kyc) // Replace with your layout resource
//
//        // Initialize your Spinners
//        genderSpinner = findViewById(R.id.gender_spinner)
//        gender2Spinner = findViewById(R.id.gender2_spinner)
//        typeIdSpinner = findViewById(R.id.type_id_spinner)
//        governorateSpinner = findViewById(R.id.governorate_spinner)
//        universitySpinner = findViewById(R.id.university_spinner)
//        collegeSpinner = findViewById(R.id.college_spinner)
//        qanonSpinner = findViewById(R.id.qanon_spinner)
//        countrySpinner = findViewById(R.id.country_spinner)
//        branchSpinner = findViewById(R.id.branch_spinner_id)
//
//        // ... Your existing WebView setup
//
//        // Load universities spinner data
//        loadSpinnerData(universitySpinner, "https://albaraka.com.sy/KYC/ApiController/universities", mapOf())
//
//        // Assuming you're using the Fuel library for HTTP requests
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                val (_, _, result) = Fuel.post("your_base_url/ApiController/getCities")
//                    .jsonBody(mapOf()) // Adjust this based on your API requirements
//                    .awaitStringResponseResult()
//
//                val data = result.component1()
//                val newOptions = StringBuilder("<option value=\"0\" hidden${if (data.obj()["cities"].length() != 1) " selected" else ""}>المحافظة</option>")
//                // Your existing code for populating cities...
//                document.querySelector('#city').innerHTML = newOptions.toString()
//                if (data.obj()["cities"].length() == 1) {
//                    toggleDelivery()
//                }
//            } catch (e: Exception) {
//                // Handle error
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//
//    private fun loadSpinnerData(spinner: Spinner, apiUrl: String, params: Map<String, String>) {
//        Fuel.post(apiUrl, parameters = params.toList())
//            .responseJson { _, _, result ->
//                result.fold(success = { data ->
//                    // Parse the data and update the spinner
//                    val spinnerData = parseSpinnerData(data)
//                    updateSpinnerData(spinner, spinnerData)
//                }, failure = { error ->
//                    // Handle error
//                    error.printStackTrace()
//                })
//            }
//    }
//
//    private fun parseSpinnerData(data: Any): List<String> {
//        // Implement the logic to parse data based on your API response
//        // For example, if your API returns a JSON array of strings
//        return if (data is JSONArray) {
//            (0 until data.length()).mapTo(mutableListOf()) { data.getString(it) }
//        } else {
//            emptyList()
//        }
//    }
//}
