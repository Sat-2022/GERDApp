package com.example.gerdapp.ui.chart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.*

class SleepAdapter(
    private val clickListener: (SleepCurrent) -> Unit
): RecyclerView.Adapter<SleepAdapter.SleepViewHolder>() {
    private val items: ArrayList<SleepCurrent> = ArrayList()

    class SleepViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.record_title)
        val timeView: TextView = itemView.findViewById(R.id.record_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily_record, parent, false)
        val viewHolder = SleepViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = holder.titleView.context.getString(R.string.sleep)
        holder.timeView.text = TimeRecord().stringToTimeRecord(current.StartDate).toString(2)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateSleepList(sleepList: List<SleepCurrent>) {
        items.clear()
        items.addAll(sleepList)
    }
}