package com.example.gerdapp

import android.app.Application
import com.example.gerdapp.data.*

class BasicApplication: Application() {
    val recordDatabase: RecordDatabase by lazy { RecordDatabase.getDatabase(this) }
    val resultDatabase: ResultDatabase by lazy { ResultDatabase.getDatabase(this) }
    val drugDatabase: DrugDatabase by lazy { DrugDatabase.getDatabase(this) }
}