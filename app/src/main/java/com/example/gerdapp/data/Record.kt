package com.example.gerdapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "time")
    val time: String,
    val symptomCough: Int?,
    val symptomHeartBurn: Int?,
    val symptomAcidReflux: Int?,
    val symptomChestHurt: Int?,
    val symptomSourMouth: Int?,
    val symptomHoarseness: Int?,
    val symptomLoseAppetite: Int?,
    val symptomStomachGas: Int?,
    val othersSymptoms: String?
)