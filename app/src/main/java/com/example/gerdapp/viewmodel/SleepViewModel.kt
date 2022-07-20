package com.example.gerdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gerdapp.data.Sleep
import com.example.gerdapp.data.SleepDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SleepViewModel(private val sleepDao: SleepDao): ViewModel() {

    private fun insertRecord(sleep: Sleep) {
        viewModelScope.launch {
            sleepDao.insert(sleep)
        }
    }

    fun addSleepRecord(startTime: String, endTime: String){
        val newRecord = Sleep(startTime = startTime, endTime = endTime)
        insertRecord(newRecord)
    }

    fun isEntryValid(startTime: String, endTime: String): Boolean {
        if(startTime.isBlank() || endTime.isBlank()) return false
        return true
    }
}

class SleepViewModelFactory(private val sleepDao: SleepDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SleepViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return SleepViewModel(sleepDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}