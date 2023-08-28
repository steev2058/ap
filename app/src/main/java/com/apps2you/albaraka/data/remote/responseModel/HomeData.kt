package com.apps2you.albaraka.data.remote.responseModel

import com.apps2you.albaraka.data.model.Account
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.data.model.Transaction
import com.google.gson.annotations.SerializedName


class HomeData(@SerializedName("transaction") val transactions: ArrayList<Transaction>,
               @SerializedName("accounts") val accounts: ArrayList<Account>,
               @SerializedName("quick_services") val quickServices: ArrayList<QuickService>)