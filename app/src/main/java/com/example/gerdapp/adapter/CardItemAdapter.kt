package com.example.gerdapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.data.CardDataSource
import com.example.gerdapp.data.CardItem

class CardItemAdapter(
    private val clickListener: (CardItem) -> Unit,
    private val subtitle: (CardItem) -> String?
): RecyclerView.Adapter<CardItemAdapter.CardItemViewHolder>() {

    private val dataset = CardDataSource().loadCards()

    class CardItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.card_item_title)
        val imageView: ImageView = view.findViewById(R.id.card_item_icon)
        val recentTextView: TextView = view.findViewById(R.id.card_item_recent_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_card, parent, false)
        return CardItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.textView.text = holder.textView.context.getString(item.stringResourceId)
        holder.imageView.setImageDrawable(holder.imageView.context.getDrawable(item.imageResourceId))
        holder.imageView.setColorFilter(item.imageColor)

        val recentRecord = subtitle(item)

        holder.recentTextView.text = when(recentRecord) {
            "" -> "no data"
            else -> recentRecord
        }

        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount() = dataset.size
}