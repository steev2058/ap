package com.apps2you.albaraka.viewmodels

import androidx.lifecycle.LiveData
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.data.model.FinancingTransactionDetails
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject


class MyFinancingViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {

     fun getAllMyFinancing(): LiveData<Resource<ArrayList<FinancingTransaction>>> {
        return appRepository.allMyFinancing
    }

    fun getAllMyFinancingDetails(dealNo: String, branchCode: String): LiveData<Resource<ArrayList<FinancingTransactionDetails>>> {
        return appRepository.getAllMyFinancingDetails(dealNo,branchCode)
    }


}
