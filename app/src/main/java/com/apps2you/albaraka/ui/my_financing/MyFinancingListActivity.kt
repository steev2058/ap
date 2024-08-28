package com.apps2you.albaraka.ui.my_financing

import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.ActivityMyFinancingBinding
import com.apps2you.albaraka.databinding.ActivityMyFinancingListBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.viewmodels.MyFinancingViewModel

class MyFinancingListActivity : BaseActivity<ActivityMyFinancingListBinding, MyFinancingViewModel>() {


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    override fun setupBaseObservers() {}


    override fun getLayoutId(): Int {
        return R.layout.activity_my_financing_list
    }

    override fun setViewModel(): Class<MyFinancingViewModel> {
        return MyFinancingViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, "List Financing")

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
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