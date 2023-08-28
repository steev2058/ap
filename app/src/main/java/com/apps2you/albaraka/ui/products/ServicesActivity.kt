package com.apps2you.albaraka.ui.products

import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.ProductCategory
import com.apps2you.albaraka.data.model.ProductService
import com.apps2you.albaraka.data.remote.networkUtils.Status
import com.apps2you.albaraka.databinding.ActivityServicesBinding
import com.apps2you.albaraka.ui.base.BaseActivity
import com.apps2you.albaraka.utils.cryptography.PRODUCT_DETAILS_EXTRA
import com.apps2you.albaraka.utils.cryptography.PRODUCT_DETAILS_TITLE_EXTRA
import com.apps2you.albaraka.viewmodels.ProductsViewModel

class ServicesActivity : BaseActivity<ActivityServicesBinding, ProductsViewModel>(),
        ProductCategoriesAdapter.ItemClickListener,
        ProductServicesAdapter.ItemClickListener {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_services
    }

    override fun setViewModel(): Class<ProductsViewModel> {
        return ProductsViewModel::class.java
    }

    override fun setUpView() {
        setToolbarTitle(viewDataBinding.toolbar, getString(R.string.more_services))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun fetchData() {
        viewModel.getProductsCategories().observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    viewDataBinding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    viewDataBinding.progressBar.visibility = View.GONE

                    showToast(it.message)
                }
                Status.SUCCESS -> {
                    viewDataBinding.progressBar.visibility = View.GONE

                    viewDataBinding.rvCategories.adapter = it?.data?.let { items ->
                        val categoriesAdapter = ProductCategoriesAdapter(items)
                        categoriesAdapter.itemClickListener = this

                        if (items.isNotEmpty()) {
                            viewDataBinding.rvServices.adapter = items.first().let { category ->
                                viewModel.selectedProductDetailTitle.set(category.name)
                                val servicesAdapter = ProductServicesAdapter(category.services)
                                servicesAdapter.itemClickListener = this
                                servicesAdapter
                            }
                        }

                        categoriesAdapter
                    }
                }
            }
        })
    }

    override fun listenToVariables() {

    }

    override fun onItemClick(item: ProductCategory) {
        viewDataBinding.rvServices.adapter = item.let {
            viewModel.selectedProductDetailTitle.set(it.name)
            val adapter = ProductServicesAdapter(it.services)
            adapter.itemClickListener = this
            adapter
        }
    }

    override fun onItemClick(item: ProductService) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(PRODUCT_DETAILS_TITLE_EXTRA, viewModel.selectedProductDetailTitle.get())
        intent.putExtra(PRODUCT_DETAILS_EXTRA, item)
        startActivity(intent)
    }

}