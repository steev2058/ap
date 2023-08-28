package com.apps2you.albaraka.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.Account
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentAccountsBinding
import com.apps2you.albaraka.ui.base.BaseFragment
import com.apps2you.albaraka.ui.common.busEvent.RefreshAccountsEvent
import com.apps2you.albaraka.ui.transactions.TransactionsActivity
import com.apps2you.albaraka.ui.transfer.accountsTransfer.TransferActivity
import com.apps2you.albaraka.utils.Constants
import com.apps2you.albaraka.utils.bus.Bus
import com.apps2you.albaraka.utils.bus.EventBusObserver
import com.apps2you.albaraka.viewmodels.AccountVM


class AccountsFragment : BaseFragment<FragmentAccountsBinding, AccountVM>(),
        AccountAdapter.ItemClickListener {

    private val adapter = AccountAdapter()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_accounts
    }

    override fun setViewModel(): Class<AccountVM> {
        return AccountVM::class.java
    }

    override fun setUpView() {
        adapter.itemClickListener = this
        viewDataBinding.recyclerView.adapter = adapter

        viewDataBinding.swipeRefresh.setOnRefreshListener { fetchData() }
    }

    override fun fetchData() {
        viewModel.homeData.observe(this, {
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

                    adapter.clear()
                    it?.data?.accounts?.let { arrayList -> adapter.addAll(arrayList) }

                    if (it?.data?.quickServices != null) {
                        viewModel.isSYGSActivated = it.data.quickServices.find { q: QuickService -> q.id == Constants.SYGS } != null
                    }
                }
            }
        })
    }

    override fun onItemClick(item: Account, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_account_options)

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                popupMenu.setForceShowIcon(true)
        } catch (e: NoSuchMethodError) { // it crashes on some devices
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_statement -> {
                    val intent = Intent(requireContext(), TransactionsActivity::class.java);
                    intent.putExtra("accountNo", item.number)
                    startActivity(intent)
                }
                R.id.menu_transfer -> {
                    activityNavigation.navigate(
                            TransferActivity.getIntent(requireContext(), viewModel.isSYGSActivated, item.number)
                    )
                }
            }
            true
        }

        popupMenu.show()
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
}