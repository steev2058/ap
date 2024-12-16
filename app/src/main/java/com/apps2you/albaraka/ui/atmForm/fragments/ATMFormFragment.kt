package com.apps2you.albaraka.ui.atmForm.fragments

import android.widget.CheckBox
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.MyApplication
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.User
import com.apps2you.albaraka.data.preference.UserUtils
import com.apps2you.albaraka.databinding.FragmentAtmformBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.ATMFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Random

class ATMFormFragment  : BaseFragment<FragmentAtmformBinding, ATMFormViewModel>() {
    var responseWaiting: Boolean = false
    private var user: User? = null
    lateinit var checkbox1: CheckBox
    lateinit var checkbox2: CheckBox
    lateinit var checkbox3: CheckBox
    private lateinit var viewModel: ATMFormViewModel

    private lateinit var progressDialog: SweetAlertDialog
    override fun createViewModel() {
        mViewModel = ViewModelProvider(mActivity).get(ATMFormViewModel::class.java)
        user = UserUtils(MyApplication.getAppContext()).user
        progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("جارٍ معالجة الطلب...")
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    fun getUser(): User? {
        return user
    }




    override fun getLayoutId(): Int {
        return R.layout.fragment_atmform
    }

    override fun setViewModel(): Class<ATMFormViewModel> {
        return ATMFormViewModel::class.java
    }

    override fun setUpView() {

        mViewDataBinding.btnSend.setOnClickListener {
            mViewDataBinding.layout.requestFocus()
            sendATMForm()
        }

//        setupAgreementCheckbox()
//        setupCaptcha()




        checkbox1 = mViewDataBinding.checkbox1
        checkbox2 = mViewDataBinding.checkbox2
        checkbox3 = mViewDataBinding.checkbox3

        // Set initial state of btn_send
        updateSendButtonState()

        // Listen for checkbox changes
        checkbox1.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }
        checkbox2.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }
        checkbox3.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }
        mViewDataBinding.checkbox1.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }
        mViewDataBinding.checkbox2.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }
        mViewDataBinding.checkbox3.setOnCheckedChangeListener { _, _ -> updateSendButtonState() }



        // Listen for send button click
        mViewDataBinding.btnSend.setOnClickListener {
            // Validate fields before sending form
            mViewDataBinding.layout.requestFocus()
            if (validateFields()) {
                sendATMForm()
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


        if (mViewModel.atmForm.national_id != null && !isNationalNumber(mViewModel.atmForm.national_id!!)) {
            setInputError(mViewDataBinding.tiNationalNumber, getString(R.string.national_form_error))
            isValid = false
        }
        if (mViewModel.atmForm.cif_id != null && !isCIF(mViewModel.atmForm.cif_id!!)) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiCifNumber, getString(R.string.cif_form_error))
            isValid = false
        }

        if (mViewModel.atmForm.mobileNumber != null && !isPhoneNumber(mViewModel.atmForm.mobileNumber!!)) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiMobileNumber, getString(R.string.mobile_form_error))
            isValid = false
        }




        // setupCaptcha()
//        val captchaTextView = mViewDataBinding.captchaTextView.text.toString()
//        val captchaInput:String = mViewDataBinding.captchaInput.text.toString()
//        if(captchaTextView.reversed().replace("\\s".toRegex(),"") == captchaInput) {
//            // SendOtpReq()
//            //  showToast("تم تسجيل طلبكم بنجاح")
//        }
//        else{
//            showToast("الرقم المدخل غير مطابق حاول مرة اخرى")
//        }

        return isValid
    }

    private fun updateSendButtonState() {
        // Enable send button only if the agreement checkbox is checked and all fields are valid
        mViewDataBinding.btnSend.isEnabled = mViewDataBinding.checkbox1.isChecked && mViewDataBinding.checkbox2.isChecked && mViewDataBinding.checkbox3.isChecked && validateFields()

        // Change button color based on enabled/disabled state
        if (mViewDataBinding.btnSend.isEnabled) {
            mViewDataBinding.btnSend.setBackgroundResource(R.drawable.bg_shadow_primary)
        } else {
            mViewDataBinding.btnSend.setBackgroundResource(R.drawable.bg_shadow_gray)
        }
    }




//    private fun setupCaptcha() {
//        // Function to generate a random CAPTCHA string
//        fun generateCaptcha(): String {
//            val random = Random()
//            val number1 = random.nextInt(10)
//            val number2 = random.nextInt(10)
//            val number3 = random.nextInt(10)
//
//            return "$number1    $number2    $number3"
//        }
//
//        // Initialize CAPTCHA elements
////        val captchaTextView = mViewDataBinding.captchaTextView
////        val captchaInput = mViewDataBinding.captchaInput
////        val refreshButton = mViewDataBinding.refreshButton
//
//        // Generate and display the initial CAPTCHA
////        val initialCaptcha = generateCaptcha()
////        captchaTextView.text = initialCaptcha
////
////        // Set an OnClickListener for the Refresh button to generate and set a new CAPTCHA
////        refreshButton.setOnClickListener {
////            val newCaptcha = generateCaptcha()
////            captchaTextView.text = newCaptcha
////        }
//
//    }





    override fun fetchData() {

    }

    private fun sendATMForm() {
        progressDialog.show()
       atmFormRequest()
    }

    private fun atmFormRequest() {


        GlobalScope.launch {
            try {

                responseWaiting = true

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("national_id", user?.phone ?: "")
                    .addFormDataPart("cif_id", user?.cif_number ?: "")
                    .addFormDataPart("mobile_id", user?.phone ?: "")
                    .addFormDataPart("skip_captcha", "true")

                val request = Request.Builder()
                    .url("https://albaraka.com.sy/ATM/saveData")
                    .post(requestBody.build())
                    .build()

                val response = withContext(Dispatchers.IO) {
                    OkHttpClient().newCall(request).execute()
                }

                val responseData = response.body?.string()

                responseData?.let {
                    try {
                        val jsonResponse = JSONObject(it)
                        withContext(Dispatchers.Main) {
                            if (jsonResponse.getBoolean("done")) {
                                showToast("تم إرسال الطلب بنجاح")
                                progressDialog.hide()
                                mActivity.finish()
                            } else {

                                showToast(jsonResponse.getString("message"))
                                // Handle accordingly
                            }
                            progressDialog.hide()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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