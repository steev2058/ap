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
        mViewDataBinding.etTelephoneNumber.addTextChangedListener(CustomTextWatcher(mViewDataBinding.tiTelephoneNumber))
        mViewDataBinding.btnSend.setOnClickListener {
            mViewDataBinding.layout.requestFocus()
            sendMobForm()
        }

        setupAgreementCheckbox()
        setupCaptcha()
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
        val spannableString = SpannableString(" افوض البنك بخصم مبلغ 8,000 ل.س من أي من حساباتي لدى بنك البركة لقاء تكاليف الاشتراك بخدمة البركة موبايل\n" +
                "و أوافق على الشروط والأحكام")
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

                val url = "https://albaraka.com.sy/KYC/conditions"
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

        if (TextUtils.isEmpty(mViewModel.mobForm.phoneNumber)) {
            setInputError(mViewDataBinding.tiTelephoneNumber, getString(R.string.error_required))
            return
        }

        if (mViewModel.mobForm.email != null &&
            !Patterns.EMAIL_ADDRESS.matcher(mViewModel.mobForm.email!!).matches()) {
            showToast(getString(R.string.please_enter_a_valid_email))
            return
        }


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