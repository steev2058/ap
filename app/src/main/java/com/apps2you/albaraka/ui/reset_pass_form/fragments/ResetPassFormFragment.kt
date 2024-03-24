package com.apps2you.albaraka.ui.reset_pass_form.fragments

import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentResetpassformBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.kyc.CompressImageTask
import com.apps2you.albaraka.ui.kyc.fragments.finishActivity
import com.apps2you.albaraka.viewmodels.ResetPassFormViewModel
import com.chaos.view.PinView
import com.google.firebase.messaging.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import java.util.Random

class ResetPassFormFragment : BaseFragment<FragmentResetpassformBinding, ResetPassFormViewModel>() {
    var responseWaiting: Boolean = false

    override fun createViewModel() {
        mViewModel = ViewModelProvider(mActivity).get(ResetPassFormViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_resetpassform
    }

    override fun setViewModel(): Class<ResetPassFormViewModel> {
        return ResetPassFormViewModel::class.java
    }

    override fun setUpView() {

        mViewDataBinding.btnSend.setOnClickListener {
            mViewDataBinding.layout.requestFocus()

            sendMobForm()
        }

        mViewDataBinding.agreementCheckbox1.setOnCheckedChangeListener { _, isChecked ->
            // If checkbox 1 is checked, send true to backend for reset_password
            mViewDataBinding.agreementCheckbox1.isChecked = isChecked
        }

        mViewDataBinding.agreementCheckbox3.setOnCheckedChangeListener { _, isChecked ->
            // If checkbox 3 is checked, send true to backend for reset_pin
            mViewDataBinding.agreementCheckbox3.isChecked= isChecked
        }

        setupAgreementCheckbox()
        setupCaptcha()


        updateSendButtonState()


        mViewDataBinding.agreementCheckbox2.setOnCheckedChangeListener { _, _ ->

            updateSendButtonState()
        }


        mViewDataBinding.btnSend.setOnClickListener {

            mViewDataBinding.layout.requestFocus()
            if (validateFields()) {
                sendMobForm()
            }
        }
    }


    private fun isPhoneNumber(input: String): Boolean {
        val phoneNoPattern = Regex("^\\(?([0-9]{3})\\)?[ ]?([0-9]{3})[ ]?([0-9]{4})\$")
        return input.matches(phoneNoPattern)
    }

    // Function to validate national numbers
    private fun isNationalNumber(input: String): Boolean {
        val phoneNoPattern = Regex("^\\d{8,}\$")
        return input.matches(phoneNoPattern)
    }

    // Function to validate CIF numbers
    private fun isCIF(input: String): Boolean {
        val phoneNoPattern = Regex("^\\d{5,}\$")
        return input.matches(phoneNoPattern)
    }
    private fun validateFields(): Boolean {
        var isValid = true


        if (mViewModel.resetPassForm.national_id != null && !isNationalNumber(mViewModel.resetPassForm.national_id!!)) {
            setInputError(mViewDataBinding.tiNationalNumber, getString(R.string.national_form_error))
            isValid = false
        }
        if (mViewModel.resetPassForm.cif_id != null && !isCIF(mViewModel.resetPassForm.cif_id!!)) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiCifNumber, getString(R.string.cif_form_error))
            isValid = false
        }

        if (mViewModel.resetPassForm.mobileNumber != null && !isPhoneNumber(mViewModel.resetPassForm.mobileNumber!!)) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiMobileNumber, getString(R.string.mobile_form_error))
            isValid = false
        }




        // setupCaptcha()
        val captchaTextView = mViewDataBinding.captchaTextView.text.toString()
        val captchaInput:String = mViewDataBinding.captchaInput.text.toString()
        if(captchaTextView.reversed().replace("\\s".toRegex(),"") == captchaInput) {
            // SendOtpReq()
            //  showToast("تم تسجيل طلبكم بنجاح")
        }
        else{
            showToast("الرقم المدخل غير مطابق حاول مرة اخرى")
        }

        return isValid
    }

    private fun updateSendButtonState() {
        // Enable send button only if the agreement checkbox is checked and all fields are valid
        mViewDataBinding.btnSend.isEnabled = mViewDataBinding.agreementCheckbox2.isChecked && validateFields()

        // Change button color based on enabled/disabled state
        if (mViewDataBinding.btnSend.isEnabled) {
            mViewDataBinding.btnSend.setBackgroundResource(R.drawable.bg_shadow_primary)
        } else {
            mViewDataBinding.btnSend.setBackgroundResource(R.drawable.bg_shadow_gray)
        }
    }




    private fun setupCaptcha() {
        // Function to generate a random CAPTCHA string
        fun generateCaptcha(): String {
            val random = Random()
            val number1 = random.nextInt(10)
            val number2 = random.nextInt(10)
            val number3 = random.nextInt(10)

            return "$number1    $number2    $number3"
        }

        // Initialize CAPTCHA elements
        val captchaTextView = mViewDataBinding.captchaTextView
        val captchaInput = mViewDataBinding.captchaInput
        val refreshButton = mViewDataBinding.refreshButton

        // Generate and display the initial CAPTCHA
        val initialCaptcha = generateCaptcha()
        captchaTextView.text = initialCaptcha

        // Set an OnClickListener for the Refresh button to generate and set a new CAPTCHA
        refreshButton.setOnClickListener {
            val newCaptcha = generateCaptcha()
            captchaTextView.text = newCaptcha
        }

    }


    private fun setupAgreementCheckbox(){
        val checkBox = mViewDataBinding.agreementCheckbox2
        val spannableString = SpannableString("افوض البنك بخصم مبلغ 2,000 ل.س من أي من حساباتي لدى بنك البركة لقاء تكاليف إعادة تعيين كلمة سر موبايل و أوافق على الشروط والأحكام")
        // Define a ForegroundColorSpan to color the text in blue
        val blueColor = ContextCompat.getColor(requireContext(), R.color.blue) // Replace with your blue color resource
        val blueText = "الشروط والأحكام"
        val blueColorSpan = ForegroundColorSpan(blueColor)
        // Find the starting index of the blue text
        val startIndex = spannableString.indexOf(blueText)
        // Apply the color span to the specific part of the text
        spannableString.setSpan(blueColorSpan, startIndex, startIndex + blueText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Set the styled text to the CheckBox
        checkBox.text = spannableString
        // Define the clickable span for "الشروط والأحكام"
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val builder = AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                val inflater = LayoutInflater.from(context)
                val dialogView = inflater.inflate(R.layout.conditions_modal, null)
                builder.setView(dialogView)

                val url = "https://albaraka.com.sy/AlBarakaForms/conditions"
                val webView: WebView = dialogView.findViewById(R.id.webView)
                val loader: ProgressBar = dialogView.findViewById(R.id.loader)

                webView.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        if (newProgress < 100) {
                            loader.visibility = View.VISIBLE
                        } else {
                            loader.visibility = View.GONE
                        }
                    }
                }

                webView.loadUrl(url)

                builder.setNegativeButton("إغلاق") { dialog, which ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }

            // Add this method to make the text appear as a link
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true // Underline the text
                ds.color = blueColor // Set the text color to blue
            }
        }
        // Set the clickable span only for the part you want to be clickable
        spannableString.setSpan(clickableSpan, startIndex, startIndex + blueText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Apply the formatted text to the CheckBox
        checkBox.text = spannableString
        // Make the CheckBox text appear as a link
        checkBox.movementMethod = LinkMovementMethod.getInstance()
        checkBox.highlightColor = Color.TRANSPARENT // Set the highlight color to transparent to remove the background color

    }



    override fun fetchData() {

    }

    private fun sendMobForm() {

        SendOtpReq()

    }

private fun showOtpDialog() {
    var otp : String = "";
    try {
        if (!activity?.isFinishing!!) {
            val builder = android.app.AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
            builder.setTitle("تم ارسال رمز التحقق (OTP) برسالة نصية:")
            val view = layoutInflater.inflate(R.layout.otp_screen, null)

            builder.setView(view)
            val pinView: PinView = view.findViewById<PinView>(R.id.pinView)

            // Add TextChangedListener if needed
            pinView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    Log.d(
                        Constants.TAG,
                        "onTextChanged() called with: s = [$s], start = [$start], before = [$before], count = [$count]"
                    )
                }

                override fun afterTextChanged(s: Editable) {

                    otp = s.toString();
                }
            });
//                val loader: ProgressBar = view.findViewById(R.id.loader)
            builder.setNegativeButton("موافق") { dialog, which ->
                mViewModel.sendResetPassForm(mViewDataBinding?.etNationalNumber?.text.toString(),mViewDataBinding?.etCifNumber?.text.toString(),otp,mViewDataBinding.agreementCheckbox3.isChecked.toString(),mViewDataBinding.agreementCheckbox1.isChecked.toString()).observe(this, {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            showToast(getString(R.string.sent_successfully))
                            mActivity.finish()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            showToast(it.message)
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                    }
                })
                //sendMobForm(otp, mViewDataBinding.agreementCheckbox3.isChecked, mViewDataBinding.agreementCheckbox1.isChecked)
            }



            builder.setPositiveButton("إعادة ارسال") { dialog, which ->
                // Handle positive button click
                SendOtpReq()
            }

            val dialog = builder.create()

            dialog.window?.decorView?.layoutDirection = View.LAYOUT_DIRECTION_RTL
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

    private fun sendOtpAndHandleResponse(cifNumber: String?) {
    // UI logic before sending OTP

    // Launch a coroutine in the background
    lifecycleScope.launch(Dispatchers.IO) {
        try {
            // Perform network operation here (replace this with your actual code)
            val response = sendOtp(cifNumber)

            // Switch back to the main thread to update UI
            withContext(Dispatchers.Main) {
              //  showProgress()
                if (response != null) {
                    showToast("تم إرسال رمز التحقق (OTP) بنجاح")
                    showOtpDialog()
                   // hideProgress()
//                        saveNewAccountRequest()
                } else {
                    showToast("حدث خطأ أثناء إرسال رمز التحقق (OTP) حاول مرة اخرى.")
                   // hideProgress()
                }
            }
        } catch (e: Exception) {
            // Handle exceptions here
            withContext(Dispatchers.Main) {
                showToast("فشل طلب الشبكة حاول مرة اخرى.")
                hideProgress()
            }
        }
    }}

        fun SendOtpReq() {
            val cif = mViewDataBinding?.etCifNumber?.text.toString()
            sendOtpAndHandleResponse(cif)
        }

    private fun sendOtp(vararg params: String?): String? {
        val cifNumber = params[0]
        try {
            val url = URL(com.apps2you.albaraka.utils.Constants.BASE_URL + "/api/send_otp")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true

            val postData: MutableMap<String, String?> = HashMap()
            postData["cif"] = cifNumber

            val requestBody = StringBuilder()
            for ((key, value) in postData) {
                if (requestBody.isNotEmpty()) {
                    requestBody.append("&")
                }
                requestBody.append(URLEncoder.encode(key, "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(value, "UTF-8"))
            }
            connection.outputStream.use { os ->
                val input = requestBody.toString().toByteArray(charset("UTF-8"))
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { br ->
                    val response = StringBuilder()
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    return response.toString()
                }
            } else {
                Log.e(Constants.TAG, "HTTP error code: $responseCode")
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }



    fun resetPasswordRequest(otp : String,resetPassword:Boolean, resetPin:Boolean) {


        GlobalScope.launch {
            try {

                responseWaiting = true

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("cif", mViewDataBinding?.etCifNumber?.text.toString())
                    .addFormDataPart("national_id", mViewDataBinding?.etNationalNumber?.text.toString())
                    .addFormDataPart("otp", otp)
                    .addFormDataPart("reset_password", resetPassword.toString()) // Add reset_password status
                    .addFormDataPart("reset_pin", resetPin.toString())

                val request = Request.Builder()
                    .url(com.apps2you.albaraka.utils.Constants.BASE_URL + "/api/reset_client")
                    .post(requestBody.build())
                    .build()

                val response = withContext(Dispatchers.IO) {
                    OkHttpClient().newCall(request).execute()
                }

                val responseData = response.body?.string()

                responseData?.let {
                    try {
                        withContext(Dispatchers.Main) {
                            showToast("تم بنجاح")
                            val intent = Intent(requireContext(), finishresetPassActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("حدث خطأ يرجى التأكد من صحة رمز التحقق والمحاولة مرة أخرى ")

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                responseWaiting = false
            }
        }

    }


}