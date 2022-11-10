package com.example.gerdapp.ui.chart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.*

class EventAdapter(
    private val clickListener: (EventCurrent) -> Unit
): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private val items: ArrayList<EventCurrent> = ArrayList()

    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.record_title)
        val timeView: TextView = itemView.findViewById(R.id.record_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily_record, parent, false)
        val viewHolder = EventViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = current.ActivityItem
        holder.timeView.text = TimeRecord().stringToTimeRecord(current.StartDate).toString(2)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateEventList(eventList: List<EventCurrent>) {
        items.clear()
        items.addAll(eventList)
    }
}