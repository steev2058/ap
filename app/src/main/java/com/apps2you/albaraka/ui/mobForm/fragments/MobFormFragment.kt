package com.apps2you.albaraka.ui.mobForm.fragments

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.Branch
import com.apps2you.albaraka.data.model.Title
import com.apps2you.albaraka.data.model.getDefault
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentMobformBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter
import com.apps2you.albaraka.utils.CustomTextWatcher
import com.apps2you.albaraka.viewmodels.MobFormViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class MobFormFragment : BaseFragment<FragmentMobformBinding, MobFormViewModel>() {

    override fun createViewModel() {
        mViewModel = ViewModelProvider(mActivity).get(MobFormViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mobform
    }

    override fun setViewModel(): Class<MobFormViewModel> {
        return MobFormViewModel::class.java
    }

    override fun setUpView() {
       // mViewDataBinding.etTelephoneNumber.addTextChangedListener(CustomTextWatcher(mViewDataBinding.tiTelephoneNumber))
        mViewDataBinding.btnSend.setOnClickListener {
            mViewDataBinding.layout.requestFocus()
            sendMobForm()
        }

        setupAgreementCheckbox()
        setupCaptcha()

        // Set initial state of btn_send
        updateSendButtonState()

        // Listen for checkbox changes
        mViewDataBinding.agreementCheckbox2.setOnCheckedChangeListener { _, _ ->
            // Update send button state when checkbox state changes
            updateSendButtonState()
        }

        // Listen for send button click
        mViewDataBinding.btnSend.setOnClickListener {
            // Validate fields before sending form
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

        // Validate mobile number
//        if (TextUtils.isEmpty(mViewModel.mobForm.mobileNumber)) {
//            setInputError(mViewDataBinding.tiMobileNumber, getString(R.string.error_required))
//            isValid = false
//        }
        if (!mViewModel.mobForm.nationalNumber?.let { isNationalNumber(it) }!!) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiNationalNumber, getString(R.string.national_form_error))
            isValid = false
        }
        if (!mViewModel.mobForm.cif?.let { isCIF(it) }!!) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiCifNumber, getString(R.string.cif_form_error))
            isValid = false
        }

        if (!mViewModel.mobForm.mobileNumber?.let { isPhoneNumber(it) }!!) {
            // Check if the mobile number is valid
            setInputError(mViewDataBinding.tiMobileNumber, getString(R.string.mobile_form_error))
            isValid = false
        }
//        // Validate telephone number
//        if (TextUtils.isEmpty(mViewModel.mobForm.phoneNumber)) {
//            setInputError(mViewDataBinding.tiTelephoneNumber, getString(R.string.error_required))
//            isValid = false
//        }

        // Validate email
//        if (!Patterns.PHONE.matcher(mViewModel.mobForm.email ?: "").matches()) {
//            setInputError(mViewDataBinding.tiEmail, getString(R.string.error_required))
//            isValid = false
//        }




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
        val spannableString = SpannableString("افوض البنك بخصم مبلغ 8,000 ل.س من أي من حساباتي لدى بنك البركة لقاء تكاليف الاشتراك بخدمة البركة موبايل و أوافق على الشروط والأحكام")
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

//        if (TextUtils.isEmpty(mViewModel.mobForm.phoneNumber)) {
//            setInputError(mViewDataBinding.tiTelephoneNumber, getString(R.string.error_required))
//            return
//        }

        showToast("تم تسجيل طلبكم بنجاح")


        mViewModel.sendMobForm().observe(this, {
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
    }




}