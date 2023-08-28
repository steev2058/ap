package com.apps2you.albaraka.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.BR
import com.apps2you.albaraka.data.model.Transaction
import com.apps2you.albaraka.databinding.ItemTransactionBinding


class TransactionAdapter(private val items: ArrayList<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    var itemClickListener: TransactionAdapter.ItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTransactionBinding
                .inflate(LayoutInflater.from(parent.context),
                        parent,
                        false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {
        fun bindData(item: Transaction) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(items[bindingAdapterPosition], binding.root)

        }
    }

    interface ItemClickListener {
        fun onItemClick(item: Transaction, view: View)
    }
}