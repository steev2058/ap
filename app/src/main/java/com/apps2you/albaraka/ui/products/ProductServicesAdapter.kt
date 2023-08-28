package com.apps2you.albaraka.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.ProductService
import com.apps2you.albaraka.databinding.ItemProductServiceBinding

class ProductServicesAdapter(private val items: ArrayList<ProductService>)
    : RecyclerView.Adapter<ProductServicesAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductServiceBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemProductServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: ProductService) {
            binding.item = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { itemClickListener?.onItemClick(items[bindingAdapterPosition]) }
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: ProductService)
    }
}