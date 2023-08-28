package com.apps2you.albaraka.ui.atmCard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.AtmCard
import com.apps2you.albaraka.databinding.ItemAtmCardBinding


class AtmCardAdapter : RecyclerView.Adapter<AtmCardAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    private val items: ArrayList<AtmCard> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAtmCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addAll(items: List<AtmCard>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemAtmCardBinding) : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

        internal fun bindData(item: AtmCard) {
            binding.item = item
            binding.executePendingBindings()

            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(items[bindingAdapterPosition], binding.iBtnMore)
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: AtmCard, view: View)
    }
}