package com.apps2you.albaraka.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.Account
import com.apps2you.albaraka.databinding.ItemAccountBinding


class AccountAdapter : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    private val items: ArrayList<Account> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val lp = binding.root.layoutParams
        lp.height = lp.width * 2
        binding.root.layoutParams = lp

        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountAdapter.AccountViewHolder, position: Int) {
        holder.bindData(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addAll(items: List<Account>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
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
            itemClickListener?.onItemClick(items[bindingAdapterPosition], binding.iBtnMore)
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: Account, view: View)
    }
}