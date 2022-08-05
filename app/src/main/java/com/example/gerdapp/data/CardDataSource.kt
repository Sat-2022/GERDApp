package com.example.gerdapp.data

import android.graphics.Color
import com.example.gerdapp.R
import com.example.gerdapp.viewmodel.*

class CardDataSource {
    fun loadCards(): List<CardItem> {
        return listOf(
            CardItem(R.string.symptoms, R.drawable.ic_baseline_eco_24, Color.parseColor("#4DD694")),
            CardItem(R.string.medicine, R.drawable.ic_baseline_healing_24, Color.parseColor("#F12B2B")),
            CardItem(R.string.sleep, R.drawable.ic_baseline_bedtime_24, Color.parseColor("#0842A0")),
            CardItem(R.string.food, R.drawable.ic_baseline_restaurant_24, Color.parseColor("#09ADEA")),
            CardItem(R.string.others, R.drawable.ic_baseline_accessibility_new_24, Color.parseColor("#F5A61D")),
        )
    }
}