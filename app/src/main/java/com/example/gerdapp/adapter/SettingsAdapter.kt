package com.example.gerdapp.adapter

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.BasicApplication
import com.example.gerdapp.R
import com.example.gerdapp.data.SettingsItem

class SettingsAdapter(
    private val clickListener: (SettingsItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>(){

    private var dataset: List<SettingsItem> = listOf(
        SettingsItem(1, R.string.text_size_title),
        SettingsItem(2, R.string.reminder_on_off_title),
        SettingsItem(3, R.string.version_title)
    )

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
//        Log.e("", holder.nameView.context.getString(dataset[1].description))
//        Log.e("", dataset.toString())

        val preferences: SharedPreferences = holder.nameView.context.getSharedPreferences("config", 0)
        val editor: SharedPreferences.Editor = preferences.edit()

        holder.nameView.text = holder.nameView.context.getString(item.title)
        when(item.id) {
            1 -> {
                when(preferences.getString("textSize", "small")) {
                    "large" -> holder.descriptionView.text = holder.nameView.context.getString(R.string.text_size_large)
                    "medium" -> holder.descriptionView.text = holder.nameView.context.getString(R.string.text_size_medium)
                    "small" -> holder.descriptionView.text = holder.nameView.context.getString(R.string.text_size_small)
                    else -> holder.descriptionView.text = holder.nameView.context.getString(R.string.text_size_small)
                }
            }
            2 -> {
                when(preferences.getString("reminder", "on")) {
                    "on" -> holder.descriptionView.text = holder.nameView.context.getString(R.string.reminder_on)
                    "off" -> holder.descriptionView.text = holder.nameView.context.getString(R.string.reminder_off)
                    else -> holder.descriptionView.text = holder.nameView.context.getString(R.string.reminder_on)
                }
            }
            3 -> {
                holder.descriptionView.text = holder.nameView.context.getString(R.string.version_number)
            }
        }
        if(position == dataset.size-1) holder.divider.visibility = View.GONE

        holder.itemView.setOnClickListener{
            when(item.id) {
                1 -> {
                    when(preferences.getString("textSize", "small")) {
                        "large" -> {
                            editor.putString("textSize", "small")
                            editor.apply()
                        }
                        "medium" -> {
                            editor.putString("textSize", "large")
                            editor.apply()
                        }
                        "small" -> {
                            editor.putString("textSize", "medium")
                            editor.apply()
                        }
                        else -> {
                            editor.putString("textSize", "small")
                            editor.apply()
                        }
                    }
                    notifyItemChanged(position)
                }
                2 -> {
                    when(preferences.getString("reminder", "on")) {
                        "on" -> {
                            editor.putString("reminder", "off")
                            editor.apply()
                            BasicApplication.RemindersManager.notificationOff(holder.nameView.context)
                        }
                        else -> {
                            editor.putString("reminder", "on")
                            editor.apply()
                            BasicApplication.RemindersManager.notificationOn(holder.nameView.context)
                        }
                    }
                    notifyItemChanged(position)
                }
            }
            clickListener(item)
        }
    }
}