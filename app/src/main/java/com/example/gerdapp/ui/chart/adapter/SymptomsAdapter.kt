package com.example.gerdapp.ui.chart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.SymptomCurrent
import com.example.gerdapp.data.TimeRecord

class SymptomsAdapter(
    private val clickListener: (SymptomCurrent) -> Unit
): RecyclerView.Adapter<SymptomsAdapter.SymptomsViewHolder>() {
    private val items: ArrayList<SymptomCurrent> = ArrayList()

    class SymptomsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.record_title)
        val timeView: TextView = itemView.findViewById(R.id.record_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily_record, parent, false)
        val viewHolder = SymptomsViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = current.symptomToString()
        holder.timeView.text = TimeRecord().stringToTimeRecord(current.StartDate).toString(2)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateSymptomList(symptomList: List<SymptomCurrent>) {
        items.clear()
        items.addAll(symptomList)
    }
}