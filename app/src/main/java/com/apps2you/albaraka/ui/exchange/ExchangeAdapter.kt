package com.apps2you.albaraka.ui.exchange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.ExchangeRate
import com.apps2you.albaraka.databinding.ItemExchangeRateBinding

class ExchangeAdapter(private var items: ArrayList<ExchangeRate>)
    : RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExchangeRateBinding
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

    fun refreshList(items: ArrayList<ExchangeRate>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemExchangeRateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: ExchangeRate) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}