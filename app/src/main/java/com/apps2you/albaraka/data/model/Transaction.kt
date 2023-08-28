package com.apps2you.albaraka.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName

import  java.io.Serializable;

class Transaction(
        @Bindable @SerializedName("id") val id: Int, // the transaction id at Tradinos backend
        @Bindable @SerializedName("od_trans_no") val originalId: Int?, // the transaction id at alBaraka backend
        @Bindable @SerializedName("code") val code: Int?, // =originalId // the transaction id at alBaraka backend

        @Bindable @SerializedName("operation_name") val operationName: String?,
        @Bindable @SerializedName("my_account_name") val accountName: String, // my account name from/to which the transaction was done
        @Bindable @SerializedName("my_account_number") val accountNumber: String, // my account number from/to which the transaction was done

        @Bindable @SerializedName("from_acc") val fromAccountNumber: String,
        @Bindable @SerializedName("to_acc") val toAccountNumber: String,
        @Bindable @SerializedName("from_cus") val fromCustomer: String,
        @Bindable @SerializedName("to_cus") val toCustomer: String,

        @Bindable @SerializedName("full_amount_description") val displayedAmount: String?, // amount concatenated with +/- sign and currency symbol in transactions list
        @Bindable @SerializedName("full_charge_description") val charge: String?,
        @Bindable @SerializedName("full_total_description") val total: String?,
        @Bindable @SerializedName("amount_in_words") val amountWords: String, // numbers in words with "Only" at the end
        @Bindable @SerializedName("branch") val branch: String,
        @Bindable @SerializedName("created_at") val date: String,
        @Bindable @SerializedName("is_sender") val isSender: Boolean,
        @Bindable @SerializedName("desc") val description: String,
        @Bindable @SerializedName("branch_code") val branchCode: String?,
) : Serializable, BaseObservable()