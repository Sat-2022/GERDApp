package com.example.gerdapp.ui.chart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.DrugCurrent
import com.example.gerdapp.data.TimeRecord

class DrugAdapter(
    private val clickListener: (DrugCurrent) -> Unit
): RecyclerView.Adapter<DrugAdapter.DrugViewHolder>() {
    private val items: ArrayList<DrugCurrent> = ArrayList()

    class DrugViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.record_title)
        val timeView: TextView = itemView.findViewById(R.id.record_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily_record, parent, false)
        val viewHolder = DrugViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: DrugViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = current.DrugItem
        holder.timeView.text = TimeRecord().stringToTimeRecord(current.MedicationTime).toString(2)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateDrugList(drugList: List<DrugCurrent>) {
        items.clear()
        items.addAll(drugList)
    }
}