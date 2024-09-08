package com.apps2you.albaraka.data.model

data class FinancingTransaction(
    val CLASS_NAME_ENG: String,        // Corresponds to tvTransactionType
    val CLASS_NAME_ARAB : String,        // Corresponds to tvTransactionType
    val BRANCH_NAME_ARAB: String,
    val BRANCH_CODE: String,
    val DEAL_NO: String,
    val DEAL_DATE: String,
    val TOTAL_AMT: String,             // Corresponds to tvTotalAmount
    val TOTAL_AMT_PAID: String,        // Corresponds to tvTotalInstallments
    val REMAIN_AMT: String,
    val NO_OF_PAYMENTS: String, // Corresponds to tvNumberOfInstallments
    val AMT_PER_PAYMENT : String,
    val CURRENCY_ARAB : String,
    val CURRENCY_ENG : String ,
    val BRANCH_NAME_ENG : String,
)
