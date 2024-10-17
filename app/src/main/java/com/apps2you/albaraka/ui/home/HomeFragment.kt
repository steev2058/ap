package com.apps2you.albaraka.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.Account
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.data.model.Transaction
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentHomeBinding
import com.apps2you.albaraka.ui.atmCard.AtmCardsActivity
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.common.busEvent.RefreshAccountsEvent
import com.apps2you.albaraka.ui.quick_services.PersonalizeQuickServicesActivity
import com.apps2you.albaraka.ui.sep.SEPActivity
import com.apps2you.albaraka.ui.transactions.TransactionDetailsActivity
import com.apps2you.albaraka.ui.transactions.TransactionsActivity
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
import com.apps2you.albaraka.utils.PageTransformer
import com.apps2you.albaraka.utils.bus.Bus
import com.apps2you.albaraka.utils.bus.EventBusObserver
import com.apps2you.albaraka.viewmodels.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(),
    AccountPagerAdapter.ItemClickListener,
    TransactionAdapter.ItemClickListener,
    QuickServiceAdapter.ItemClickListener {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun setViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun setUpView() {
        viewDataBinding.viewPager.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> viewDataBinding.swipeRefresh.setEnabled(false)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> viewDataBinding.swipeRefresh.setEnabled(
                    true
                )
            }
            false
        })
        viewDataBinding.swipeRefresh.setOnRefreshListener { fetchData() }

        viewDataBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        viewDataBinding.tvLabelRecent.setOnClickListener {
            if (!viewDataBinding.recyclerView.isVisible) {
                getRecentTransactions();
                viewDataBinding.tvLabelRecent.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_transactions_list,
                    0,
                    R.drawable.ic_down_arrow,
                    0
                )
            } else {
                viewDataBinding.tvAllTransactions.visibility = View.GONE
                viewDataBinding.recyclerView.visibility = View.GONE
                viewDataBinding.tvLabelRecent.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_transactions_list,
                    0,
                    R.drawable.ic_right_arrow,
                    0
                )
            }
        }

        viewDataBinding.tvAllTransactions.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    TransactionsActivity::class.java
                )
            )
        }

        viewDataBinding.tvLabelPersonalize.setOnClickListener {
            val intent = Intent(requireContext(), PersonalizeQuickServicesActivity::class.java)
            intent.putExtra("quick_services", viewModel.homeQuickServices.value)
            startActivityForResult(intent, 111)
        }

        viewModel.homeQuickServices.observe(viewLifecycleOwner, {
            viewDataBinding.rvQuickServices.adapter = it?.let { items ->

                val personalizedList = arrayListOf<QuickService>()
                for (item in items) {
                    if (item.enabled)
                        personalizedList.add(item)
                }
                val adapter = QuickServiceAdapter(personalizedList)
                adapter.itemClickListener = this
                adapter
            }
        })

    }

    private fun getRecentTransactions() {
        viewModel.recentTransactions.observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (!viewDataBinding.swipeRefresh.isRefreshing) {
                        viewDataBinding.transactionsProgress.visibility = View.VISIBLE
                    }
                }
                Status.ERROR -> {
                    viewDataBinding.swipeRefresh.isRefreshing = false
                    viewDataBinding.transactionsProgress.visibility = View.GONE

                    showToast(it.message)
                }
                Status.SUCCESS -> {
                    viewDataBinding.swipeRefresh.isRefreshing = false
                    viewDataBinding.transactionsProgress.visibility = View.GONE
                    viewDataBinding.tvAllTransactions.visibility = View.VISIBLE
                    viewDataBinding.recyclerView.visibility = View.VISIBLE

                    viewDataBinding.recyclerView.adapter =
                        it.data?.let { items -> TransactionAdapter(items) }
                    val transactionAdapter =
                        viewDataBinding.recyclerView.adapter as TransactionAdapter
                    transactionAdapter.itemClickListener = this
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
    }

    override fun onDetach() {
        super.onDetach()
        unRegisterObservers()
    }

    private val refreshAccountsObserver: EventBusObserver<RefreshAccountsEvent> =
        EventBusObserver { refresh() }

    private fun registerObservers() {
        Bus.instance()
            .register(RefreshAccountsEvent::class.java, refreshAccountsObserver, true)
    }

    private fun unRegisterObservers() {
        Bus.instance().unregister(refreshAccountsObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            viewModel.setQuickHomeServices(data.extras?.get("sorted_quick_services") as java.util.ArrayList<QuickService>?)
        }
    }

    override fun fetchData() {
        viewModel.homeData.observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    if (!viewDataBinding.swipeRefresh.isRefreshing) {
                        viewDataBinding.view.visibility = View.INVISIBLE
                        viewDataBinding.progressBar.visibility = View.VISIBLE
                    }
                }
                Status.ERROR -> {
                    viewDataBinding.swipeRefresh.isRefreshing = false
                    viewDataBinding.progressBar.visibility = View.GONE

                    showToast(it.message)
                }
                Status.SUCCESS -> {
                    viewDataBinding.swipeRefresh.isRefreshing = false
                    viewDataBinding.progressBar.visibility = View.GONE
                    viewDataBinding.view.visibility = View.VISIBLE

//                    viewDataBinding.recyclerView.adapter = it.data?.transactions?.let { items ->
//                        TransactionAdapter(items)
//                        val transactionAdapter = viewDataBinding.recyclerView.adapter as TransactionAdapter;
//                        transactionAdapter.itemClickListener = this
//                        transactionAdapter
//                    }

                    viewModel.setQuickHomeServices(it.data?.quickServices)

                    if (it?.data?.quickServices != null) {
                        viewModel.isSYGSActivated =
                            it.data.quickServices.find { q: QuickService -> q.id == Constants.SYGS } != null
                    }

                    if (it?.data?.quickServices != null) {
                        viewModel.isHFActivated =
                            it.data.quickServices.find { q: QuickService -> q.id == Constants.HF } != null
                    }

//                    viewDataBinding.rvQuickServices.adapter = it.data?.quickServices?.let { items ->
//
//                        val personalizedList = arrayListOf<QuickService>()
//                        for (item in items){
//                            if(item.enabled)
//                                personalizedList.add(item)
//                        }
//                        val adapter = QuickServiceAdapter(personalizedList)
//                        adapter.itemClickListener = this
//                        adapter
//                    }

                    if (it.data?.accounts != null) {
                        val items = it.data.accounts

                        // if user has only one account, the card will take the full width of the homepage
                        if (items.size == 1)
                            viewDataBinding.viewPager.setPadding(16, 0, 16, 0)
                        else
                            viewDataBinding.viewPager.setPadding(100, 0, 100, 0)

                        val accountPagerAdapter = AccountPagerAdapter(items)
                        accountPagerAdapter.itemClickListener = this
                        viewDataBinding.viewPager.offscreenPageLimit = 3
                        viewDataBinding.viewPager.adapter = accountPagerAdapter
                        viewDataBinding.viewPager.setPageTransformer(PageTransformer())

                        TabLayoutMediator(
                            viewDataBinding.tabLayout,
                            viewDataBinding.viewPager
                        ) { _, _ -> }.attach()
                    }
                }
            }
        })
    }

    override fun onItemClick(item: Account) {
        val intent = Intent(requireContext(), TransactionsActivity::class.java);
        intent.putExtra("accountNo", item.number)
        startActivity(intent)
    }

    override fun onItemClick(item: Transaction, view: View) {
        startActivity(
            TransactionDetailsActivity.getIntent(
                requireContext(),
                item.originalId,
                item.branchCode
            )
        )
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

            Constants.SEP -> openSEPActivity()

            Constants.RESTAURANTS -> openRestaurantsPaymentActivity()

//            Constants.BILLS -> activityNavigation.navigate(
//                Intent(
//                    requireContext(),
//                    BillsActivity::class.java
//                )
//            )

            Constants.ALPHA_CAPITAL -> activityNavigation.navigate(
                Intent(
                    requireContext(),
                    AlphaPaymentActivity::class.java
                )
            )

            Constants.ATM_CARDS -> activityNavigation.navigate(
                Intent(
                    requireContext(),
                    AtmCardsActivity::class.java
                )
            )

            Constants.SYGS -> activityNavigation.navigate(
                Intent(
                    requireContext(),
                    SYGSActivity::class.java
                )
            )

            Constants.HF -> activityNavigation.navigate(
                Intent(
                    requireContext(),
                    HFActivity::class.java
                )
            )
        }
    }

    private fun openTransferActivity() {
        activityNavigation.navigate(
            TransferActivity.getIntent(
                requireContext(),
                viewModel.isSYGSActivated,
                viewModel.isHFActivated
            )
        )
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

    private fun openSEPActivity() {
        activityNavigation.navigate(Intent(requireContext(), SEPActivity::class.java))
    }


    private fun openUniversitiesPaymentActivity() {
        activityNavigation.navigate(
            Intent(
                requireContext(),
                UniversitiesPaymentActivity::class.java
            )
        )
    }

    private fun openSchoolsPaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), SchoolsPaymentActivity::class.java))
    }

    private fun openRestaurantsPaymentActivity() {
        activityNavigation.navigate(
            Intent(
                requireContext(),
                RestaurantsPaymentActivity::class.java
            )
        )
    }

    private fun openMobilePaymentActivity() {
        activityNavigation.navigate(Intent(requireContext(), MobilePaymentActivity::class.java))
    }
}