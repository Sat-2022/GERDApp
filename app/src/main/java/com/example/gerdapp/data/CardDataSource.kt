package com.example.gerdapp.data

import com.example.gerdapp.R
import com.example.gerdapp.data.CardItem

class CardDataSource {
    fun loadCards(): List<CardItem> {
        return listOf(
            CardItem(R.string.symptoms),
            CardItem(R.string.sleep),
            CardItem(R.string.food),
            CardItem(R.string.others),
        )
    }
}