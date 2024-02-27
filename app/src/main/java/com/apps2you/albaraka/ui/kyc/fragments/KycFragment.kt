package com.apps2you.albaraka.ui.kyc.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.MyApplication
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentKycBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.kyc.CompressImageTask
import com.apps2you.albaraka.viewmodels.KycViewModel
import com.bumptech.glide.Glide
import com.chaos.view.PinView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.Constants
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.EnumMap
import java.util.Random
import java.util.regex.Pattern
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.util.Locale




class KycFragment  : BaseFragment<FragmentKycBinding, KycViewModel>() , ZXingScannerView.ResultHandler  {

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
    private lateinit var imageUploadView5: ImageView
    private lateinit var imageUploadView4: LinearLayout
    private lateinit var jobbh: TextInputEditText
    private var currentImageViewTag: Int = 0
    private val imageViewList = mutableListOf<Pair<ImageView, Int>>()
    private lateinit var universitySpinner: Spinner
    private lateinit var collegeSpinner: Spinner
    private lateinit var zxingScannerView: ZXingScannerView
    private lateinit var scanButton: Button
    private lateinit var gif: ImageView
    private lateinit var browseButton: Button
    private lateinit var datePicker : DatePicker
    var responseWaiting: Boolean = false
    private val CAPTURE_IMAGE_REQUEST = 1234 // You can use any positive integer
    private var lastFetchedUniversityData: JSONArray? = null
    var mobnumEditText: TextInputEditText? = null
    private val CAMERA_PERMISSION_CODE = 1001

    private fun setupStepView(){


    mViewDataBinding.stepView.done(false)
        mViewDataBinding.button.setOnClickListener {
//            val intent = Intent(requireContext(), OtpScreen::class.java)
            when (position) {
                0 -> goToStep(1)
                1 -> goToStep(2)
                2 -> goToStep(3)
                3 -> goToStep(4)
                4 -> {
                    val selectedBranch = mViewDataBinding.branchSpinnerId.selectedItem.toString()
                    if (selectedBranch == "اختر الفرع الذي ترغب بفتح الحساب فيه") {
                        showToast("يرجى تحديد الفرع")

                    }

                    // Validate the Agreement Checkbox 2
                    val isAgreementCheckbox2Checked = mViewDataBinding.agreementCheckbox2.isChecked
                    if (!isAgreementCheckbox2Checked) {
                        showToast("يرجى التحقق من مربع الاقتراح الثاني")

                    }
                   // setupCaptcha()
                    val captchaTextView = mViewDataBinding.captchaTextView.text.toString()
                    val captchaInput:String = mViewDataBinding.captchaInput.text.toString()
                    if(captchaTextView.reversed().replace("\\s".toRegex(),"") == captchaInput) {
                        SendOtpReq()
                    }
                    else{
                        showToast("الرقم المدخل غير مطابق حاول مرة اخرى")
                    }

                }

                else -> {
                    position = 0
                    mViewDataBinding.stepView.done(true)
                    goToStep(position)
                }
            }
        }

        mViewDataBinding.previousButton.setOnClickListener {
            when (position) {
                0 -> {

                    // No previous step on the first screen
                    // You can handle this as needed (e.g., go back to a previous activity or fragment)
                }
                1 -> goToStep(0,false)
                2 -> goToStep(1,false)
                3 -> goToStep(2,false)
                else -> goToStep(3,false)
            }
        }

        mViewDataBinding.radio.setOnCheckedChangeListener { _, _ ->
            handleRadioButtons()
        }

        mViewDataBinding.previousButton.visibility = if (position == 0) View.GONE else View.VISIBLE
    }


    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // Permission has already been granted, start the camera activity
            //startCameraActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the camera activity
                startCameraActivity()
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(requireContext(), "تم رفض إذن الكاميرا", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCameraActivity() {
        // Start the camera activity here
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (context?.let { cameraIntent.resolveActivity(it.packageManager) } != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
        } else {
            showToast("يوجد خطأ بحزمة تطبيق الكاميرا")
        }
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

    private fun goToStep(step: Int,checkValidate:Boolean = true) {
        if(checkValidate)
            if (!validateForm()) {
              //  showToast("يرجى ملء جميع الحقول المطلوبة.")
                return
            }
        // Hide all steps
        mViewDataBinding.personalDetails.visibility = View.GONE
        mViewDataBinding.ConfirmPersonalty.visibility = View.GONE
        mViewDataBinding.ContactAndJob.visibility = View.GONE
        mViewDataBinding.Attachments.visibility = View.GONE
        mViewDataBinding.CreditCard.visibility = View.GONE
        if (step == 0) {
            mViewDataBinding.previousButton.visibility = View.GONE
        } else {
            mViewDataBinding.previousButton.visibility = View.VISIBLE
        }
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
            val msg = " تمت قراءة الباركود: $day/$formattedMonth/$year"
//            Toast.makeText(this@KycFragment.requireContext(), msg, Toast.LENGTH_SHORT).show()
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



                // Validate the Agreement Checkbox 2
                val checkacc=mViewDataBinding.btnProfits.isChecked
                val checkacc2=mViewDataBinding.btnFinancing.isChecked


                if (!(checkacc || checkacc2) || (checkacc && checkacc2)) {
                    mViewDataBinding.btnProfits.isChecked = true;
                    handleRadioButtons();

                }

                if (firstName.isEmpty()) {
                    mViewDataBinding.etFirstName.error = "يرجى إدخال الاسم الأول"
                    return false
                } else {
                    mViewDataBinding.etFirstName.error = null
                }

                if (fatherName.isEmpty()) {
                    mViewDataBinding.etFirstNamde.error = "يرجى إدخال اسم الأب"
                    return false
                } else {
                    mViewDataBinding.etFirstNamde.error = null
                }

                if (lastName.isEmpty()) {
                    mViewDataBinding.etFirstNamde2.error = "يرجى إدخال الكنية"
                    return false
                } else {
                    mViewDataBinding.etFirstNamde2.error = null
                }

                if (motherName.isEmpty()) {
                    mViewDataBinding.moss.error = "يرجى إدخال اسم الأم"
                    return false
                } else {
                    mViewDataBinding.moss.error = null
                }

                if (motherLastName.isEmpty()) {
                    mViewDataBinding.mos22.error = "يرجى إدخال كنية الأم"
                    return false
                } else {
                    mViewDataBinding.mos22.error = null
                }

                if (birthPlace.isEmpty()) {
                    mViewDataBinding.mos33.error = "يرجى إدخال مكان الميلاد"
                    return false
                } else {
                    mViewDataBinding.mos33.error = null
                }

                // Validate the gender spinner
                val selectedGender = mViewDataBinding.genderSpinner.selectedItem.toString()
                if (selectedGender == "اختر الجنس") {
                    showToast("يرجى تحديد الجنس")
                    return false
                }

                val selectedNationality = mViewDataBinding.gender2Spinner.selectedItem.toString()
                if (selectedNationality == "اختر الجنسية") {
                    showToast("يرجى تحديد الجنسية")
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
                    showToast("يرجى تحديد خيار نوع الوثيقة")
                    return false
                }

                if (nationalNumber.isEmpty()) {
                    mViewDataBinding.nationalNumberr.error = "يرجى إدخال الرقم الوطني"
                    return false
                } else {
                    mViewDataBinding.nationalNumberr.error = null
                }

                if (nationalPlace.isEmpty()) {
                    mViewDataBinding.nationalPlacec.error = "يرجى إدخال مكان الإصدار(الأمانة)"
                    return false
                } else {
                    mViewDataBinding.nationalPlacec.error = null
                }

                if (kayed.isEmpty()) {
                    mViewDataBinding.kayed.error = "يرجى إدخال القيد"
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

                // Check if btn_financing is checked
                if (mViewDataBinding.btnFinancing.isChecked) {
                    // Spinner validations

                    val selectedUniversity = mViewDataBinding.universitySpinner.selectedItem.toString()
                    if (selectedUniversity == "اختر الجامعة") {
                        (mViewDataBinding.universitySpinner.parent.parent as? TextInputLayout)?.error = "يرجى تحديد الجامعة"
                        return false
                    } else {
                        (mViewDataBinding.universitySpinner.parent.parent as? TextInputLayout)?.error = null
                    }

                    val selectedCollege = mViewDataBinding.collegeSpinner.selectedItem.toString()
                    if (selectedCollege == "اختر الكلية") {
                        (mViewDataBinding.collegeSpinner.parent.parent as? TextInputLayout)?.error = "يرجى تحديد الكلية"
                        return false
                    } else {
                        (mViewDataBinding.collegeSpinner.parent.parent as? TextInputLayout)?.error = null
                    }

                    if (address.isEmpty()) {
                        mViewDataBinding.addrr.error = "يرجى إدخال عنوانك"
                        return false
                    } else {
                        mViewDataBinding.addrr.error = null
                    }

                    if (mobileNumber.isEmpty()) {
                        mViewDataBinding.mobnumm.error = "يرجى إدخال رقم هاتفك المحمول"
                        return false
                    } else {
                        mViewDataBinding.mobnumm.error = null
                    }
                }

                // Check if btn_profits is checked
                if (mViewDataBinding.btnProfits.isChecked) {
                    if (address.isEmpty()) {
                        mViewDataBinding.addrr.error = "يرجى إدخال عنوانك"
                        return false
                    } else {
                        mViewDataBinding.addrr.error = null
                    }

                    if (mobileNumber.isEmpty()) {
                        mViewDataBinding.mobnumm.error = "يرجى إدخال رقم هاتفك المحمول"
                        return false
                    } else {
                        mViewDataBinding.mobnumm.error = null
                    }

                    if (job.isEmpty()) {
                        mViewDataBinding.job.error = "يرجى إدخال مهنتك"
                        return false
                    } else {
                        mViewDataBinding.job.error = null
                    }
                }
            }
            3 -> {
                // Validate the form on the fourth step (Attachments) if needed
                val selectedQanon = mViewDataBinding.qanonSpinner.selectedItem.toString()
                if (selectedQanon == "اختر نوع الوثيقة") {
                    showToast("يرجى تحديد خيار قانون الامتثال الضريبي للحسابات الخارجية (FATCA)")
                    return false
                }

                // Validate image uploads based on the buttons' state
                if (mViewDataBinding.btnProfits.isChecked) {

                    if (getImageUriFromImageView(mViewDataBinding.imageUploadView1) == null ||
                        getImageUriFromImageView(mViewDataBinding.imageUploadView2) == null ||
                        getImageUriFromImageView(mViewDataBinding.imageUploadView3) == null) {
                        showToast("يرجى تحميل جميع الصور المطلوبة")
                        return false
                    }

                } else if (mViewDataBinding.btnFinancing.isChecked) {

                    if (getImageUriFromImageView(mViewDataBinding.imageUploadView1) == null ||
                        getImageUriFromImageView(mViewDataBinding.imageUploadView2) == null ||
                        getImageUriFromImageView(mViewDataBinding.imageUploadView3) == null||
                        getImageUriFromImageView(mViewDataBinding.imageUploadView5) == null) {
                        showToast("يرجى تحميل جميع الصور المطلوبة")
                        return false
                    }

                }
            }
            4 -> {
                // Validate the form on the fifth step (CreditCard) if needed
//                val isAgreementChecked = mViewDataBinding.agreementCheckbox.isChecked
//
//                if (!isAgreementChecked) {
//                    showToast("يرجى الموافقة على إصدار البطاقة")
//                    return false
//                }
//
//                val firstNameEn = mViewDataBinding.fnameenn.text.toString().trim()
//                if (firstNameEn.isEmpty()) {
//                    mViewDataBinding.fnameenn.error = "يرجى إدخال الاسم الأول بالإنجليزية"
//                    return false
//                }
//
//                val fatherNameEn = mViewDataBinding.fathernameenN.text.toString().trim()
//                if (fatherNameEn.isEmpty()) {
//                    mViewDataBinding.fathernameenN.error = "يرجى إدخال اسم والدك بالإنجليزية"
//                    return false
//                }
//
//                val lastNameEn = mViewDataBinding.lastNameenn.text.toString().trim()
//                if (lastNameEn.isEmpty()) {
//                    mViewDataBinding.lastNameenn.error = "يرجى إدخال اللقب بالإنجليزية"
//                    return false
//                }

                val selectedBranch = mViewDataBinding.branchSpinnerId.selectedItem.toString()
                if (selectedBranch == "اختر الفرع الذي ترغب بفتح الحساب فيه") {
                    showToast("يرجى تحديد الفرع")
                    return false
                }

                // Validate the Agreement Checkbox 2
                val isAgreementCheckbox2Checked = mViewDataBinding.agreementCheckbox2.isChecked
                if (!isAgreementCheckbox2Checked) {
                    showToast("يرجى التحقق من مربع الاقتراح الثاني")
                    return false
                }

                // Validate the CAPTCHA input
                val captchaInput = mViewDataBinding.captchaInput.text.toString().trim()
                val captchaText = mViewDataBinding.captchaTextView.text.toString()
                if (captchaInput != captchaText) {
                    showToast("الرمز المدخل غير صحيح، يرجى المحاولة مرة أخرى")
                    return false
                }
            }
        }

        //  all validations pass
        return true
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

    private fun cardInfo(){
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
//        val countrySpinner = mViewDataBinding.countrySpinner
        val branchSpinner = mViewDataBinding.branchSpinnerId
        val genderSpinner2 = mViewDataBinding.gender2Spinner
        //val governoratespinner = mViewDataBinding.governorateSpinner
        val universityspinner = mViewDataBinding.universitySpinner
        val collegespinner = mViewDataBinding.collegeSpinner


        val branchOptions = arrayOf( "اختر الفرع الذي ترغب بفتح الحساب فيه",
            "دمشق - فرع المزة : اوتوستراد المزة ( إياب ) - مقابل طلعة الإسكان",
            "دمشق - الفرع الرئيسي : السبع بحرات",
            "دمشق - فرع الميدان : غربي الميدان - مقابل شركة البريد السريع",
            "دمشق - فرع أبو رمانة : شارع الجلاء - مقابل مكتب البريد",
            "دمشق - فرع الدامسكينو : كفرسوسة دامسكينو مول",
            "دمشق - فرع شارع حلب : شارع حلب",
            "ريف دمشق - فرع يعفور : يعفور - البوابة الثامنة",
            "ريف دمشق - فرع أشرفية صحنايا : أشرفية صحنايا الشارع العام",
            "حلب - فرع الفرقان : الفرقان - شارع اكسبريس",
            "حلب - فرع الفيصل : شارع الفيصل - جوار القنصلية الفرنسية",
            "حماه - فرع القوتلي : شارع القوتلي",
            "حمص - فرع الدروبي : شارع عبد الحميد الدروبي",
            "اللاذقية - فرع اللاذقية : الكورنيش الغربي - منطقة الشيخ ضاهر - بناء برج سبيرو",
            "طرطوس - فرع طرطوس : شارع المينا",
            "صافيتا - مكتب صافيتا : شارع الكورنيش امتداد السرايا",
            "حماة - فرع صلاح الدين : شارع صلاح الدين")
        val countryOptions = arrayOf("اختر المحافظة","دمشق","حمص", "ريف دمشق")
        val qanonOptions = arrayOf("أنا لست مواطناً أمريكياً أو مقيم في الولايات المتحدة.","أنا مواطن أمريكي أو مقيم في الولايات المتحدة ")
        val typeidOptions = arrayOf("اختر نوع الوثيقة","بطاقة شخصية", "هوية عسكرية")
        val genderOptions = arrayOf("اختر الجنس","ذكر", "أنثى")
        val nationalityOptions = arrayOf("اختر الجنسية","سوري", "فلسطيني")
       // val governoratespinnerOptions = arrayOf("اختر المحافظة","دمشق", "ريف دمشق","حمص")
        val universityspinnerOptions = arrayOf("اختر الجامعة")
        val collegespinnerOptions = arrayOf("اختر الكلية")


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



        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, branchOptions)
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        branchSpinner.adapter = adapter6


        val adapter8 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, universityspinnerOptions)
        adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        universityspinner.adapter = adapter8

        val adapter9 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, collegespinnerOptions)
        adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        collegespinner.adapter = adapter9


    }





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




    private fun showOtpDialog() {
        var otp : String = "";
        try {
            if (!activity?.isFinishing!!) {
                val builder = AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                builder.setTitle("تم ارسال رمز التفعيل برسالة نصية:")
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
                         saveNewAccountRequest2(otp);

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



    private fun showQRDialog() {
         try {
             if (!activity?.isFinishing!!) {
                 val builder = AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                 builder.setTitle("قم باختيار طريقة لمسح باركود الهوية الشخصية:")
                 val view = layoutInflater.inflate(R.layout.qr_modal, null)
                 val gifImageView = view.findViewById<ImageView>(R.id.gifImageView)
                 scanButton = view.findViewById(R.id.scanButton)
                 browseButton = view.findViewById(R.id.browseButton)
                 Glide.with(this).asGif().load(R.drawable.modal1).into(gifImageView)
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

                 builder.setNegativeButton("إغلاق"){ dialog, which ->
                     // Handle negative button click
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



        val compoundNounParts = decodedResult.getOrNull(0)?.split(" ") ?: emptyList()
        val compoundNoun = buildString {
            for (part in compoundNounParts) {

                append("$part ")
            }
        }

        mViewDataBinding.etFirstName.setText(compoundNoun)

        val compoundNounParts2 = decodedResult.getOrNull(2)?.split(" ") ?: emptyList()
        val compoundNoun2 = buildString {
            for (part in compoundNounParts2) {
                append("$part ")
            }
        }
        mViewDataBinding.etFirstNamde.setText(compoundNoun2)



        val compoundNounParts3 = decodedResult.getOrNull(1)?.split(" ") ?: emptyList()
        val compoundNoun3 = buildString {
            for (part in compoundNounParts3) {
                append("$part ")
            }
        }
        mViewDataBinding.etFirstNamde2.setText(compoundNoun3)


        mViewDataBinding.moss.setText(decodedResult.getOrNull(3)?.split(" ")?.getOrNull(0) ?: "")
        mViewDataBinding.mos22.setText(decodedResult.getOrNull(3)?.split(" ")?.getOrNull(1) ?: "")

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
        currentImageViewTag = imageViewTag
        requestCameraPermission()
        val options = arrayOf("اختر من الاستوديو", "التقاط صورة")
        val builder = AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
        builder.setTitle("اختر الخيار")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Choose from gallery
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, PICK_QR_REQUEST)
                }
                1 -> {
                    // Capture from camera
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (context?.let { cameraIntent.resolveActivity(it.packageManager) } != null) {
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
                    } else {
                        showToast("لا يوجد تطبيق كاميرا مثبت على الجهاز.")
                    }
                }
            }
        }

        builder.setNegativeButton("إلغاء") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
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

        imageUploadView5 = mViewDataBinding.imageUploadView5
        imageUploadView5.setTag(R.id.image_upload_view_tag, 4)
        imageViewList.add(Pair(imageUploadView5, 4))

        val imageViews = listOf(imageUploadView1, imageUploadView2, imageUploadView3, imageUploadView5)
        setImageViewClickListeners(imageViews)

    }
    private fun setImageViewClickListeners(imageViews: List<ImageView>) {
        for ((index, imageView) in imageViews.withIndex()) {
            imageView.setOnClickListener {
                openImagePicker(index + 1)
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
                            updateUIWithDecodedResult(parts)
                            Toast.makeText(
                                requireContext(),
                                "تم قراءة الباركود بنجاح",
                                Toast.LENGTH_SHORT
                            ).show()


                        } else {
                            // Handle unsuccessful decoding
                            Toast.makeText(
                                requireContext(),
                                "لم يتم التعرف على الباركود لعدم استيفاء شروط دقة الصورة",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle IOException
                        Toast.makeText(
                            requireContext(),
                            "فشل في فتح الصورة المحددة.",
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
//                                val compressImageTask = CompressImageTask(createTempFile("temp", ".jpg"), 800, 800)
//                                compressImageTask.execute()
                                break
                            }
                        }
                    }
                }
            }

            CAPTURE_IMAGE_REQUEST -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    // Check if the captured image is available in the data
                    val imageBitmap = data?.extras?.get("data") as? Bitmap

                    // Check if the imageBitmap is not null
                    if (imageBitmap != null) {
                        // Find the ImageView with the corresponding tag and set the image
                        for ((imageView, tag) in imageViewList) {
                            if (currentImageViewTag == tag) {
                                imageView.setImageBitmap(imageBitmap)
//                                val compressImageTask = CompressImageTask(createTempFile("temp", ".jpg"), 800, 800)
//                                compressImageTask.execute()
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
//             Show views student
         //   governorateSpinner.visibility = View.VISIBLE
            universitySpinner.visibility = View.VISIBLE
            collegeSpinner.visibility = View.VISIBLE
           // studentStatusInput.visibility = View.VISIBLE
            imageUploadView4.visibility= View.VISIBLE
            mViewDataBinding.tvContactandeduInfo.visibility= View.VISIBLE
            mViewDataBinding.tvContactandjobInfo.visibility= View.GONE
            jobbh.visibility= View.GONE

        } else if (mViewDataBinding.btnProfits.isChecked) {
            // Hide views for job
          //  governorateSpinner.visibility = View.GONE
            universitySpinner.visibility = View.GONE
            collegeSpinner.visibility = View.GONE
           // studentStatusInput.visibility = View.GONE
            imageUploadView4.visibility= View.GONE
            mViewDataBinding.tvContactandeduInfo.visibility= View.GONE
            mViewDataBinding.tvContactandjobInfo.visibility= View.VISIBLE
            jobbh.visibility= View.VISIBLE

        }
        if ( position==3&&mViewDataBinding.btnFinancing.isChecked || position==4&&mViewDataBinding.btnFinancing.isChecked )  {
            goToStep(2,false)
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
        val urlString = "https://albaraka.com.sy/KYC/ApiController/universities"
        FetchUniversityDataTask().execute(urlString)
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





       mViewDataBinding.etFirstName.addTextChangedListener(textWatcher)
        mViewDataBinding.etFirstNamde.addTextChangedListener(textWatcher)
        mViewDataBinding.etFirstNamde2.addTextChangedListener(textWatcher)
        mViewDataBinding.moss.addTextChangedListener(textWatcher)
        mViewDataBinding.mos22.addTextChangedListener(textWatcher)
        mViewDataBinding.mos33.addTextChangedListener(textWatcher)
        mViewDataBinding.nationalPlacec.addTextChangedListener(textWatcher)
        mViewDataBinding.kayed.addTextChangedListener(textWatcher)
        mViewDataBinding.addrr.addTextChangedListener(textWatcher)
        mViewDataBinding.job.addTextChangedListener(textWatcher)



        val etFirstName = view?.findViewById<TextInputEditText>(R.id.et_first_name)
        val et_firstnamde = view?.findViewById<TextInputEditText>(R.id.et_first_namde)
        val et_firstnamde2 = view?.findViewById<TextInputEditText>(R.id.et_first_namde2)
        val moss = view?.findViewById<TextInputEditText>(R.id.moss)
        val mos22 = view?.findViewById<TextInputEditText>(R.id.mos22)
        val mos33 = view?.findViewById<TextInputEditText>(R.id.mos33)
        val national_placec = view?.findViewById<TextInputEditText>(R.id.national_placec)
        val kayed = view?.findViewById<TextInputEditText>(R.id.kayed)
        val addrr = view?.findViewById<TextInputEditText>(R.id.addrr)
        val job = view?.findViewById<TextInputEditText>(R.id.job)




        val englishInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!isEnglish(source[i].toString())) {
                    Toast.makeText(requireContext(), "يرجى الكتابة باللغة الإنجليزية", Toast.LENGTH_SHORT).show()
                    return@InputFilter ""
                }
            }
            null
        }

        val arabicInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!isArabic(source[i].toString())) {
                    Toast.makeText(requireContext(), "يرجى الكتابة باللغة العربية", Toast.LENGTH_SHORT).show()
                    return@InputFilter ""
                }
            }
            null
        }
        if (etFirstName != null) {
            etFirstName.filters = arrayOf(arabicInputFilter)
        }
        if (et_firstnamde != null) {
            et_firstnamde.filters = arrayOf(arabicInputFilter)
        }
        if (et_firstnamde2 != null) {
            et_firstnamde2.filters = arrayOf(arabicInputFilter)
        }
        if (moss != null) {
            moss.filters = arrayOf(arabicInputFilter)
        }
        if (mos22 != null) {
            mos22.filters = arrayOf(arabicInputFilter)
        }
        if (mos33 != null) {
            mos33.filters = arrayOf(arabicInputFilter)
        }
        if (national_placec != null) {
            national_placec.filters = arrayOf(arabicInputFilter)
        }
        if (kayed != null) {
            kayed.filters = arrayOf(arabicInputFilter)
        }
        if (addrr != null) {
            addrr.filters = arrayOf(arabicInputFilter)
        }
        if (job != null) {
            job.filters = arrayOf(arabicInputFilter)
        }
        //
//        if (fnameenn != null) {
//            fnameenn.filters = arrayOf(englishInputFilter)
//        }
//        if (fathernameen_n != null) {
//            fathernameen_n.filters = arrayOf(englishInputFilter)
//        }
//        if (last_nameenn != null) {
//            last_nameenn.filters = arrayOf(englishInputFilter)
//        }








    }







    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (isEnglish(s.toString())) {
                Toast.makeText(requireContext(), "يرجى الكتابة باللغة العربية", Toast.LENGTH_SHORT).show()
                // You can clear the input or handle it in another way
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed in this case
        }
    }
    fun isArabic(text: String): Boolean {
        val arabicPattern = Pattern.compile("[\\u0600-\\u06FF()+*/\\\\|0-9 ]+")
        return arabicPattern.matcher(text).matches()
    }


    //

    val englishTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isArabic(s.toString())) {
                Toast.makeText(requireContext(), "يرجى الكتابة باللغة الإنجليزية", Toast.LENGTH_SHORT).show()
                // You can clear the input or handle it in another way
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Not needed in this case
        }
    }

     fun isEnglish(text: String): Boolean {
        val englishPattern = Pattern.compile("[a-zA-Z0-9-()+*/\\\\\\\\| ]+")
        return englishPattern.matcher(text).matches()
    }

    override fun fetchData() {



    }

    private fun sendOtpAndHandleResponse(mobileNumber: String?, captchaChallenge: String?) {
        // UI logic before sending OTP

        // Launch a coroutine in the background
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Perform network operation here (replace this with your actual code)
                val response = sendOtp(mobileNumber, captchaChallenge)

                // Switch back to the main thread to update UI
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        handleServerResponse(response)
//                        saveNewAccountRequest()
                    } else {
                        showToast("حدث خطأ أثناء إرسال الرمز (OTP) حاول مرة اخرى.")
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions here
                withContext(Dispatchers.Main) {
                    showToast("فشل طلب الشبكة حاول مرة اخرى.")
                }
            }
        }}

    private fun sendOtp(vararg params: String?): String? {
        val mobileNumber = params[0]
        val captchaChallenge = params[1]
        try {
            val url = URL("https://albaraka.com.sy/KYC/ApiController/sendOtp")
            val connection = url.openConnection() as HttpURLConnection

            // Set the request method to POST
            connection.requestMethod = "POST"
            connection.doOutput = true

            // Create the request body
            val postData: MutableMap<String, String?> = HashMap()
            postData["phone"] = mobileNumber
            postData["captcha_challenge"] = captchaChallenge
            postData["skip_captcha"] = "true";
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
            BufferedReader(InputStreamReader(connection.inputStream)).use { br ->
                val response = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    response.append(line)
                }
                return response.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun handleServerResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            if (jsonResponse.getBoolean("data")) {
                showOtpDialog()
                showToast("تم إرسال رمز التحقق بنجاح")
            } else {
                showToast(jsonResponse.getString("وصف خاطئ"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



    fun SendOtpReq() {
        // Inside your Fragment class

        val captchaInputText = mViewDataBinding?.captchaInput?.text.toString()
        val mobnumText = mViewDataBinding?.mobnumm?.text.toString()
        sendOtpAndHandleResponse(mobnumText, captchaInputText)

    }


    fun getImageUriFromImageView(imageView: ImageView?): Uri? {
        val drawable = imageView?.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val uri = getImageUriFromBitmap(bitmap)
            return uri
        }
        return null
    }

    fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val tempUri = Uri.fromFile(File.createTempFile("tempImage", ".jpg"))
        val outputStream = FileOutputStream(tempUri.path)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return tempUri
    }
    fun getFilePathFromUri(contentResolver: ContentResolver, uri: Uri?): String? {
        val filePath: String?
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        if (cursor == null) {
            filePath = uri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath
    }
    fun saveNewAccountRequest2(otp : String) {


        GlobalScope.launch {
            try {

                responseWaiting = true

                val isStudent = if (mViewDataBinding.btnFinancing.isChecked) {
                    "طالب ${mViewDataBinding.universitySpinner.selectedItem} ${mViewDataBinding.collegeSpinner.selectedItem}"
                } else {
                    mViewDataBinding.job.text.toString()
                }

                val imageUploadViews = listOf(
                    mViewDataBinding?.imageUploadView2,
                    mViewDataBinding?.imageUploadView3,
                    mViewDataBinding?.imageUploadView1,
                    mViewDataBinding?.imageUploadView5
                )

                val filePaths = mutableListOf<String?>()

                if (imageUploadViews.any { it == null }) {
                    withContext(Dispatchers.Main) {
                        showToast("الرجاء تحديد جميع الصور قبل الإرسال.")
                    }
                    return@launch
                }

                val selectedYear = datePicker.year
                val selectedMonth = datePicker.month + 1 // Adjust month since it's zero-based
                val selectedDay = datePicker.dayOfMonth

                // Format the date as needed, for example, in the format "dd/MM/yyyy"
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth, selectedYear)
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("first_name", mViewDataBinding?.etFirstName?.text.toString())
                    .addFormDataPart("father_name", mViewDataBinding?.etFirstNamde?.text.toString())
                    .addFormDataPart("last_name", mViewDataBinding?.etFirstNamde2?.text.toString())
                    .addFormDataPart("mother_name", mViewDataBinding?.moss?.text.toString())
                    .addFormDataPart("last_mother_name", mViewDataBinding?.mos22?.text.toString())
                    .addFormDataPart("birthdate_place", mViewDataBinding?.mos33?.text.toString())
                    .addFormDataPart("gender", mViewDataBinding?.genderSpinner?.selectedItem.toString())
                    .addFormDataPart("nationality", mViewDataBinding?.gender2Spinner?.selectedItem.toString())
                    .addFormDataPart("birthdate", formattedDate)
                    .addFormDataPart("national_id_type", mViewDataBinding?.typeIdSpinner?.selectedItem.toString())
                    .addFormDataPart("national_id", mViewDataBinding?.nationalNumberr?.text.toString())
                    .addFormDataPart("national_id_place", mViewDataBinding?.nationalPlacec?.text.toString())
                    .addFormDataPart("national_id_documenter", mViewDataBinding?.kayed?.text.toString())
                    .addFormDataPart("address", mViewDataBinding.addrr.text.toString())
                    .addFormDataPart("phone", mViewDataBinding.mobnumm.text.toString())
                    .addFormDataPart("job",  isStudent)
                    .addFormDataPart("fatca_id_type", mViewDataBinding?.qanonSpinner?.selectedItem.toString())
                    .addFormDataPart("delivery_address", mViewDataBinding?.branchSpinnerId?.selectedItem.toString())
                    .addFormDataPart("captcha_challenge", mViewDataBinding?.captchaInput?.text.toString())
                    .addFormDataPart("otp", otp)

                // Add image files to the request body

//                }
                val imageFields = listOf(
                    "nationality_image1",
                    "nationality_image2",
                    "personal_image",
                    "university_card_image"
                )


                val contentResolver = requireContext().contentResolver  // replace with your actual context
                for ((index, imageView) in imageUploadViews.withIndex()) {
                    val imageUri = getImageUriFromImageView(imageView)

                    if (imageUri != null) {
                        val filePath = getFilePathFromUri(contentResolver, imageUri)
                        filePaths.add(filePath)

                        if (filePath != null) {
                            val compressedImage = CompressImageTask(File(filePath), maxWidth = 800, maxHeight = 800)
                            val requestFile = compressedImage.execute().get()?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            if (requestFile != null) {
                                val imageField = imageFields.getOrNull(index) ?: continue
                                requestBody.addFormDataPart(imageField, compressedImage.file.name, requestFile)
                            }
                        }
                    }
                }






                val request = Request.Builder()
                    .url("https://albaraka.com.sy/KYC/ApiController/saveData")
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
                                showToast("تم حفظ الحساب بنجاح")
                                val intent = Intent(requireContext(), finishActivity::class.java)
                                startActivity(intent)
                                // Response indicates success
                                // Handle accordingly
                            } else {
                                // Response indicates failure
                                goToStep(4)
                                showToast("حدث خطأ يرجى التأكد من صحة رمز التحقق والمحاولة مرة أخرى ")
                                // Handle accordingly
                            }
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


    private inner class FetchUniversityDataTask : AsyncTask<String, Void, Pair<List<String>, JSONArray?>>() {

        override fun doInBackground(vararg params: String): Pair<List<String>, JSONArray?> {
            val urlString = params[0]
            val universityNames = mutableListOf<String>()

            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()

                val jsonObject = JSONObject(response.toString())
                val universitiesArray = jsonObject.optJSONArray("universities")

                if (universitiesArray != null) {
                    for (i in 0 until universitiesArray.length()) {
                        val universityObject = universitiesArray.getJSONObject(i)
                        val universityName = universityObject.optString("name", "")
                        universityNames.add(universityName)
                    }
                }

                return Pair(universityNames, universitiesArray) // Return universitiesArray as well
            } catch (e: Exception) {
                Log.e("FetchUniversityDataTask", "Error fetching university data", e)
            }

            return Pair(emptyList(), null) // Return empty list and null for universitiesArray on error
        }

        override fun onPostExecute(result: Pair<List<String>, JSONArray?>) {
            super.onPostExecute(result)
            lastFetchedUniversityData = result.second
            updateUniversityAndCollegeSpinners(result.first)
        }
    }

    private fun updateUniversityAndCollegeSpinners(universityNames: List<String>) {
        val defaultUniversity = "اختر الجامعة"
        val updatedUniversityNames  = mutableListOf(defaultUniversity)
        updatedUniversityNames.addAll(universityNames)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, universityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        universitySpinner.adapter = adapter

        // Set up the university spinner listener
        universitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUniversity = universitySpinner.selectedItem as String
                fetchCollegesForUniversity(selectedUniversity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun fetchCollegesForUniversity(selectedUniversity: String) {
        if (lastFetchedUniversityData != null) {
            for (i in 0 until lastFetchedUniversityData!!.length()) {
                val universityObject = lastFetchedUniversityData!!.getJSONObject(i)
                val universityName = universityObject.optString("name", "")
                if (universityName == selectedUniversity) {
                    val facultiesArray = universityObject.optJSONArray("faculties")
                    if (facultiesArray != null) {
                        val collegeNames = mutableListOf<String>()
                        for (j in 0 until facultiesArray.length()) {
                            val facultyObject = facultiesArray.getJSONObject(j)
                            val collegeName = facultyObject.optString("name", "")
                            collegeNames.add(collegeName)
                        }
                        updateCollegeSpinner(collegeNames)
                        break
                    }
                }
            }
        }
    }

    private fun updateCollegeSpinner(collegeNames: List<String>) {
        val defaultCollege = "اختر الكلية"
        val updatedCollegeNames = mutableListOf(defaultCollege)
        updatedCollegeNames.addAll(collegeNames)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, collegeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        collegeSpinner.adapter = adapter
    }
}