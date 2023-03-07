package com.example.gerdapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.NotificationCardItem
import com.example.gerdapp.data.TimeRecord

/**********************************************
 * The adapter for notification cards.
 **********************************************/
class NotificationCardItemAdapter(
    private val clickListener: (NotificationCardItem) -> Unit
): RecyclerView.Adapter<NotificationCardItemAdapter.NotificationViewHolder>() {

    private val items: ArrayList<NotificationCardItem> = ArrayList()

    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.card_item_title)
        val headlineView: TextView = itemView.findViewById(R.id.card_item_recent_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notification, parent, false)

        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = current.ReturnItem
        holder.headlineView.text = TimeRecord().stringToTimeRecord(current.ReturnDate).toString(1)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateNotification(notifications: List<NotificationCardItem>) {
        items.clear()
        items.addAll(notifications)
    }
}