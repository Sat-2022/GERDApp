package com.example.gerdapp.ui.main

import com.example.gerdapp.R

class CardDataSource {
    fun loadCards(): List<CardItem> {
        return listOf(
            CardItem(R.string.questionnaire),
            CardItem(R.string.symptoms),
            CardItem(R.string.sleep),
            CardItem(R.string.food),
            CardItem(R.string.others),
            CardItem(R.string.chart)
        )
    }
}