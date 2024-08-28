package com.apps2you.albaraka.ui.my_financing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.FinancingTransaction
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingFragment
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingListFragment

class FinancingTransactionAdapter(
    private val fragment: MyFinancingListFragment, // Pass the fragment instead of context
    private val transactions: List<FinancingTransaction>
) : ArrayAdapter<FinancingTransaction>(fragment.requireContext(), 0, transactions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val transaction = getItem(position)
        val view = convertView ?: LayoutInflater.from(fragment.requireContext()).inflate(R.layout.item_financing_transaction, parent, false)

        view.findViewById<TextView>(R.id.tvTransactionType).text = transaction?.transactionType
        view.findViewById<TextView>(R.id.tvTotalAmount).text = transaction?.totalAmount
        view.findViewById<TextView>(R.id.tvTotalInstallments).text = transaction?.totalInstallments
        view.findViewById<TextView>(R.id.tvInstallmentValue).text = transaction?.installmentValue
        view.findViewById<TextView>(R.id.tvNumberOfInstallments).text = transaction?.numberOfInstallments

        view.setOnClickListener {
            fragment.openMyFinancingDetailsFragment()
        }

        return view
    }
}
