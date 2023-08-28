package com.apps2you.albaraka.ui.products

import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.ProductService
import com.apps2you.albaraka.databinding.ActivityProductDetailsBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.utils.cryptography.PRODUCT_DETAILS_EXTRA
import com.apps2you.albaraka.utils.cryptography.PRODUCT_DETAILS_TITLE_EXTRA
import com.apps2you.albaraka.viewmodels.ProductsViewModel
import com.google.android.material.appbar.AppBarLayout

class ProductDetailsActivity : BaseActivity<ActivityProductDetailsBinding, ProductsViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_details
    }

    override fun setViewModel(): Class<ProductsViewModel> {
        return ProductsViewModel::class.java
    }

    override fun setUpView() {
        val details = intent.getSerializableExtra(PRODUCT_DETAILS_EXTRA) as ProductService
        val title = intent.getStringExtra(PRODUCT_DETAILS_TITLE_EXTRA)
        setToolbarTitle(viewDataBinding.toolbar, "")


        viewModel.selectedProductDetailTitle.set(title)
        viewModel.details.set(details)

//        viewDataBinding.toolbarLayout.setTitle(title)


        (findViewById<AppBarLayout>(R.id.app_bar)).addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() === 0) {
                    //  Collapsed
                   viewDataBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this@ProductDetailsActivity, R.color.white))
                    viewDataBinding.toolbarLayout.title=details.name
                    viewDataBinding.toolbarLayout.title=details.name
                } else {
                    //Expanded
                    viewDataBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this@ProductDetailsActivity, android.R.color.transparent))
                    viewDataBinding.toolbarLayout.title=""
                }
            }
        })
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