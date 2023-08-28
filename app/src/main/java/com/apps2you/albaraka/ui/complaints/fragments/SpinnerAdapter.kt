package com.apps2you.albaraka.ui.complaints.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.Branch
import com.apps2you.albaraka.data.model.SpinnerItem
import com.apps2you.albaraka.databinding.ItemComplaintSpinnerBinding

class SpinnerAdapter<T : SpinnerItem>(var items: ArrayList<T>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder(ItemComplaintSpinnerBinding.inflate(LayoutInflater.from(parent.context),
                    parent,
                    false))
            holder.itemView.tag = holder
        } else {
            @Suppress("UNCHECKED_CAST")
            holder = convertView.tag as SpinnerAdapter<T>.ViewHolder
        }
        holder.bindData(getItem(position))
        return holder.itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_spinner_dropdown_item,
                    parent,
                    false)
            view.tag = view
        } else {
            view = convertView.tag as View
        }
        val item = getItem(position)
        val title = view.findViewById<CheckedTextView>(android.R.id.text1)
        title.text = item.getName()

        if (item is Branch) {
            @SuppressLint("SetTextI18n")
            title.text = "${item.getName()} ${item.phoneNumber}"
        }

        return view
    }

    fun positionOf(item: T): Int{
        return items.indexOf(item)
    }

    fun refreshList(items: ArrayList<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemComplaintSpinnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: T) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}