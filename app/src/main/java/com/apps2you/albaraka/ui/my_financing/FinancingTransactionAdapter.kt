package com.apps2you.albaraka.ui.my_financing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingListFragment

class FinancingTransactionAdapter(
    private val fragment: MyFinancingListFragment, // Pass the fragment instead of context
    private val transactions: List<FinancingTransaction>,
    private val itemClickListener: (FinancingTransaction) -> Unit
) : ArrayAdapter<FinancingTransaction>(fragment.requireContext(), 0, transactions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val transaction = getItem(position)
        val view = convertView ?: LayoutInflater.from(fragment.requireContext()).inflate(R.layout.item_financing_transaction, parent, false)


        view.findViewById<TextView>(R.id.tvTransactionType).text = transaction?.CLASS_NAME_ARAB
        view.findViewById<TextView>(R.id.tvTotalAmount2).text = transaction?.TOTAL_AMT
        view.findViewById<TextView>(R.id.tvTotalInstallments2).text = transaction?.TOTAL_AMT_PAID
        view.findViewById<TextView>(R.id.tvInstallmentValue2).text = transaction?.AMT_PER_PAYMENT // Assuming you meant this
        view.findViewById<TextView>(R.id.tvNumberOfInstallments2).text = transaction?.NO_OF_PAYMENTS
        view.findViewById<TextView>(R.id.tv_currency_name2).text = transaction?.CURRENCY_ARAB

        view.setOnClickListener {
            transaction?.let { itemClickListener(it) }
        }

        return view
    }


}
