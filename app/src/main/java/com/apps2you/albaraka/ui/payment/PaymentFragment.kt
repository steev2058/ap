package com.apps2you.albaraka.ui.payment

import android.content.Intent
import android.view.View
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentPaymentBinding
import com.apps2you.albaraka.ui.atmCard.AtmCardsActivity
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.transfer.accountsTransfer.TransferActivity
import com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs.SYGSActivity
import com.apps2you.albaraka.ui.transfer.adsl.ADSLActivity
import com.apps2you.albaraka.ui.transfer.alphaCapital.AlphaPaymentActivity
import com.apps2you.albaraka.ui.transfer.bills.BillsActivity
import com.apps2you.albaraka.ui.transfer.hf.HFActivity
import com.apps2you.albaraka.ui.transfer.payment.education.schools.SchoolsPaymentActivity
import com.apps2you.albaraka.ui.transfer.payment.education.universities.UniversitiesPaymentActivity
import com.apps2you.albaraka.ui.transfer.payment.mobile.MobilePaymentActivity
import com.apps2you.albaraka.ui.transfer.payment.restaurants.RestaurantsPaymentActivity
import com.apps2you.albaraka.ui.transfer.sadaka.SadakaActivity
import com.apps2you.albaraka.ui.transfer.zakat.ZakatActivity
import com.apps2you.albaraka.utils.Constants
import com.apps2you.albaraka.viewmodels.HomeViewModel

class PaymentFragment : BaseFragment<FragmentPaymentBinding, HomeViewModel>(),
        PayQuickServiceAdapter.ItemClickListener {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_payment
    }

    override fun setViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun setUpView() {
        viewDataBinding.swipeRefresh.setOnRefreshListener { fetchData() }
    }

    override fun fetchData() {
        viewModel.quickServices.observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (!viewDataBinding.swipeRefresh.isRefreshing)
                        viewDataBinding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    viewDataBinding.swipeRefresh.isRefreshing = false

                    showToast(it.message)
                }
                Status.SUCCESS -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    viewDataBinding.swipeRefresh.isRefreshing = false

                    viewDataBinding.recyclerView.adapter = it?.data?.let { items ->
                        viewModel.isSYGSActivated = items.find { q: QuickService -> q.id == Constants.SYGS } != null

                        val adapter = PayQuickServiceAdapter(items)
                        adapter.itemClickListener = this
                        adapter
                    }
                    viewDataBinding.recyclerView.adapter = it?.data?.let { items ->
                        viewModel.isHFActivated = items.find { q: QuickService -> q.id == Constants.TRANSFER_HF } != null

                        val adapter = PayQuickServiceAdapter(items)
                        adapter.itemClickListener = this
                        adapter
                    }
                }
            }
        })
    }

    override fun onItemClick(item: QuickService) {
        when (item.id) {
            Constants.TRANSFER -> openTransferActivity()

            Constants.ZAKAT -> openZakatActivity()

            Constants.SADAKA -> openSadakaActivity()

            Constants.UNIVERSITIES -> openUniversitiesPaymentActivity()

            Constants.SCHOOLS -> openSchoolsPaymentActivity()

            Constants.MOBILE_PAYMENT -> openMobilePaymentActivity()

            Constants.ADSL -> openADSLActivity()

            Constants.RESTAURANTS -> openRestaurantsPaymentActivity()

            Constants.BILLS -> activityNavigation.navigate(Intent(requireContext(), BillsActivity::class.java))

            Constants.ALPHA_CAPITAL -> activityNavigation.navigate(Intent(requireContext(), AlphaPaymentActivity::class.java))

            Constants.ATM_CARDS -> activityNavigation.navigate(Intent(requireContext(), AtmCardsActivity::class.java))

            Constants.SYGS -> activityNavigation.navigate(Intent(requireContext(), SYGSActivity::class.java))

            Constants.HF -> activityNavigation.navigate(Intent(requireContext(), HFActivity::class.java))
        }
    }

    private fun openTransferActivity() {
        activityNavigation.navigate(TransferActivity.getIntent(requireContext(), viewModel.isSYGSActivated,viewModel.isHFActivated))
    }

    private fun openZakatActivity() {
        activityNavigation.navigate(Intent(requireContext(), ZakatActivity::class.java))
    }

    private fun openSadakaActivity() {
        activityNavigation.navigate(Intent(requireContext(), SadakaActivity::class.java))
    }

    private fun openADSLActivity() {
        activityNavigation.navigate(Intent(requireContext(), ADSLActivity::class.java))
    }

    private fun openUniversitiesPaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), UniversitiesPaymentActivity::class.java))
    }

    private fun openSchoolsPaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), SchoolsPaymentActivity::class.java))
    }

    private fun openRestaurantsPaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), RestaurantsPaymentActivity::class.java))
    }

    private fun openMobilePaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), MobilePaymentActivity::class.java))
    }
}