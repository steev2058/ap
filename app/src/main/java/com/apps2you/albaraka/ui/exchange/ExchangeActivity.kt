package com.apps2you.albaraka.ui.exchange

import android.app.Dialog
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.ExchangeRate
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.ActivityExchangeBinding
import com.apps2you.albaraka.databinding.DialogCalculatorBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.utils.BindingUtils
import com.apps2you.albaraka.utils.NumberTextWatcher
import com.apps2you.albaraka.utils.text.TextUtils
import com.apps2you.albaraka.viewmodels.ExchangeViewModel
import java.math.BigDecimal

class ExchangeActivity : BaseActivity<ActivityExchangeBinding, ExchangeViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_exchange
    }

    override fun setViewModel(): Class<ExchangeViewModel> {
        return ExchangeViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(viewDataBinding.toolbar, getString(R.string.guest_rate))
        viewDataBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    viewModel.exchangeRatesList?.let { list ->
                        val currencies = list.filter { exchangeRate ->
                            val symbol = exchangeRate.currency?.symbol ?: ""
                            symbol.contains(text.trim())
                        }
                        val adapter = viewDataBinding.recyclerView.adapter
                        if (adapter is ExchangeAdapter && currencies is ArrayList<ExchangeRate>) {
                            adapter.refreshList(currencies)
                        }
                    }
                }
                return false
            }

        })

        viewDataBinding.calculatorIv.setOnClickListener { showExchangeCalculator() }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun fetchData() {

    }

    override fun listenToVariables() {
        viewDataBinding.calculatorIv.visibility = View.GONE
        viewModel.exchangeRates.observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    viewDataBinding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    viewDataBinding.progressBar.visibility = View.GONE
                    viewDataBinding.calculatorIv.visibility = View.VISIBLE

                    viewDataBinding.recyclerView.adapter = it.data?.let { data ->

                        data.maxDate.let { maxDate ->
                            viewModel.lastUpdate.set(maxDate)
                        }
                        data.exchangeRates.let { items ->
                            viewModel.exchangeRatesList = items
                            ExchangeAdapter(items)
                        }
                    }
                }
                Status.ERROR -> {
                    viewDataBinding.progressBar.visibility = View.GONE

                    showToast(it.message)
                }
                else -> {
                }
            }
        })
    }

    private fun showExchangeCalculator() {
        val dialog = Dialog(this)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        val dialogDataBinding: DialogCalculatorBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_calculator,
                null,
                false)
        dialog.setContentView(dialogDataBinding.root)
        dialog.setCancelable(true)

        //add data to spinner
        dialogDataBinding.currenciesSpinner.adapter = CurrenciesSpinnerAdapter(viewModel.exchangeRatesList)

        dialog.show()

        dialogDataBinding.fromEt.addTextChangedListener(NumberTextWatcher(dialogDataBinding.fromEt))

        dialogDataBinding.buttonSubmit.setOnClickListener {
            //calculate and show result
            if (dialogDataBinding.fromEt.text.isEmpty())//clear the to_textView content
                dialogDataBinding.toEt.setText("")
            else {
                val selectedItem: ExchangeRate = dialogDataBinding.currenciesSpinner.selectedItem as ExchangeRate
                val input = TextUtils.toEnglishNumber(dialogDataBinding.fromEt.text.toString())
                BindingUtils.setFormattedNum(dialogDataBinding.toEt, BigDecimal(selectedItem.middleRate * input.toDouble()))
            }
        }
    }
}