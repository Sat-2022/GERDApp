package com.example.gerdapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.NotificationCardItem

class NotificationCardItemAdapter(
    private val clickListener: (NotificationCardItem) -> Unit
): RecyclerView.Adapter<NotificationCardItemAdapter.NotificationCardItemViewHolder>() {


    class NotificationCardItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.card_item_title)
        val subtitleTextView: TextView = view.findViewById(R.id.card_item_recent_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationCardItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notification, parent, false)
        return NotificationCardItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NotificationCardItemViewHolder, position: Int) {
        holder.titleTextView.text = ""
        holder.titleTextView.text = ""
    }

    override fun getItemCount(): Int {
        return 1
    }
}