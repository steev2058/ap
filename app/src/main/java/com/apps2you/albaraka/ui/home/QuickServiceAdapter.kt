package com.apps2you.albaraka.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.QuickService
import com.apps2you.albaraka.databinding.ItemQuickServiceBinding

class QuickServiceAdapter(private val items: ArrayList<QuickService>)
    : RecyclerView.Adapter<QuickServiceAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemQuickServiceBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemQuickServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: QuickService) {
            binding.item = item
            binding.executePendingBindings()

            binding.btnService.setOnClickListener { itemClickListener?.onItemClick(items[bindingAdapterPosition]) }
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: QuickService)
    }
}