package com.apps2you.albaraka.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.data.model.Transaction
import com.apps2you.albaraka.databinding.ItemPdfBinding


class TransactionPdfAdapter(private val items: ArrayList<Transaction>)
    : RecyclerView.Adapter<TransactionPdfAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPdfBinding
                .inflate(LayoutInflater.from(parent.context),
                        parent,
                        false))
    }

    override fun onBindViewHolder(holder: TransactionPdfAdapter.ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemPdfBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Transaction) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }
}