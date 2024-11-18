package com.apps2you.albaraka.ui.financeForm

import android.view.MenuItem
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.ActivityFinanceBinding
import com.apps2you.albaraka.databinding.ActivityKycBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.viewmodels.FinanceFormViewModel
import com.apps2you.albaraka.viewmodels.KycViewModel

class FinanceFormActivity: BaseActivity<ActivityFinanceBinding, FinanceFormViewModel>() {


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    override fun setupBaseObservers() {}


    override fun getLayoutId(): Int {
        return R.layout.activity_finance
    }

    override fun setViewModel(): Class<FinanceFormViewModel> {
        return FinanceFormViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, getString(R.string.financing))

//        val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
    }
    override fun fetchData() {

    }

    override fun listenToVariables() {

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }




}