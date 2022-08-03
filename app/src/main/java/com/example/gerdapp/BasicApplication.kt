package com.example.gerdapp

import android.app.Application
import com.example.gerdapp.data.*

class BasicApplication: Application() {
    val recordDatabase: RecordDatabase by lazy { RecordDatabase.getDatabase(this) }
    val foodDatabase: FoodDatabase by lazy { FoodDatabase.getDatabase(this) }
    val sleepDatabase: SleepDatabase by lazy { SleepDatabase.getDatabase(this) }
    val othersDatabase: OthersDatabase by lazy { OthersDatabase.getDatabase(this) }
    val resultDatabase: ResultDatabase by lazy { ResultDatabase.getDatabase(this) }
    val drugDatabase: DrugDatabase by lazy { DrugDatabase.getDatabase(this) }
}