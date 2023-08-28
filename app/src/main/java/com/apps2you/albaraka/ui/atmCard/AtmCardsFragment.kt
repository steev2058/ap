package com.apps2you.albaraka.ui.atmCard

import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.AtmCard
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.FragmentAtmCardsBinding
import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog
import com.apps2you.albaraka.viewmodels.AtmViewModel


const val RC_SELECT_LIMIT = "RC_SELECT_LIMIT"
const val BUNDLE_LIMIT = "BUNDLE_POSITION"

class AtmCardsFragment : AtmBaseFragment<FragmentAtmCardsBinding>(), AtmCardAdapter.ItemClickListener {

    private val adapter = AtmCardAdapter()


    override fun getBindingVariable(): Int {
        return 0
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_atm_cards
    }

    override fun getViewModelOwner(): ViewModelStoreOwner? {
        return activity
    }

    override fun setViewModel(): Class<AtmViewModel> {
        return AtmViewModel::class.java
    }

    override fun setUpView() {
        adapter.itemClickListener = this
        viewDataBinding.recyclerView.adapter = adapter

        viewDataBinding.swipeRefresh.setOnRefreshListener {
            adapter.clear()
            viewModel.atmData = null
            fetchData()
        }
    }

    override fun fetchData() {
        viewDataBinding.progressBar.visibility = View.GONE
        if (adapter.itemCount == 0)
            getAtmCards()

        if (viewModel.atmData == null)
            getAtmServicesData()
    }

    override fun onItemClick(item: AtmCard, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_atm_options)

        if (item.isActive)
            popupMenu.menu.findItem(R.id.option_status).title = getString(R.string.card_opposition)
        else
            popupMenu.menu.findItem(R.id.option_status).title = getString(R.string.card_opposition_withdrawal)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.option_resend -> resendPinCode(item)

                R.id.option_limit -> updateLimit(item)

                R.id.option_status -> updateStatus(item)
            }
            true
        }

        popupMenu.show()
    }

    override fun refreshList() {
        adapter.clear()
        getAtmCards()
    }

    private fun getAtmCards() {
        viewModel.atmCards.observe(this, {
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
                    it?.data?.let { arrayList -> adapter.addAll(arrayList) }
                }
            }
        })
    }

    private fun getAtmServicesData() {
        viewModel.atmServicesData.observe(this, {
            when (it.status) {
                Status.ERROR -> showToast(it.message)
                Status.SUCCESS -> {
                    viewModel.atmData = it.data
                }

                Status.LOADING -> {
                }
            }
        })
    }


    private fun resendPinCode(item: AtmCard) {
        if (viewModel.atmData == null)
            getAtmServicesData()
        else {
            showFeeDialog(getString(R.string.resend_pin_code), viewModel.atmData.resetPinFee) {
                showPinConfirmDialog {
                    viewModel.setPinCode(it)
                    viewModel.resendPinCode(item.cardNumber).observe(this@AtmCardsFragment, createUiObserve(getString(R.string.new_pin_code_sent)))
                }
            }
        }
    }

    private fun updateLimit(item: AtmCard) {
        if (viewModel.atmData == null)
            getAtmServicesData()
        else {
            viewModel.selectLimit(item.maxLimit)

            //viewModel.atmData.limits.removeAll{ it.limit == item.maxLimit };
            navController.navigate(R.id.action_atmCardsFragment_to_atmLimitsFragment)

            parentFragmentManager.setFragmentResultListener(RC_SELECT_LIMIT, this, { requestKey, bundle ->
                if (requestKey == RC_SELECT_LIMIT) {
                    showPinConfirmDialog {
                        viewModel.setPinCode(it)
                        viewModel.updateLimit(item.cardNumber, bundle.getString(BUNDLE_LIMIT))
                                .observe(this, createUiObserve(getString(R.string.card_limit_changed)))
                    }
                }
            })
        }
    }

    private fun updateStatus(item: AtmCard) {
        showPinConfirmDialog {
            viewModel.setPinCode(it)
            // this is the card's current status // status hasn't changed yet here
            val successMessage = if (item.isActive) getString(R.string.card_suspend) else getString(R.string.card_activate)
            viewModel.updateStatus(item).observe(this, createUiObserve(successMessage))
        }
    }


    private fun showPinConfirmDialog(listener: ConfirmPinDialog.PinConfirmationListener) {
        ConfirmPinDialog.show(childFragmentManager, listener)
    }

    private fun showDoneDialog(message: String?) {
        message?.let { showDialog("", it, showOk = true, cancelable = false) { } }
    }

    private fun createUiObserve(successMessage: String): Observer<Resource<String>> {
        return Observer {
            viewModel.setPinCode("")

            when (it.status) {
                Status.LOADING -> showProgress()
                Status.SUCCESS -> {
                    hideProgress()
                    showDoneDialog(successMessage)
                }
                Status.ERROR -> {
                    hideProgress()
                    showToast(it.message)
                }
            }
        }
    }

    fun interface DialogClickListener {
        fun onYesClicked()
    }
}