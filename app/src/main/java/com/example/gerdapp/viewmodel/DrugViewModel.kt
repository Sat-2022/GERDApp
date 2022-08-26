package com.example.gerdapp.viewmodel

import androidx.lifecycle.*
import com.example.gerdapp.data.Drug
import com.example.gerdapp.data.DrugDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DrugViewModel(private val drugDao: DrugDao): ViewModel() {

    private fun insertRecord(drug: Drug) {
        viewModelScope.launch {
            drugDao.insert(drug)
        }
    }

    fun addDrugRecord(startTime: String, endTime: String, drugName: String){
        val newRecord = Drug(startTime = startTime, endTime = endTime, drugName = drugName)
        insertRecord(newRecord)
    }

    fun isEntryValid(startTime: String, endTime: String, drugName: String): Boolean {
        if(startTime.isBlank() || endTime.isBlank() || drugName.isBlank()) return false
        return true
    }

    fun getRecentRecord(): LiveData<Drug> = drugDao.getRecent().asLiveData()
}

class DrugViewModelFactory(private val drugDao: DrugDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DrugViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return DrugViewModel(drugDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}