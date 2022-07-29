package com.example.gerdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gerdapp.data.Record
import com.example.gerdapp.data.RecordDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RecordViewModel(private val recordDao: RecordDao): ViewModel() {

    private fun insertRecord(record: Record) {
        viewModelScope.launch {
            recordDao.insert(record)
        }
    }

    fun addSymptomRecord(time: String, symptomCough: Int?, symptomHeartBurn: Int?, symptomAcidReflux: Int?,
        symptomChestHurt: Int?, symptomAcidMouth: Int?, symptomHoarse: Int?, symptomLoseAppetite: Int?, symptomStomachGas: Int?,
                         otherSymptoms: String?
    ){
        val newRecord = Record(time = time,symptomCough = symptomCough, symptomHeartBurn = symptomHeartBurn,
            symptomAcidReflux = symptomAcidReflux, symptomChestHurt = symptomChestHurt, symptomAcidMouth = symptomAcidMouth,
            symptomHoarse = symptomHoarse, symptomLoseAppetite = symptomLoseAppetite, symptomStomachGas = symptomStomachGas,
            othersSymptoms = otherSymptoms
        )
        insertRecord(newRecord)
    }

    fun isEntryValid(time: String): Boolean {
        if(time.isBlank()) return false
        return true
    }
}

class RecordViewModelFactory(private val recordDao: RecordDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return RecordViewModel(recordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}