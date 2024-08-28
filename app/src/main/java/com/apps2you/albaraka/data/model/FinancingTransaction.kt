package com.apps2you.albaraka.data.model

data class FinancingTransaction(
    val transactionType: String,
    val totalAmount: String,
    val totalInstallments: String,
    val installmentValue: String,
    val numberOfInstallments: String
)
