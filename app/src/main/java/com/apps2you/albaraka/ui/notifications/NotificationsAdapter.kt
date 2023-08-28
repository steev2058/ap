package com.apps2you.albaraka.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps2you.albaraka.data.model.NotificationContent
import com.apps2you.albaraka.databinding.ItemNotificationBinding

class NotificationsAdapter(private val items: ArrayList<NotificationContent>)
    : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotificationBinding
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

    fun removeItem(index: Int) {
        if (index >= 0 && index < items.size) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: NotificationContent) {
            binding.item = item
            binding.executePendingBindings()

            binding.root.setOnClickListener { itemClickListener?.onItemClick(items[bindingAdapterPosition]) }

            binding.iBtnMore.setOnClickListener {
                itemClickListener?.onMoreClick(bindingAdapterPosition, items[bindingAdapterPosition], binding.iBtnMore)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: NotificationContent)

        fun onMoreClick(index: Int, item: NotificationContent, view: View)
    }
}
