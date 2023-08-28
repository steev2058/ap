package com.apps2you.albaraka.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.ProductCategory
import com.apps2you.albaraka.databinding.ItemProductCategoryBinding

class ProductCategoriesAdapter(private val items: ArrayList<ProductCategory>)
    : RecyclerView.Adapter<ProductCategoriesAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductCategoryBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemProductCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: ProductCategory) {
            binding.item = item
            binding.isSelected = bindingAdapterPosition == selectedPosition
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                itemClickListener?.onItemClick(items[bindingAdapterPosition])
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: ProductCategory)
    }
}