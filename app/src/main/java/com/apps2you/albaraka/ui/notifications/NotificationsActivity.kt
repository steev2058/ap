package com.apps2you.albaraka.ui.notifications

import android.app.NotificationManager
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.NotificationContent
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.ActivityNotificationsBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.ui.transactions.TransactionDetailsActivity
import com.apps2you.albaraka.utils.Constants
import com.apps2you.albaraka.viewmodels.HomeViewModel
import com.apps2you.albaraka.viewmodels.NotificationsViewModel


class NotificationsActivity : BaseActivity<ActivityNotificationsBinding, NotificationsViewModel>(), NotificationsAdapter.ItemClickListener {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_notifications
    }

    override fun setViewModel(): Class<NotificationsViewModel> {
        return NotificationsViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, getString(R.string.Notifications))

        viewDataBinding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun fetchData() {

    }

    override fun listenToVariables() {
        mViewModel.getNotifications().observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    viewDataBinding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    viewDataBinding.progressBar.visibility = View.GONE

                    viewDataBinding.recyclerView.adapter = it.data?.let { items ->
                        val adapter = NotificationsAdapter(items)
                        adapter.itemClickListener = this
                        adapter
                    }

                    HomeViewModel.notificationCount.set(0)
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancelAll()
                }
                Status.ERROR -> {
                    viewDataBinding.progressBar.visibility = View.GONE

                    showToast(it.message)
                }
            }
        })
    }

    override fun onItemClick(item: NotificationContent) {
        if (item.type == Constants.NOTIFICATION_TRANSFER)
            startActivity(TransactionDetailsActivity.getIntent(this, item.originalTransactionId, item.branchCode))
    }

    override fun onMoreClick(index: Int, item: NotificationContent, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_notification_options)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_delete -> deleteNotification(index, item.id)
            }
            true
        }

        popupMenu.show()
    }

    private fun deleteNotification(index: Int, id: Int) {
        viewModel.deleteNotification(id).observe(this, {
            when (it.status) {
                Status.LOADING -> showProgress()

                Status.SUCCESS -> {
                    hideProgress()
                    showToast(it.message)
                    (viewDataBinding.recyclerView.adapter as NotificationsAdapter).removeItem(index)
                }

                Status.ERROR -> {
                    hideProgress()
                    showToast(it.message)
                }
            }
        })
    }
}