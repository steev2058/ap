package com.apps2you.albaraka.ui.complaints.fragments

import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.Branch
import com.apps2you.albaraka.data.model.Title
import com.apps2you.albaraka.data.model.getDefault
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentComplaintBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.utils.CustomTextWatcher
import com.apps2you.albaraka.viewmodels.ComplaintViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class ComplaintFragment : BaseFragment<FragmentComplaintBinding, ComplaintViewModel>(), DatePickerDialog.OnDateSetListener {

    override fun createViewModel() {
        mViewModel = ViewModelProvider(mActivity).get(ComplaintViewModel::class.java)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_complaint
    }

    override fun setViewModel(): Class<ComplaintViewModel> {
        return ComplaintViewModel::class.java
    }

    override fun setUpView() {
        mViewDataBinding.etTelephoneNumber.addTextChangedListener(CustomTextWatcher(mViewDataBinding.tiTelephoneNumber))
        mViewDataBinding.etMessage.addTextChangedListener(CustomTextWatcher(mViewDataBinding.tiMessage))


        mViewDataBinding.spinnerClient.adapter = SpinnerAdapter(mViewModel.getClientSpinnerItems(mActivity))
        mViewDataBinding.spinnerMessageTitle.adapter = SpinnerAdapter(mViewModel.getMessageTitleSpinnerItem(mActivity))
        mViewDataBinding.spinnerPlace.adapter = SpinnerAdapter(arrayListOf(getDefault(getString(R.string.place_of_incident))))

        mViewDataBinding.iBtnDate.setOnClickListener { openDatePicker() }
        mViewDataBinding.etDate.setOnClickListener { openDatePicker() }
        mViewDataBinding.btnSend.setOnClickListener {
            mViewDataBinding.layout.requestFocus()
            sendComplaint()
        }

        mViewDataBinding.spinnerClient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = mViewDataBinding.spinnerClient.adapter.getItem(position)
                if (item is Title && position != 0) {
                    if (position == 1) {
                        mViewModel.complaint.clientStatus = "Client"
                    } else if (position == 2) {
                        mViewModel.complaint.clientStatus = "Not a client"
                    }
                } else {
                    mViewModel.complaint.clientStatus = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mViewDataBinding.spinnerMessageTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = mViewDataBinding.spinnerMessageTitle.adapter.getItem(position)
                if (item is Title && item.title_id != -1) {
                    mViewModel.complaint.complaintTitleID = item.title_id
                } else {
                    mViewModel.complaint.complaintTitleID = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mViewDataBinding.spinnerPlace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = mViewDataBinding.spinnerPlace.adapter.getItem(position)
                if (item is Branch && item.branch_id != -1) {
                    mViewModel.complaint.branchID = item.branch_id
                } else {
                    mViewModel.complaint.branchID = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mViewDataBinding.rgTime.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mViewDataBinding.rbMorning.id -> {
                    mViewModel.complaint.contactTime = "9-11"
                }
                mViewDataBinding.rbAfternoon.id -> {
                    mViewModel.complaint.contactTime = "12-2"
                }
                mViewDataBinding.rbEvening.id -> {
                    mViewModel.complaint.contactTime = "3-5"
                }
            }
        }
    }

    override fun fetchData() {
        mViewModel.getBranches().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    @Suppress("UNCHECKED_CAST")
                    val adapter = mViewDataBinding.spinnerPlace.adapter as SpinnerAdapter<Branch>
                    it.data?.let { items ->
                        val list = adapter.items
                        list.addAll(items)
                        adapter.refreshList(list)
                    }
                }
                Status.ERROR -> {
                    showToast(it.message)
                }
                Status.LOADING -> {
                }
            }
        })

        mViewModel.getComplaintTitles().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    @Suppress("UNCHECKED_CAST")
                    val adapter = mViewDataBinding.spinnerMessageTitle.adapter as SpinnerAdapter<Title>
                    it.data?.let { items ->
                        val list = adapter.items
                        list.addAll(items)
                        adapter.refreshList(list)
                    }
                }
                Status.ERROR -> {
                    showToast(it.message)
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun sendComplaint() {

        if (TextUtils.isEmpty(mViewModel.complaint.phoneNumber)) {
            setInputError(mViewDataBinding.tiTelephoneNumber, getString(R.string.error_required))
            return
        }

        if (mViewModel.complaint.email != null &&
                !Patterns.EMAIL_ADDRESS.matcher(mViewModel.complaint.email!!).matches()) {
            showToast(getString(R.string.please_enter_a_valid_email))
            return
        }

        if (mViewModel.complaint.message.isEmpty()) {
            setInputError(mViewDataBinding.tiMessage, getString(R.string.error_required))
            return
        }

        mViewModel.sendComplaint().observe(this, {
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

    private fun openDatePicker() {
        val now = Calendar.getInstance()
        val okTitle = getString(R.string.action_ok)
        val cancelTitle = getString(R.string.prompt_info_cancel)

        val datePickerDialog = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).apply {
            version = DatePickerDialog.Version.VERSION_2
            setOkColor(ContextCompat.getColor(baseActivity, R.color.colorAccent))
            setCancelColor(ContextCompat.getColor(baseActivity, R.color.colorAccent))
            setOkText(okTitle)
            setCancelText(cancelTitle)
        }

        datePickerDialog.show(mActivity.supportFragmentManager, DatePickerDialog::class.simpleName)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        viewModel.complaint.complaintDate = sdf.format(calendar.time)
    }

}