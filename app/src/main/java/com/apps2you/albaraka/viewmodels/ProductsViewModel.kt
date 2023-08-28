package com.apps2you.albaraka.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.apps2you.albaraka.data.model.ProductCategory
import com.apps2you.albaraka.data.model.ProductService
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject

class ProductsViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {

    var selectedProductDetailTitle: ObservableField<String> = ObservableField("")
    var details: ObservableField<ProductService> = ObservableField()

    fun getProductsCategories(): LiveData<Resource<ArrayList<ProductCategory>>> =
            appRepository.productsCategories
}