package com.apps2you.albaraka.ui.home

import android.content.Intent
import android.net.Uri
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentGuestHomeBinding
import com.apps2you.albaraka.ui.about.AboutActivity
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.base.BaseViewModel
import com.apps2you.albaraka.ui.calculator.CalculatorActivity
import com.apps2you.albaraka.ui.complaints.ComplaintActivity
import com.apps2you.albaraka.ui.locations.LocationsActivity
import com.apps2you.albaraka.ui.products.ServicesActivity
import com.apps2you.albaraka.utils.Constants


class GuestHomeFragment : BaseFragment<FragmentGuestHomeBinding, BaseViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_guest_home
    }

    override fun setViewModel(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun setUpView() {
        viewDataBinding.btnLocations.setOnClickListener { startActivity(Intent(context, LocationsActivity::class.java)) }

        viewDataBinding.btnServices.setOnClickListener { startActivity(Intent(context, ServicesActivity::class.java)) }

        viewDataBinding.btnFinancing.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_FINANCING))) }

        viewDataBinding.btnComplaint.setOnClickListener { startActivity(Intent(context, ComplaintActivity::class.java)) }

        viewDataBinding.btnAbout.setOnClickListener { startActivity(Intent(context, AboutActivity::class.java)) }

        viewDataBinding.btnCalculator.setOnClickListener { startActivity(Intent(context, CalculatorActivity::class.java)) }

        viewDataBinding.btnLogin.setOnClickListener { activity?.finish() }
    }

    override fun fetchData() {

    }
}