package com.apps2you.albaraka.ui.my_financing2

import android.view.MenuItem
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.databinding.ActivityMyFinancing2Binding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.viewmodels.MyFinancing2ViewModel

class MyFinancing2Activity: BaseActivity<ActivityMyFinancing2Binding, MyFinancing2ViewModel>() {


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    override fun setupBaseObservers() {}


    override fun getLayoutId(): Int {
        return R.layout.activity_my_financing2
    }

    override fun setViewModel(): Class<MyFinancing2ViewModel> {
        return MyFinancing2ViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(mViewDataBinding.toolbar, getString(R.string.my_financing))

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