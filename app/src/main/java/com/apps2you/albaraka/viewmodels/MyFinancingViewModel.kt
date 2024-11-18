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

    private var allDetails2: List<FinancingTransaction> = listOf()

    private val _paidInstallments = MutableLiveData<Int>()
    val paidInstallments: LiveData<Int> = _paidInstallments

    private val _remainingPremiums = MutableLiveData<Int>()
    val remainingPremiums: LiveData<Int> = _remainingPremiums

    private val _installmentValue = MutableLiveData<String>()
    val installmentValue: LiveData<String> = _installmentValue

    private val _remainingCommitment = MutableLiveData<String>()
    val remainingCommitment: LiveData<String> = _remainingCommitment


     fun getAllMyFinancing(): LiveData<Resource<ArrayList<FinancingTransaction>>> {
        return appRepository.allMyFinancing
    }

    fun getAllMyFinancingDetails(dealNo: String, branchCode: String): LiveData<Resource<ArrayList<FinancingTransactionDetails>>> {

        return appRepository.getAllMyFinancingDetails(dealNo,branchCode)
    }

    fun updateAllDetails(details: List<FinancingTransactionDetails>) {
        allDetails = details
        // Calculate the number of "مسدد"
        _paidInstallments.value = allDetails.count { it.LINE_STATUS == "مسدد" }

        // Calculate the number of "متأخر" + "غير مستحق بعد" + "مسدد جزئياً"
        _remainingPremiums.value = allDetails.count {
            it.LINE_STATUS == "متأخر" || it.LINE_STATUS == "غير مستحق بعد" || it.LINE_STATUS == "مسدد جزئياً"
        }


        _filteredDetails.value = details
    }

    fun updateAllDetails2(details: List<FinancingTransaction>) {
        allDetails2 = details


    }



    //    fun onStatusFilterClicked(status: String) {
//        _filteredDetails.value = if (status.isEmpty()) {
//            allDetails
//        } else {
//            allDetails.filter { it.LINE_STATUS == status }
//        }
//    }
fun calculateAndDisplayData(details: List<FinancingTransactionDetails>) {
    val paidInstallments = details.count { it.LINE_STATUS == "مسدد" }
    val remainingPremiums = details.count {
        it.LINE_STATUS == "متأخر" ||
                it.LINE_STATUS == "غير مستحق بعد" ||
                it.LINE_STATUS == "مسدد جزئياً"
    }

    _paidInstallments.postValue(paidInstallments)
    _remainingPremiums.postValue(remainingPremiums)
}

    fun onStatusFilterClicked(status: String) {
    _filteredDetails.value = when (status) {
        "الجميع" -> allDetails // Show all data if "الجميع" is selected
        else -> allDetails.filter { it.LINE_STATUS == status }
    }
}
}
