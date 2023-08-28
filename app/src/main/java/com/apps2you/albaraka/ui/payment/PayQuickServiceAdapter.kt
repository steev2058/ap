package com.apps2you.albaraka.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.databinding.ItemPaymentQuickServiceBinding

class PayQuickServiceAdapter(private val items: ArrayList<QuickService>)
    : RecyclerView.Adapter<PayQuickServiceAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPaymentQuickServiceBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemPaymentQuickServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: QuickService) {
            binding.item = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { itemClickListener?.onItemClick(items[bindingAdapterPosition]) }
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: QuickService)
    }
}