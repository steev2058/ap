package com.apps2you.albaraka.ui.financeForm.fragments

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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
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
import com.apps2you.albaraka.databinding.FragmentFinanceBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.financeForm.CompressImageTask
import com.apps2you.albaraka.ui.kyc.fragments.ResultActivity
import com.apps2you.albaraka.ui.kyc.fragments.ScannerActivity
import com.apps2you.albaraka.ui.kyc.fragments.finishActivity
import com.apps2you.albaraka.viewmodels.FinanceFormViewModel
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
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
import java.util.Locale
import java.util.Random
import java.util.regex.Pattern

class FinanceFormFragment  : BaseFragment<FragmentFinanceBinding, FinanceFormViewModel>()   {
    data class FinanceType(val id: String, val type: String, val maxYear: Int)
    // Fetch data from the API asynchronously
    private inner class FetchFinanceTypesDataTask : AsyncTask<String, Void, List<FinanceType>>() {
        override fun doInBackground(vararg params: String?): List<FinanceType> {
            val urlString = params[0] ?: return emptyList()
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val financeTypes = mutableListOf<FinanceType>()
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val type = jsonObject.getString("type")
                val maxYear = jsonObject.getInt("max_year")
                financeTypes.add(FinanceType(id, type, maxYear))
            }

            return financeTypes
        }

        override fun onPostExecute(result: List<FinanceType>?) {
            result?.let {
                setupFinanceTypeSpinner(it)
            }
        }
    }

    // Set up the finance type spinner
    private fun setupFinanceTypeSpinner(financeTypes: List<FinanceType>) {
        val defaultType = "اختر نوع التمويل"
        val financeTypeNames = mutableListOf(defaultType)
        financeTypeNames.addAll(financeTypes.map { it.type })

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, financeTypeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mViewDataBinding.choseFinanceType.adapter = adapter

        mViewDataBinding.choseFinanceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) { // Ignore the default option
                    val selectedFinanceType = financeTypes[position - 1]
                    setupNoYearsSpinner(selectedFinanceType.maxYear)
                } else {
                    // Reset the noYearsSpinner if the default option is selected
                    setupNoYearsSpinner(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    // Set up the no years spinner
    private fun setupNoYearsSpinner(maxYear: Int) {
        val defaultOption = "اختر عدد السنوات المطلوب للتسديد"
        val years = mutableListOf(defaultOption)
        if (maxYear > 0) {
            years.addAll((1..maxYear).map { it.toString() })
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mViewDataBinding.noYearsSpinner.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_finance
    }
    private var position = 0

    private lateinit var datePicker : DatePicker
    var responseWaiting: Boolean = false
    private var lastFetchedUniversityData: JSONArray? = null


    private fun setupStepView(){


        mViewDataBinding.stepView.done(false)
        mViewDataBinding.button.setOnClickListener {
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
                    val isAgreementCheckbox2Checked = mViewDataBinding.agreementCheckbox2.isChecked
                    if (!isAgreementCheckbox2Checked) {
                        showToast("يرجى التحقق من مربع الاقتراح الثاني")
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

//        mViewDataBinding.radio.setOnCheckedChangeListener { _, _ ->
//            handleRadioButtons()
//        }

        mViewDataBinding.previousButton.visibility = if (position == 0) View.GONE else View.VISIBLE
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

                return
            }
        // Hide all steps
        mViewDataBinding.personalDetails.visibility = View.GONE
        mViewDataBinding.CommunicationInfo.visibility = View.GONE
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
                mViewDataBinding.CommunicationInfo.visibility = View.VISIBLE
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
                mViewDataBinding.button.text = "إدخال الطلب"
            }
        }

        position = step
        mViewDataBinding.stepView.go(position, true)




    }



    //Validation
    private fun validateForm(): Boolean {
        when (position) {
            0 -> {
                // Validate the form on the first step (personal details) if needed
                val firstName = mViewDataBinding.etFirstName.text.toString().trim()
                val fatherName = mViewDataBinding.etFirstNamde.text.toString().trim()
                val lastName = mViewDataBinding.etFirstNamde2.text.toString().trim()


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



                // Validate the gender spinner
                val selectedGender = mViewDataBinding.genderSpinner.selectedItem.toString()
                if (selectedGender == "اختر الجنس") {
                    showToast("يرجى تحديد الجنس")
                    return false
                }



                val selectedmilitary_servic = mViewDataBinding.genderSpinnerServiceMilitary.selectedItem.toString()
                if (selectedmilitary_servic == "الخدمة العسكرية") {
                    showToast("يرجى تحديد الخدمة العسكرية")
                    return false
                }

                val selectedNationality = mViewDataBinding.gender2Spinner.selectedItem.toString()
                if (selectedNationality == "اختر الجنسية") {
                    showToast("يرجى تحديد الجنسية")
                    return false
                }

                // Validate the Spinner
                val selectedTypeId = mViewDataBinding.typeIdSpinner.selectedItem.toString()
                if (selectedTypeId == "اختر نوع الوثيقة") {
                    showToast("يرجى تحديد خيار نوع الوثيقة")
                    return false
                }


                val nationalNumber = mViewDataBinding.nationalNumberr.text.toString().trim()
                if (nationalNumber.isEmpty()) {
                    mViewDataBinding.nationalNumberr.error = "يرجى إدخال الرقم الوطني"
                    return false
                } else {
                    mViewDataBinding.nationalNumberr.error = null
                }

            }
            1 -> {

                val address = mViewDataBinding.etAddressInfo.text.toString().trim()
                val mobileNumber = mViewDataBinding.mobnumm.text.toString().trim()
                val email = mViewDataBinding.etEmail.text.toString().trim()
                    if (address.isEmpty()) {
                        mViewDataBinding.etAddressInfo.error = "يرجى إدخال عنوان السكن الحالي"
                        return false
                    } else {
                        mViewDataBinding.etAddressInfo.error = null
                    }

                if (mobileNumber.isEmpty()) {
                        mViewDataBinding.mobnumm.error = "يرجى إدخال رقم هاتفك المحمول"
                        return false
                    } else {
                        mViewDataBinding.mobnumm.error = null
                    }

//                if (email.isEmpty()) {
//                    mViewDataBinding.etEmail.error = "يرجى إدخال الايميل"
//                    return false
//                } else {
//                    mViewDataBinding.etEmail.error = null
//                }
                if (!validateEmail()) {
                    return false
                }

            }
            2 -> {
                val selectedJob = mViewDataBinding.jobSpinner.selectedItem.toString()
                if (selectedJob == "اختر العمل الحالي") {
                    showToast("يرجى تحديد العمل الحالي")
                    return false
                }


                val nameCompany = mViewDataBinding.nameOfCompany2.text.toString().trim()
                if (nameCompany.isEmpty()) {
                    mViewDataBinding.nameOfCompany2.error = "يرجى إدخال اسم الشركة/المعمل"
                    return false
                } else {
                    mViewDataBinding.nameOfCompany2.error = null
                }

                val jobDescription = mViewDataBinding.jobDescription2.text.toString().trim()
                if (jobDescription.isEmpty()) {
                    mViewDataBinding.jobDescription2.error = "يرجى إدخال المنصب الوظيفي"
                    return false
                } else {
                    mViewDataBinding.jobDescription2.error = null
                }


                val dateJob = mViewDataBinding.etDate.text.toString().trim()
                if (dateJob.isEmpty()) {
                    mViewDataBinding.etDate.error = "يرجى إدخال تاريخ العمل"
                    return false
                } else {
                    mViewDataBinding.etDate.error = null
                }

                val moreJobInfo = mViewDataBinding.moreJobInfo2.text.toString().trim()
                if (moreJobInfo.isEmpty()) {
                    mViewDataBinding.moreJobInfo2.error = "يرجى إدخال  تفاصيل إضافية عن العمل"
                    return false
                } else {
                    mViewDataBinding.moreJobInfo2.error = null
                }

                val addrJobInfo = mViewDataBinding.addressJobInfoInDetails2.text.toString().trim()
                if (addrJobInfo.isEmpty()) {
                    mViewDataBinding.addressJobInfoInDetails2.error = "يرجى إدخال عنوان العمل بالتفصيل"
                    return false
                } else {
                    mViewDataBinding.addressJobInfoInDetails2.error = null
                }

            }
            3 -> {
                // Validate basic salary
                val basicSalary = mViewDataBinding.basicSalary2.text.toString().trim()
                if (basicSalary.isEmpty()) {
                    mViewDataBinding.basicSalary2.error = "يرجى إدخال الراتب الأساسي"
                    return false
                } else {
                    mViewDataBinding.basicSalary2.error = null
                }

                // Validate additional income details if income spinner is selected
                val additionalIncome = mViewDataBinding.isThereIncomeSpinner.selectedItem.toString()
                if (additionalIncome == "نعم") {
                    val additionalIncomeDetails = mViewDataBinding.additionalIncomeDetails2.text.toString().trim()
                    if (additionalIncomeDetails.isEmpty()) {
                        mViewDataBinding.additionalIncomeDetails2.error = "يرجى إدخال تفاصيل الدخل الإضافي"
                        return false
                    } else {
                        mViewDataBinding.additionalIncomeDetails2.error = null
                    }
                }

                // Validate additional salary
                val additionalSalary = mViewDataBinding.additionalSalary2.text.toString().trim()
                if (additionalSalary.isEmpty()) {
                    mViewDataBinding.additionalSalary2.error = "يرجى إدخال الراتب الإضافي"
                    return false
                } else {
                    mViewDataBinding.additionalSalary2.error = null
                }

                // Validate engagements spinner and details
                val engagements = mViewDataBinding.isThereAnotherEngagments.selectedItem.toString()
                if (engagements == "نعم") {
                    val engagementValue = mViewDataBinding.engagementValue2.text.toString().trim()
                    if (engagementValue.isEmpty()) {
                        mViewDataBinding.engagementValue2.error = "يرجى إدخال قيمة الالتزام"
                        return false
                    } else {
                        mViewDataBinding.engagementValue2.error = null
                    }

                    val monthlyPayment = mViewDataBinding.monthlyPayment2.text.toString().trim()
                    if (monthlyPayment.isEmpty()) {
                        mViewDataBinding.monthlyPayment2.error = "يرجى إدخال الدفع الشهري"
                        return false
                    } else {
                        mViewDataBinding.monthlyPayment2.error = null
                    }

                    val bankName = mViewDataBinding.bankName2.text.toString().trim()
                    if (bankName.isEmpty()) {
                        mViewDataBinding.bankName2.error = "يرجى إدخال اسم البنك"
                        return false
                    } else {
                        mViewDataBinding.bankName2.error = null
                    }
                }

                // Validate non-bank engagements spinner and details
                val nonBankEngagements = mViewDataBinding.isThereAnotherEngagmentsNotBank.selectedItem.toString()
                if (nonBankEngagements == "نعم") {
                    val engagementName = mViewDataBinding.engagementName2.text.toString().trim()
                    if (engagementName.isEmpty()) {
                        mViewDataBinding.engagementName2.error = "يرجى إدخال اسم الالتزام"
                        return false
                    } else {
                        mViewDataBinding.engagementName2.error = null
                    }

                    val monthlyPaymentNb = mViewDataBinding.monthlyPaymentNb2.text.toString().trim()
                    if (monthlyPaymentNb.isEmpty()) {
                        mViewDataBinding.monthlyPaymentNb2.error = "يرجى إدخال الدفع الشهري"
                        return false
                    } else {
                        mViewDataBinding.monthlyPaymentNb2.error = null
                    }
                }
            }
            4 -> {

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


            }
        }

        //  all validations pass
        return true
    }



    private fun validateEmail(): Boolean {
        val email = mViewDataBinding.etEmail.text.toString().trim()
        return if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.please_enter_a_valid_email))
            false
        } else {
            true
        }
    }


    // All Spinner
    private  fun  setupSpinners(){


        val genderSpinner = mViewDataBinding.genderSpinner

        val jobSpinner = mViewDataBinding.jobSpinner
        val typeidSpinner = mViewDataBinding.typeIdSpinner
        val serviceMilitarySpinner = mViewDataBinding.genderSpinnerServiceMilitary
        val serviceMilitarySpinnerimg = mViewDataBinding.genderSpinnerServiceMilitaryimg
        val branchSpinner = mViewDataBinding.branchSpinnerId
        val genderSpinner2 = mViewDataBinding.gender2Spinner

        val isThereIncomeSpinner = mViewDataBinding.isThereIncomeSpinner
        val isThereAnotherEngagementsSpinner = mViewDataBinding.isThereAnotherEngagments
        val isThereAnotherEngagementsNotBankSpinner = mViewDataBinding.isThereAnotherEngagmentsNotBank

        val insuranseSpinner = mViewDataBinding.insuranseSpinner

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
        val serviceMilitaryOptions = arrayOf("الخدمة العسكرية","مؤجل","مؤدي", "معفى")
        val isThereIncomeOptions = arrayOf("هل يوجد دخل إضافي","نعم", "لا")
        val isThereAnotherEngagementsOptions = arrayOf("هل يوجد لديكم التزامات قائمة لدى البنوك العاملة في سورية","نعم", "لا")
        val isThereAnotherEngagementsNotBankOptions = arrayOf("هل يوجد لديكم التزامات اُخرى (إيجار منزل ، أقساط غير مصرفية)","نعم", "لا")
        val jobOptions = arrayOf("العمل الحالي","نقابي","تاجر / صناعي", "موظف ","غير ذلك")
        val typeidOptions = arrayOf("اختر نوع الوثيقة","بطاقة شخصية", "هوية عسكرية")
        val genderOptions = arrayOf("اختر الجنس","ذكر", "أنثى")
        val nationalityOptions = arrayOf("اختر الجنسية","سوري", "فلسطيني","غير ذلك")

        val insuranseOptions = arrayOf("اختر الضمانات الممكن تقديمها","كفالة شخصية ", "كفالة شركة","توطين","رهن عقاري","رهن سيارة خاصة","لايوجد ضمان")



        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        val adapterMilitary = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceMilitaryOptions)
        adapterMilitary.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceMilitarySpinner.adapter = adapterMilitary

        val adapterInsuranseOptions = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, insuranseOptions)
        adapterInsuranseOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        insuranseSpinner.adapter = adapterInsuranseOptions

        val adapterIsThereIncomeOptions = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, isThereIncomeOptions)
        adapterIsThereIncomeOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        isThereIncomeSpinner.adapter = adapterIsThereIncomeOptions

        val adapterJob = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobOptions)
        adapterJob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        jobSpinner.adapter = adapterJob

        val adapterIsThereAnotherEngagement = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, isThereAnotherEngagementsOptions)
        adapterIsThereAnotherEngagement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        isThereAnotherEngagementsSpinner.adapter = adapterIsThereAnotherEngagement

        val adapterIsThereAnotherEngagementNb = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, isThereAnotherEngagementsNotBankOptions)
        adapterIsThereAnotherEngagementNb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        isThereAnotherEngagementsNotBankSpinner.adapter = adapterIsThereAnotherEngagementNb


        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalityOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner2.adapter = adapter2

        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeidOptions)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeidSpinner.adapter = adapter3


        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, branchOptions)
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        branchSpinner.adapter = adapter6


        // Set up listener for genderSpinner
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGender = parent.getItemAtPosition(position).toString()
                if (selectedGender == "ذكر") {
                    serviceMilitarySpinner.visibility = View.VISIBLE
                    serviceMilitarySpinnerimg.visibility = View.VISIBLE
                } else {
                    serviceMilitarySpinner.visibility = View.GONE
                    serviceMilitarySpinnerimg.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Initial visibility setting based on default selection
        val initialGender = genderSpinner.selectedItem.toString()
        if (initialGender == "ذكر") {
            serviceMilitarySpinner.visibility = View.VISIBLE
            serviceMilitarySpinnerimg.visibility = View.VISIBLE
        } else {
            serviceMilitarySpinner.visibility = View.GONE
            serviceMilitarySpinnerimg.visibility = View.GONE
        }

    }

    override fun setupBaseObservers() {}


    override fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
        mViewModel = ViewModelProvider(mActivity).get(FinanceFormViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }



    override fun setViewModel(): Class<FinanceFormViewModel> {
        return FinanceFormViewModel::class.java
    }

        override fun setUpView() {




            val urlString = "https://albaraka.com.sy/AlBarakaForms/ApiController/credit_types"
            FetchFinanceTypesDataTask().execute(urlString)
            // Set up spinners
            setupSpinners()

            // Set up agreement checkbox
            setupAgreementCheckbox()

            // Set up step view
            setupStepView()
            datePicker =mViewDataBinding.datePicker2;






            mViewDataBinding.etFirstName.addTextChangedListener(textWatcher)
            mViewDataBinding.etFirstNamde.addTextChangedListener(textWatcher)
            mViewDataBinding.etFirstNamde2.addTextChangedListener(textWatcher)



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

    fun isEnglish(text: String): Boolean {
        val englishPattern = Pattern.compile("[a-zA-Z0-9-()+*/\\\\\\\\| ]+")
        return englishPattern.matcher(text).matches()
    }



    override fun fetchData() {



    }



    fun saveNewAccountRequest2(otp : String) {


        GlobalScope.launch {
            try {

                responseWaiting = true




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
                    .addFormDataPart("gender", mViewDataBinding?.genderSpinner?.selectedItem.toString())
                    .addFormDataPart("nationality", mViewDataBinding?.gender2Spinner?.selectedItem.toString())
                    .addFormDataPart("birthdate", formattedDate)
                    .addFormDataPart("national_id_type", mViewDataBinding?.typeIdSpinner?.selectedItem.toString())
                    .addFormDataPart("national_id", mViewDataBinding?.nationalNumberr?.text.toString())
                    .addFormDataPart("address", mViewDataBinding.etAddressInfo.text.toString())
                    .addFormDataPart("phone", mViewDataBinding.mobnumm.text.toString())
                    .addFormDataPart("delivery_address", mViewDataBinding?.branchSpinnerId?.selectedItem.toString())



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
                                showToast("حدث خطأ يرجى المحاولة مرة أخرى ")
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





}