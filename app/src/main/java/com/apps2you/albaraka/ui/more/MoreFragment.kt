package com.apps2you.albaraka.ui.more

import android.content.Intent
import android.net.Uri
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.FragmentMoreBinding
import com.apps2you.albaraka.ui.about.AboutActivity
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.calculator.CalculatorActivity
import com.apps2you.albaraka.ui.complaints.ComplaintActivity
import com.apps2you.albaraka.ui.exchange.ExchangeActivity
import com.apps2you.albaraka.ui.kyc.KycActivity
import com.apps2you.albaraka.ui.locations.LocationsActivity
import com.apps2you.albaraka.ui.locations_v2.LocationsV2Activity
import com.apps2you.albaraka.ui.products.ServicesActivity
import com.apps2you.albaraka.utils.Constants
import com.apps2you.albaraka.viewmodels.HomeViewModel

class MoreFragment : BaseFragment<FragmentMoreBinding, HomeViewModel>() {


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_more
    }

    override fun setViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun setUpView() {
        viewDataBinding.tvLocation.setOnClickListener { startActivity(Intent(context, LocationsActivity::class.java)) }
        viewDataBinding.tvLocationV2.setOnClickListener { startActivity(Intent(context, LocationsV2Activity::class.java)) }
        viewDataBinding.tvExchangeRate.setOnClickListener { startActivity(Intent(context, ExchangeActivity::class.java)) }
        viewDataBinding.tvKyc.setOnClickListener { startActivity(Intent(context, KycActivity::class.java)) }
        viewDataBinding.tvAbout.setOnClickListener { startActivity(Intent(context, AboutActivity::class.java)) }
        viewDataBinding.tvComplaints.setOnClickListener { startActivity(Intent(context, ComplaintActivity::class.java)) }
        viewDataBinding.tvServices.setOnClickListener { startActivity(Intent(context, ServicesActivity::class.java)) }
        viewDataBinding.tvCalculator.setOnClickListener { startActivity(Intent(context, CalculatorActivity::class.java)) }
        viewDataBinding.tvFinancing.setOnClickListener { openLink(Constants.LINK_FINANCING) }
        viewDataBinding.tvAtm.setOnClickListener { openLink(Constants.LINK_ORDER_ATM) }
    }

    override fun fetchData() {

    }

    private fun openLink(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}