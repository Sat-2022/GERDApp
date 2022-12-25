package com.example.gerdapp.ui.chart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.*

class FoodAdapter(
    private val clickListener: (FoodCurrent) -> Unit
): RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private val items: ArrayList<FoodCurrent> = ArrayList()

    class FoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.record_title)
        val timeView: TextView = itemView.findViewById(R.id.record_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily_record, parent, false)
        val viewHolder = FoodViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val current = items[position]
        holder.titleView.text = current.FoodItem
        holder.timeView.text = TimeRecord().stringToTimeRecord(current.StartDate).toString(2)

        holder.itemView.setOnClickListener {
            clickListener(current)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateFoodList(foodList: List<FoodCurrent>) {
        items.clear()
        items.addAll(foodList)
    }
}