package com.apps2you.albaraka.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.Account
import com.apps2you.albaraka.databinding.ItemAccountBinding


class AccountPagerAdapter(private val items: List<Account>) : RecyclerView.Adapter<AccountPagerAdapter.AccountViewHolder>() {

    var itemClickListener: ItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.iBtnMore.visibility = View.GONE
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bindData(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class AccountViewHolder internal constructor(private val binding: ItemAccountBinding)
        : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

        internal fun bindData(item: Account, position: Int) {
            binding.item = item
            binding.position = position
            binding.executePendingBindings()

            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(items[bindingAdapterPosition])
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: Account)
    }
}