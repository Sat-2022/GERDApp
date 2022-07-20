package com.example.gerdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gerdapp.data.Others
import com.example.gerdapp.data.OthersDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class OthersViewModel(private val othersDao: OthersDao): ViewModel() {

    private fun insertRecord(others: Others) {
        viewModelScope.launch {
            othersDao.insert(others)
        }
    }

    fun addOthersRecord(startTime: String, endTime: String, others: String){
        val newRecord = Others(startTime = startTime, endTime = endTime, others = others)
        insertRecord(newRecord)
    }

    fun isEntryValid(startTime: String, endTime: String, others: String): Boolean {
        if(startTime.isBlank() || endTime.isBlank() || others.isBlank()) return false
        return true
    }
}

class OthersViewModelFactory(private val othersDao: OthersDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(OthersViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return OthersViewModel(othersDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}