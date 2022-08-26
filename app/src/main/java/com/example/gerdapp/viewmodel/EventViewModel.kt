package com.example.gerdapp.viewmodel

import androidx.lifecycle.*
import com.example.gerdapp.data.Event
import com.example.gerdapp.data.EventDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class EventViewModel(private val eventDao: EventDao): ViewModel() {

    private fun insertRecord(event: Event) {
        viewModelScope.launch {
            eventDao.insert(event)
        }
    }

    fun addOthersRecord(startTime: String, endTime: String, event: String){
        val newRecord = Event(startTime = startTime, endTime = endTime, others = event)
        insertRecord(newRecord)
    }

    fun isEntryValid(startTime: String, endTime: String, event: String): Boolean {
        if(startTime.isBlank() || endTime.isBlank() || event.isBlank()) return false
        return true
    }

    fun getRecentRecord(): LiveData<Event> = eventDao.getRecent().asLiveData()
}

class EventViewModelFactory(private val eventDao: EventDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return EventViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}