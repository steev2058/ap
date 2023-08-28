package com.apps2you.albaraka.ui.complaints

import android.view.MenuItem
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.ActivityComplaintBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.viewmodels.ComplaintViewModel

class ComplaintActivity : BaseActivity<ActivityComplaintBinding, ComplaintViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_complaint
    }

    override fun setViewModel(): Class<ComplaintViewModel> {
        return ComplaintViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, getString(R.string.more_complaints))

//        val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun fetchData() {

    }

    override fun listenToVariables() {

    }

}