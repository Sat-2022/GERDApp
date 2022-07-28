package com.example.gerdapp.viewmodel

import androidx.lifecycle.*
import com.example.gerdapp.data.Food
import com.example.gerdapp.data.FoodDao
import com.example.gerdapp.data.Sleep
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FoodViewModel(private val foodDao: FoodDao): ViewModel() {

    private fun insertRecord(food: Food) {
        viewModelScope.launch {
            foodDao.insert(food)
        }
    }

    fun addFoodRecord(startTime: String, endTime: String, food: String){
        val newRecord = Food(startTime = startTime, endTime = endTime, food = food)
        insertRecord(newRecord)
    }

    fun isEntryValid(startTime: String, endTime: String, food: String): Boolean {
        if(startTime.isBlank() || endTime.isBlank() || food.isBlank()) return false
        return true
    }

    fun getRecentRecord(): LiveData<Food> = foodDao.getRecent().asLiveData()
}

class FoodViewModelFactory(private val foodDao: FoodDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return FoodViewModel(foodDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}