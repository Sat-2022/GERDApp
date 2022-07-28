package com.example.gerdapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result")
data class Result(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: String,
    val symptomCough: Int?,
    val symptomHeartBurn: Int?,
    val symptomAcidReflux: Int?,
    val symptomChestHurt: Int?,
    val symptomAcidMouth: Int?,
    val symptomHoarse: Int?,
    val symptomLoseAppetite: Int?,
    val symptomStomachGas: Int?,
    val symptomCoughNight: Int?,
    val symptomAcidRefluxNight: Int?
)