package com.apps2you.albaraka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.data.model.FinancingTransactionDetails
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import javax.inject.Inject


class MyFinancingViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {
    private val _filteredDetails = MutableLiveData<List<FinancingTransactionDetails>>()
    val filteredDetails: LiveData<List<FinancingTransactionDetails>> = _filteredDetails

    private var allDetails: List<FinancingTransactionDetails> = listOf()
     fun getAllMyFinancing(): LiveData<Resource<ArrayList<FinancingTransaction>>> {
        return appRepository.allMyFinancing
    }

    fun getAllMyFinancingDetails(dealNo: String, branchCode: String): LiveData<Resource<ArrayList<FinancingTransactionDetails>>> {

        return appRepository.getAllMyFinancingDetails(dealNo,branchCode)
    }

    fun updateAllDetails(details: List<FinancingTransactionDetails>) {
        allDetails = details
        _filteredDetails.value = details
    }

//    fun onStatusFilterClicked(status: String) {
//        _filteredDetails.value = if (status.isEmpty()) {
//            allDetails
//        } else {
//            allDetails.filter { it.LINE_STATUS == status }
//        }
//    }
fun onStatusFilterClicked(status: String) {
    _filteredDetails.value = when (status) {
        "الجميع" -> allDetails // Show all data if "الجميع" is selected
        else -> allDetails.filter { it.LINE_STATUS == status }
    }
}
}
