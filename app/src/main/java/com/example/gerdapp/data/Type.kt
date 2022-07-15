package com.example.gerdapp.data

import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.TypeConverter

enum class Type{
    SYMPTOMS,
    SLEEP,
    OTHERS,
    FOOD
}