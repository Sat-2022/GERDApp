package com.example.gerdapp.ui.main

import android.content.Context
import android.speech.RecognitionListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gerdapp.R
import com.example.gerdapp.databinding.ListItemCardBinding

class CardItemAdapter(
    private val clickListener: (CardItem) -> Unit
): RecyclerView.Adapter<CardItemAdapter.CardItemViewHolder>() {

    private val dataset = CardDataSource().loadCards()

    class CardItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.card_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_card, parent, false)
        return CardItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = holder.textView.context.getString(item.stringResourceId)

        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount() = dataset.size
}