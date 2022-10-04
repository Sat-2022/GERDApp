package com.example.gerdapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.SettingDataSource
import com.example.gerdapp.data.SettingsItem

class SettingsAdapter(
    private val clickListener: (SettingsItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>(){

    private val dataset = SettingDataSource().loadSettings()

    class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val nameView: TextView = view.findViewById(R.id.name)
        val descriptionView: TextView = view.findViewById(R.id.description)
        //val itemView: View = view.findViewById(R.id.settings_list_item)
        val divider: View = view.findViewById(R.id.view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_settings, parent, false)
        return SettingsViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = dataset[position]

        holder.nameView.text = holder.nameView.context.getString(item.title)
        holder.descriptionView.text = holder.nameView.context.getString(item.description)

        if(position == dataset.size-1) holder.divider.visibility = View.GONE

        holder.itemView.setOnClickListener{
            clickListener(item)
        }
    }
}