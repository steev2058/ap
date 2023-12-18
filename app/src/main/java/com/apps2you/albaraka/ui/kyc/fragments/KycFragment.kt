package com.apps2you.albaraka.ui.kyc.fragments
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.MyApplication
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentKycBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.viewmodels.KycViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.internal.http2.Http2Reader
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class KycFragment  : BaseFragment<FragmentKycBinding, KycViewModel>() , ZXingScannerView.ResultHandler {

    override fun getLayoutId(): Int {
        return R.layout.fragment_kyc
    }
    private var position = 0
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_QR_REQUEST = 2
    private val SCANNER_REQUEST_CODE = 123
    private lateinit var imageUploadView1: ImageView
    private lateinit var imageUploadView2: ImageView
    private lateinit var imageUploadView3: ImageView
    private lateinit var imageUploadView4: LinearLayout
    private lateinit var jobbh: TextInputEditText
    private var currentImageViewTag: Int = 0
    private val imageViewList = mutableListOf<Pair<ImageView, Int>>()
    private lateinit var governorateSpinner: Spinner
    private lateinit var universitySpinner: Spinner
    private lateinit var collegeSpinner: Spinner
    private lateinit var studentStatusInput: TextInputEditText
    private lateinit var zxingScannerView: ZXingScannerView
    private lateinit var scanButton: Button
    private lateinit var browseButton: Button
    private lateinit var datePicker : DatePicker
     private lateinit var binding: FragmentKycBinding

    private lateinit var nationalNumberEditText: TextInputEditText
    private lateinit var mobNumEditText: TextInputEditText

    // The Stepper Things
    private fun setupStepView(){
        mViewDataBinding.stepView.done(false)
        mViewDataBinding.button.setOnClickListener {

            when (position) {
                0 -> goToStep(1)
                1 -> goToStep(2)
                2 -> goToStep(3)
                3 -> goToStep(4)
                else -> {
                    position = 0
                    mViewDataBinding.stepView.done(true)
                    goToStep(position)
                }

            }}

        mViewDataBinding.previousButton.setOnClickListener {
            when (position) {
                0 -> {
                    // No previous step on the first screen
                    // You can handle this as needed (e.g., go back to a previous activity or fragment)
                }
                1 -> goToStep(0)
                2 -> goToStep(1)
                3 -> goToStep(2)
                else -> goToStep(3)
            }
        }

        mViewDataBinding.radio.setOnCheckedChangeListener { _, _ ->
            handleRadioButtons()
        }
    }
    private fun applyMasks() {
        nationalNumberEditText.addTextChangedListener(MaskWatcher("###-##-#####"))
        mobNumEditText.addTextChangedListener(MaskWatcher("###-#######"))
    }
    private inner class MaskWatcher(private val mask: String) : TextWatcher {
        private var isRunning = false
        private var isDeleting = false

        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            // No implementation needed
        }

        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            // No implementation needed
        }

        override fun afterTextChanged(editable: Editable?) {
            if (isRunning || isDeleting) {
                return
            }

            isRunning = true

            // Remove previous masks
            val cleanText = editable?.toString()?.replace("[^\\d]".toRegex(), "")

            if (cleanText != null) {
                var formatted = ""
                var index = 0

                for (element in mask.toCharArray()) {
                    if (index == cleanText.length) {
                        break
                    }

                    if (element == '#') {
                        formatted += cleanText[index]
                        index++
                    } else {
                        formatted += element
                    }
                }

                isDeleting = formatted.length < editable.length

                editable.replace(0, editable.length, formatted)
            }

            isRunning = false
        }
    }
    private fun initializeDatePicker() {
        val calendar = Calendar.getInstance()
        val maxDate = Calendar.getInstance()
        maxDate.set(2006, 0, 1) // January 1, 2006

        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )

        // Set max date
        datePicker.maxDate = maxDate.timeInMillis
    }

    private fun goToStep(step: Int) {
        if (!validateForm()) {
            showToast("Please fill in all the required fields.")
            return
        }
        // Hide all steps
        mViewDataBinding.personalDetails.visibility = View.GONE
        mViewDataBinding.ConfirmPersonalty.visibility = View.GONE
        mViewDataBinding.ContactAndJob.visibility = View.GONE
        mViewDataBinding.Attachments.visibility = View.GONE
        mViewDataBinding.CreditCard.visibility = View.GONE

        when (step) {
            0 -> {
                mViewDataBinding.personalDetails.visibility = View.VISIBLE
                mViewDataBinding.button.text = "التالي"
                mViewDataBinding.previousButton.visibility = View.GONE
            }
            1 -> {
                mViewDataBinding.ConfirmPersonalty.visibility = View.VISIBLE
                mViewDataBinding.button.text = "التالي"
                mViewDataBinding.previousButton.visibility = View.VISIBLE
            }
            2 -> {
                mViewDataBinding.ContactAndJob.visibility = View.VISIBLE
                mViewDataBinding.button.text = "التالي"
                mViewDataBinding.previousButton.visibility = View.VISIBLE
            }
            3 -> {
                mViewDataBinding.Attachments.visibility = View.VISIBLE
                mViewDataBinding.button.text = "التالي"
                mViewDataBinding.previousButton.visibility = View.VISIBLE
            }
            4 -> {
                mViewDataBinding.CreditCard.visibility = View.VISIBLE
                mViewDataBinding.button.text = "إدخال"
            }
        }

        position = step
        mViewDataBinding.stepView.go(position, true)

        // handleRadioButtons()


    }
    private fun setupDatePicker() {
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            val formattedMonth = month + 1 // Adjust month since it's zero-based
            val msg = "You Selected: $day/$formattedMonth/$year"
            Toast.makeText(this@KycFragment.requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }


    //Validation
    private fun validateForm(): Boolean {
        when (position) {
            0 -> {
                // Validate the form on the first step (personal details) if needed
                val firstName = mViewDataBinding.etFirstName.text.toString().trim()
                val fatherName = mViewDataBinding.etFirstNamde.text.toString().trim()
                val lastName = mViewDataBinding.etFirstNamde2.text.toString().trim()
                val motherName = mViewDataBinding.moss.text.toString().trim()
                val motherLastName = mViewDataBinding.mos22.text.toString().trim()
                val birthPlace = mViewDataBinding.mos33.text.toString().trim()



                if (firstName.isEmpty()) {
                    mViewDataBinding.etFirstName.error = "Please enter your first name"
                    return false
                } else {
                    mViewDataBinding.etFirstName.error = null
                }

                if (fatherName.isEmpty()) {
                    mViewDataBinding.etFirstNamde.error = "Please enter your father's name"
                    return false
                } else {
                    mViewDataBinding.etFirstNamde.error = null
                }

                if (lastName.isEmpty()) {
                    mViewDataBinding.etFirstNamde2.error = "Please enter your last name"
                    return false
                } else {
                    mViewDataBinding.etFirstNamde2.error = null
                }

                if (motherName.isEmpty()) {
                    mViewDataBinding.moss.error = "Please enter your mother's name"
                    return false
                } else {
                    mViewDataBinding.moss.error = null
                }

                if (motherLastName.isEmpty()) {
                    mViewDataBinding.mos22.error = "Please enter your mother Last Name"
                    return false
                } else {
                    mViewDataBinding.mos22.error = null
                }

                if (birthPlace.isEmpty()) {
                    mViewDataBinding.mos33.error = "Please enter your Birth Place"
                    return false
                } else {
                    mViewDataBinding.mos33.error = null
                }
                // Validate the gender spinner
                val selectedGender = mViewDataBinding.genderSpinner.selectedItem.toString()
                if (selectedGender == "اختر الجنس") {
                    showToast("Please select your gender")
                    return false
                }

                val selectedNationality = mViewDataBinding.gender2Spinner.selectedItem.toString()
                if (selectedNationality == "اختر الجنسية") {
                    showToast("Please select your nationality")
                    return false
                }

            }
            1 -> {
                // Validate the form on the second step (Confirm_Personalty) if needed
                val nationalNumber = mViewDataBinding.nationalNumberr.text.toString().trim()
                val nationalPlace = mViewDataBinding.nationalPlacec.text.toString().trim()
                val kayed = mViewDataBinding.kayed.text.toString().trim()


                // Validate the Spinner
                val selectedTypeId = mViewDataBinding.typeIdSpinner.selectedItem.toString()
                if (selectedTypeId == "اختر نوع الوثيقة") {
                    showToast("Please select a نوع الوثيقة option")
                    return false
                }

                if (nationalNumber.isEmpty()) {
                    mViewDataBinding.nationalNumberr.error = "Please enter the national number"
                    return false
                } else {
                    mViewDataBinding.nationalNumberr.error = null
                }

                if (nationalPlace.isEmpty()) {
                    mViewDataBinding.nationalPlacec.error = "Please enter the place of issuance"
                    return false
                } else {
                    mViewDataBinding.nationalPlacec.error = null
                }

                if (kayed.isEmpty()) {
                    mViewDataBinding.kayed.error = "Please enter the kayed"
                    return false
                } else {
                    mViewDataBinding.kayed.error = null
                }


            }

            2 -> {
                // Validate the form on the third step (ContactAndJob) if needed
                val address = mViewDataBinding.addrr.text.toString().trim()
                val mobileNumber = mViewDataBinding.mobnumm.text.toString().trim()
                val job = mViewDataBinding.job.text.toString().trim()

                if (address.isEmpty()) {
                    mViewDataBinding.addrr.error = "Please enter your address"
                    return false
                } else {
                    mViewDataBinding.addrr.error = null
                }

                if (mobileNumber.isEmpty()) {
                    mViewDataBinding.mobnumm.error = "Please enter your mobile number"
                    return false
                } else {
                    mViewDataBinding.mobnumm.error = null
                }

                if (job.isEmpty()) {
                    mViewDataBinding.job.error = "Please enter your job"
                    return false
                } else {
                    mViewDataBinding.job.error = null
                }

                // Spinner validations
                val selectedGovernorate = mViewDataBinding.governorateSpinner.selectedItem.toString()
                if (selectedGovernorate == "اختر المحافظة") {
                    (mViewDataBinding.governorateSpinner.parent.parent as? TextInputLayout)?.error = "Please select a governorate"
                    return false
                } else {
                    (mViewDataBinding.governorateSpinner.parent.parent as? TextInputLayout)?.error = null
                }

                val selectedUniversity = mViewDataBinding.universitySpinner.selectedItem.toString()
                if (selectedUniversity == "اختر الجامعة") {
                    (mViewDataBinding.universitySpinner.parent.parent as? TextInputLayout)?.error = "Please select a university"
                    return false
                } else {
                    (mViewDataBinding.universitySpinner.parent.parent as? TextInputLayout)?.error = null
                }

                val selectedCollege = mViewDataBinding.collegeSpinner.selectedItem.toString()
                if (selectedCollege == "اختر الكلية") {
                    (mViewDataBinding.collegeSpinner.parent.parent as? TextInputLayout)?.error = "Please select a college"
                    return false
                } else {
                    (mViewDataBinding.collegeSpinner.parent.parent as? TextInputLayout)?.error = null
                }

            }
            3 -> {
                // Validate the form on the fourth step (Attachments) if needed
                val selectedQanon = mViewDataBinding.qanonSpinner.selectedItem.toString()
                if (selectedQanon == "اختر نوع الوثيقة") {
                    // No option selected for قانون الامتثال الضريبي للحسابات الخارجية (FATCA)
                    showToast("Please select a قانون الامتثال الضريبي للحسابات الخارجية (FATCA) option")
                    return false
                }



                // Validate image uploads
                val image1 = mViewDataBinding.imageUploadView1.drawable != null
                val image2 = mViewDataBinding.imageUploadView2.drawable != null
                val image3 = mViewDataBinding.imageUploadView3.drawable != null

                if (!image1 || !image2 || !image3) {
                    showToast("Please upload all required images")
                    return false
                }
            }

            4 -> {
                // Validate the form on the fifth step (CreditCard) if needed
                val isAgreementChecked = mViewDataBinding.agreementCheckbox.isChecked

                if (!isAgreementChecked) {
                    showToast("Please agree to issue the card")
                    return false
                }



                val firstNameEn = mViewDataBinding.fnameenn.text.toString().trim()
                if (firstNameEn.isEmpty()) {
                    mViewDataBinding.fnameenn.error = "Please enter the first name in English"
                    return false
                }

                val fatherNameEn = mViewDataBinding.fathernameenN.text.toString().trim()
                if (fatherNameEn.isEmpty()) {
                    mViewDataBinding.fathernameenN.error = "Please enter the father's name in English"
                    return false
                }

                val lastNameEn = mViewDataBinding.lastNameenn.text.toString().trim()
                if (lastNameEn.isEmpty()) {
                    mViewDataBinding.lastNameenn.error = "Please enter the last name in English"
                    return false
                }

                val selectedBranch = mViewDataBinding.branchSpinnerId.selectedItem.toString()
                if (selectedBranch == "اختر الفرع الذي ترغب بفتح الحساب فيه") {
                    showToast("Please select a branch")
                    return false
                }

                // Validate the Agreement Checkbox 2
                val isAgreementCheckbox2Checked = mViewDataBinding.agreementCheckbox2.isChecked
                if (!isAgreementCheckbox2Checked) {
                    showToast("Please check the second agreement checkbox")
                    return false
                }

                // Validate the CAPTCHA input
                val captchaInput = mViewDataBinding.captchaInput.text.toString().trim()
                val captchaText = mViewDataBinding.captchaTextView.text.toString()
                if (captchaInput != captchaText) {
                    showToast("Incorrect CAPTCHA, please try again")
                    return false
                }
            }




        }

        //  all validations pass
        return true
    }
    private fun setupCaptcha(){

        fun generateCaptcha(): String {
            val random = Random()
            val number1 = random.nextInt(10) // Generate a random number between 0 and 9
            val number2 = random.nextInt(10)
            val number3 = random.nextInt(10)

            // Generate a CAPTCHA string with the three random numbers
            return "$number1    $number2    $number3"
        }

        // Generate a CAPTCHA and set it in the TextView
        val captchaTextView = mViewDataBinding.captchaTextView
        val captchaInput = mViewDataBinding.captchaInput
        val refreshButton = mViewDataBinding.refreshButton

        fun generateAndSetCaptcha() {
            val captcha = generateCaptcha()
            captchaTextView.text = captcha
        }
        generateAndSetCaptcha()
        // Set an OnClickListener for the Refresh button to generate and set a new CAPTCHA
        refreshButton.setOnClickListener {
            generateAndSetCaptcha()
        }
        val captcha = generateCaptcha()
        captchaTextView.text = captcha
        // Now you can check the user's input against the generated CAPTCHA
        val userEnteredCaptcha = captchaInput.text.toString()
        if (userEnteredCaptcha == captcha) {
            // CAPTCHA is correct, you can proceed with your logic
        } else {
            // CAPTCHA is incorrect, handle the error
        }
        // Generate a CAPTCHA and set it in the TextView

    }
    private fun cardInfo(){

        val agreementCheckbox = mViewDataBinding.agreementCheckbox
        val agreementCheckbox1 = mViewDataBinding.agreementCheckbox1
        val stp5areaLayout = mViewDataBinding.stp5area
        val countrySspinner = mViewDataBinding.countrySpinner
        val cardIssuanceFeeLayout = mViewDataBinding.cardIssuanceFeeLayout
        val cardIssuanceFeeLayout2 =  mViewDataBinding.cardIssuanceFeeLayout2

        agreementCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                stp5areaLayout.visibility = View.VISIBLE
                agreementCheckbox1.visibility= View.VISIBLE
                countrySspinner.visibility = View.VISIBLE
                cardIssuanceFeeLayout.visibility = View.VISIBLE
                cardIssuanceFeeLayout2.visibility = View.VISIBLE
            } else {
                stp5areaLayout.visibility = View.GONE
                countrySspinner.visibility = View.GONE
                agreementCheckbox1.visibility= View.GONE
                cardIssuanceFeeLayout.visibility = View.GONE
                cardIssuanceFeeLayout2.visibility = View.GONE
            }
        }

        studentStatusInput = mViewDataBinding.studentStatusInput
        governorateSpinner = mViewDataBinding.governorateSpinner
        universitySpinner = mViewDataBinding.universitySpinner
        collegeSpinner = mViewDataBinding.collegeSpinner
        imageUploadView4 = mViewDataBinding.imageUploadView4
        jobbh = mViewDataBinding.job
        // Initialize position and set the initial step
    }


    // All Spinner
    private  fun  setupSpinners(){

        val genderSpinner = mViewDataBinding.genderSpinner
        val typeidSpinner = mViewDataBinding.typeIdSpinner
        val qanonSpinner = mViewDataBinding.qanonSpinner
        val countrySpinner = mViewDataBinding.countrySpinner
        val branchSpinner = mViewDataBinding.branchSpinnerId
        val genderSpinner2 = mViewDataBinding.gender2Spinner
        val governoratespinner = mViewDataBinding.governorateSpinner
        val universityspinner = mViewDataBinding.universitySpinner
        val collegespinner = mViewDataBinding.collegeSpinner


        val branchOptions = arrayOf("اختر الفرع الذي ترغب بفتح الحساب فيه","دمشق - الفرع الرئيسي : السبع بحرات","دمشق - فرع الدامسكينو : كفرسوسة دامسكينو مول", "حلب - فرع الفرقان : الفرقان - شارع اكسبريس")
        val countryOptions = arrayOf("اختر المحافظة","دمشق","حمص", "ريف دمشق")
        val qanonOptions = arrayOf("أنا لست مواطناً أمريكياً أو مقيم في الولايات المتحدة.","أنا مواطن أمريكي أو مقيم في الولايات المتحدة ")
        val typeidOptions = arrayOf("اختر نوع الوثيقة","بطاقة شخصية", "هوية عسكرية")
        val genderOptions = arrayOf("اختر الجنس","ذكر", "أنثى")
        val nationalityOptions = arrayOf("اختر الجنسية","سوري", "فلسطيني","غير ذلك")

        val governoratespinnerOptions = arrayOf("اختر المحافظة","دمشق", "ريف دمشق","حمص")
        val universityspinnerOptions = arrayOf("اختر الجامعة","جامعة دمشق", "الجامعة العربية الدولية (AIU)","جامعة الشام الخاصة")
        val collegespinnerOptions = arrayOf("اختر الكلية","هندسة المعلوماتية", "طب أسنان","طب بشري")



        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalityOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner2.adapter = adapter2

        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeidOptions)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeidSpinner.adapter = adapter3

        val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, qanonOptions)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qanonSpinner.adapter = adapter4

        val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countryOptions)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter5

        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, branchOptions)
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        branchSpinner.adapter = adapter6

        val adapter7 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, governoratespinnerOptions)
        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        governoratespinner.adapter = adapter7

        val adapter8 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, universityspinnerOptions)
        adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        universityspinner.adapter = adapter8

        val adapter9 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, collegespinnerOptions)
        adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        collegespinner.adapter = adapter9


    }

    //MODAL & Toast
//    private  fun setupQRScannerDialog(){
//        val tvPersonalInfo = mViewDataBinding.tvPersonalInfo
////        val text = "البيانات الشخصية-باللغة العربية [اضغط لتعبئة البيانات تلقائيا]"
//        val text = context?.getString(R.string.personal_information_kyc)
//        val spannableStringseconde = SpannableString(text)
//        // Find the start and end index of the clickable portion "[مسح باركود الهوية الشخصية]"
//        val startIndex2 = context?.getString(R.string.personal_info_kyc_qr)
//            ?.let { text?.indexOf(it) }
//        val endIndex = context?.getString(R.string.personal_info_kyc_qr)
//            ?.let { startIndex2?.plus(it.length) }
//        // Set a ClickableSpan to handle the click event
//        if (startIndex2 != null) {
//            if (endIndex != null) {
//                spannableStringseconde.setSpan(object : ClickableSpan() {
//                    override fun onClick(widget: View) {
//                        // Handle the click event (e.g., show a modal dialog)
//                        showQRDialog()
//                    }
//
//                    override fun updateDrawState(ds: TextPaint) {
//                        super.updateDrawState(ds)
//                        // Customize the appearance of the clickable text if needed
//                        ds.isUnderlineText = false
//                        ds.color = ContextCompat.getColor(requireContext(), R.color.red)
//                    }
//                }, startIndex2, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//        }
//
//        // Make the clickable portion appear as a link
//        tvPersonalInfo.text = spannableStringseconde
//        tvPersonalInfo.movementMethod = LinkMovementMethod.getInstance()
//        tvPersonalInfo.highlightColor = Color.TRANSPARENT  // To remove the link highlight color
//
//    }


     private fun setupQRScannerDialog() {
         val tvPersonalInfo = mViewDataBinding.tvPersonalInfo
         val text = context?.getString(R.string.personal_information_kyc)

         // Check if the clickable portion string is present in the text
         val clickableText = context?.getString(R.string.personal_info_kyc_qr)
         val startIndex = text?.indexOf(clickableText ?: "")

         if (startIndex != -1 && clickableText != null) {
             val endIndex = startIndex?.plus(clickableText.length)

             val spannableString = SpannableString(text)
             startIndex?.let {
                 if (endIndex != null) {
                     spannableString.setSpan(object : ClickableSpan() {
                         override fun onClick(widget: View) {
                             // Handle the click event (e.g., show a modal dialog)
                             showQRDialog()
                         }

                         override fun updateDrawState(ds: TextPaint) {
                             super.updateDrawState(ds)
                             // Customize the appearance of the clickable text if needed
                             ds.isUnderlineText = false
                             ds.color = ContextCompat.getColor(requireContext(), R.color.red)
                         }
                     }, it, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                 }
             }

             // Make the clickable portion appear as a link
             tvPersonalInfo.text = spannableString
             tvPersonalInfo.movementMethod = LinkMovementMethod.getInstance()
             tvPersonalInfo.highlightColor = Color.TRANSPARENT // To remove the link highlight color
         } else {
             // Handle the case where the clickable text is not found in the main text
             tvPersonalInfo.text = text
         }
     }


     private fun showQRDialog() {
         try {
             if (!activity?.isFinishing!!) {
                 val builder = AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                 builder.setTitle("قم باختيار طريقة لمسح باركود الهوية الشخصية:")
                 val view = layoutInflater.inflate(R.layout.qr_modal, null)
                 scanButton = view.findViewById(R.id.scanButton)
                 browseButton = view.findViewById(R.id.browseButton)
                 scanButton = view.findViewById(R.id.scanButton)
                 scanButton.setOnClickListener {
                     MyApplication.skipQuit = true
                     startScannerActivity()
                 }
                 browseButton.setOnClickListener {
                     MyApplication.skipQuit = true
                     openImagePickerForResult()
                 }
                 builder.setView(view)

                 builder.setNegativeButton("موافق"){ dialog, which ->
                     // Handle negative button click
                 }
                 builder.setPositiveButton("إلغاء") { dialog, which ->
                     // Handle positive button click
                 }

                 val dialog = builder.create()
                 dialog.window?.decorView?.layoutDirection = View.LAYOUT_DIRECTION_RTL
                 dialog.show()
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }




    private fun startScannerActivity() {
        val scannerIntent = Intent(requireContext(), ScannerActivity::class.java)
        startActivityForResult(scannerIntent, SCANNER_REQUEST_CODE)
    }




    private fun updateUIWithDecodedResult(decodedResult: Array<String>) {

        // Update TextInputEditText fields with decoded information
        mViewDataBinding.etFirstName.setText(decodedResult.getOrNull(0) ?: "")
        mViewDataBinding.etFirstNamde.setText(
            decodedResult.getOrNull(2) ?: "")
        mViewDataBinding.etFirstNamde2.setText(
            decodedResult.getOrNull(1) ?: "")
        mViewDataBinding.moss.setText(
            decodedResult.getOrNull(3)?.split(" ")?.getOrNull(0) ?: "")
        mViewDataBinding.mos22.setText(
            decodedResult.getOrNull(3)?.split(" ")?.getOrNull(1) ?: "")

        val mos33Text = decodedResult?.getOrNull(4) ?: ""
        val mos33Words = mos33Text.split(" ")
        mViewDataBinding.mos33.setText(mos33Words.getOrNull(0) ?: "")
        val datePicker = mViewDataBinding.datePicker2
        if (mos33Words.size > 1) {
            // Show DatePicker if the second word is present
            mViewDataBinding.datePicker2.visibility = View.VISIBLE
            val dates = mos33Words.get(1).split("-");
            // Extract day, month, and year from the string
            val day = dates.getOrNull(0)?.toIntOrNull() ?: 1
            val month = dates.getOrNull(1)?.toIntOrNull() ?: 1
            val year = dates.getOrNull(2)?.toIntOrNull() ?: 2023 // You need to set a default value

            // Update the DatePicker with the extracted values
            datePicker.updateDate(year, month - 1, day)
        } else {
            // Hide DatePicker otherwise
            mViewDataBinding.datePicker2.visibility = View.GONE
        }
        mViewDataBinding.nationalNumberr.setText(
            decodedResult.getOrNull(5) ?: "")
    }


     override fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Upload Images Step 4

    private fun openImagePicker(imageViewTag: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        currentImageViewTag = imageViewTag
        startActivityForResult(intent, PICK_QR_REQUEST)

    }
    private fun setupImageUpload(){
        imageUploadView1 = mViewDataBinding.imageUploadView1
        imageUploadView1.setTag(R.id.image_upload_view_tag, 1)
        imageViewList.add(Pair(imageUploadView1, 1))

        imageUploadView2 = mViewDataBinding.imageUploadView2
        imageUploadView2.setTag(R.id.image_upload_view_tag, 2)
        imageViewList.add(Pair(imageUploadView2, 2))

        imageUploadView3 = mViewDataBinding.imageUploadView3
        imageUploadView3.setTag(R.id.image_upload_view_tag, 3)
        imageViewList.add(Pair(imageUploadView3, 3))

        // Set click listeners for ImageViews
        for ((imageView, tag) in imageViewList) {
            imageView.setOnClickListener {
                openImagePicker(tag)
            }
        }
    }


    // For QR Read Barcode Image
    private fun openImagePickerForResult() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    try {
                        val imageBitmap =
                            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImage)
                        val decodedBarcode = decodeBarcodeFromImage(imageBitmap)
                        if (decodedBarcode != null) {
                            // Successfully decoded, update TextInputEditText fields with the decoded information
                            val decodedString = String(
                                decodedBarcode.toByteArray(Charset.forName("ISO-8859-1")),
                                Charset.forName("Cp1256")
                            )
                            // Split the decodedString using the '#' character as a delimiter
                            val parts = decodedString.split("#".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            // Update TextInputEditText fields with decoded information
                            mViewDataBinding.etFirstName.setText(parts.getOrNull(0) ?: "")
                            mViewDataBinding.etFirstNamde.setText(parts.getOrNull(2) ?: "")
                            mViewDataBinding.etFirstNamde2.setText(parts.getOrNull(1) ?: "")
                            mViewDataBinding.moss.setText(parts.getOrNull(3)?.split(" ")?.getOrNull(0) ?: "")
                            mViewDataBinding.mos22.setText(parts.getOrNull(3)?.split(" ")?.getOrNull(1) ?: "")

                            val mos33Text = parts?.getOrNull(4) ?: ""
                            val mos33Words = mos33Text.split(" ")
                            mViewDataBinding.mos33.setText(mos33Words.getOrNull(0) ?: "")
                            val datePicker = mViewDataBinding.datePicker2
                            if (mos33Words.size > 1) {
                                // Show DatePicker if the second word is present
                                datePicker.visibility = View.VISIBLE
                                val dates = mos33Words.get(1).split("-");
                                // Extract day, month, and year from the string
                                val day = dates.getOrNull(0)?.toIntOrNull() ?: 1
                                val month = dates.getOrNull(1)?.toIntOrNull() ?: 1
                                val year = dates.getOrNull(2)?.toIntOrNull() ?: 2023 // You need to set a default value

                                // Update the DatePicker with the extracted values
                                datePicker.updateDate(year, month - 1, day)
                            } else {
                                // Hide DatePicker otherwise
                                datePicker.visibility = View.GONE
                            }
                            mViewDataBinding.nationalNumberr.setText(parts.getOrNull(5) ?: "")

                        } else {
                            // Handle unsuccessful decoding
                            Toast.makeText(
                                requireContext(),
                                "Barcode not found or could not be decoded.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle IOException
                        Toast.makeText(
                            requireContext(),
                            "Failed to open the selected image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            PICK_QR_REQUEST -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    data.data?.let { selectedImageUri ->
                        val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
                        val selectedImage = BitmapFactory.decodeStream(inputStream)

                        // Find the ImageView with the corresponding tag and set the image
                        for ((imageView, tag) in imageViewList) {
                            if (currentImageViewTag == tag) {
                                imageView.setImageBitmap(selectedImage)
                                break
                            }
                        }
                    }
                }
            }

            SCANNER_REQUEST_CODE ->{

                val decodedResult = data?.getStringArrayExtra("decodedResult")
                if (decodedResult != null) {
                    // Update your UI with the decoded result
                    updateUIWithDecodedResult(decodedResult)
                }
            }



        }
    }
    private fun decodeBarcodeFromImage(imageBitmap: Bitmap): String? {
        return try {
            val width = imageBitmap.width
            val height = imageBitmap.height
            val pixels = IntArray(width * height)
            imageBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val hints: MutableMap<DecodeHintType, Any?> = EnumMap(
                DecodeHintType::class.java
            )
            val formats: MutableList<BarcodeFormat> = ArrayList()
            formats.add(BarcodeFormat.PDF_417) // Add PDF417 format
            hints[DecodeHintType.POSSIBLE_FORMATS] = formats
            val result = reader.decode(binaryBitmap, hints)
            result.text
        } catch (e: NotFoundException) {
            e.printStackTrace()
            null // Handle decoding errors
        }
    }
    override fun handleResult(rawResult: com.google.zxing.Result?) {

        val encodedString = rawResult?.text

        // Decode the encoded string using ISO-8859-1 encoding (Cp1256)
        val isoBytes = encodedString?.toByteArray(StandardCharsets.ISO_8859_1)
        var decodedString: String? = null
        try {
            decodedString = isoBytes?.let { String(it, Charset.forName("Cp1256")) }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        // Create an Intent to start the ResultActivity
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra("decodedString", decodedString) // Pass the decodedString as an extra

        // Start the new activity
        startActivity(intent)

        // Stop the camera after scanning
        zxingScannerView.stopCamera()
    }

    // Change account after edit
    private fun handleRadioButtons() {
        if (mViewDataBinding.btnFinancing.isChecked) {
//             Show views for financing
            governorateSpinner.visibility = View.VISIBLE
            universitySpinner.visibility = View.VISIBLE
            collegeSpinner.visibility = View.VISIBLE
            studentStatusInput.visibility = View.VISIBLE
            imageUploadView4.visibility= View.VISIBLE
            jobbh.visibility= View.GONE
        } else if (mViewDataBinding.btnProfits.isChecked) {
            // Hide views for profits
            governorateSpinner.visibility = View.GONE
            universitySpinner.visibility = View.GONE
            collegeSpinner.visibility = View.GONE
            studentStatusInput.visibility = View.GONE
            imageUploadView4.visibility= View.GONE
            jobbh.visibility= View.VISIBLE
        }
        if ( position==3 || position==4 )  {
            goToStep(2)
        }
    }

    // Conditions
    private fun setupAgreementCheckbox(){
        val checkBox = mViewDataBinding.agreementCheckbox2
        val spannableString = SpannableString("أوافق على الشروط والأحكام الخاصة بفتح الحساب لدى بنك البركة")
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
                val url = "https://albaraka.com.sy/KYC/conditions"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
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




    override fun createViewModel() {
        mViewModel = ViewModelProvider(mActivity).get(KycViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }



    override fun setViewModel(): Class<KycViewModel> {
        return KycViewModel::class.java
    }

    override fun setUpView() {

        // Set up QR scanner dialog
        setupQRScannerDialog()

        // Set up spinners
        setupSpinners()

        // Set up image upload
        setupImageUpload()

        // Set up agreement checkbox
        setupAgreementCheckbox()

        // Set up CAPTCHA
        setupCaptcha()

        // Set up CARD INFO
        cardInfo()

        // Set up step view
        setupStepView()
        datePicker =mViewDataBinding.datePicker2;
        setupDatePicker()
//        nationalNumberEditText = (view?.findViewById(R.id.national_numberr) ?: Handler(Looper.getMainLooper()).postDelayed({
//            applyMasks()
//            initializeDatePicker()
//        }, 1000)) as TextInputEditText

    }

    override fun fetchData() {

    }

}