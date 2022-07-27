package com.example.gerdapp.viewmodel

import androidx.lifecycle.*
import com.example.gerdapp.data.Result
import com.example.gerdapp.data.ResultDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ResultViewModel(private val resultDao: ResultDao): ViewModel() {

    private fun insertRecord(result: Result) {
        viewModelScope.launch {
            resultDao.insert(result)
        }
    }

    fun getResultById(id: Int): LiveData<Result> {
        return resultDao.getRecord(id).asLiveData()
    }

    fun getFirstResult(): LiveData<Result> {
        return resultDao.getFirst().asLiveData()
    }

    fun addResultRecord(time: String, symptomCough: Int?, symptomHeartBurn: Int?, symptomAcidReflux: Int?,
        symptomChestHurt: Int?, symptomAcidMouth: Int?, symptomHoarse: Int?, symptomLoseAppetite: Int?, symptomStomachGas: Int?,
                        symptomCoughNight: Int?, symptomAcidRefluxNight: Int?
    ){
        val newRecord = Result(time = time,symptomCough = symptomCough, symptomHeartBurn = symptomHeartBurn,
            symptomAcidReflux = symptomAcidReflux, symptomChestHurt = symptomChestHurt, symptomAcidMouth = symptomAcidMouth,
            symptomHoarse = symptomHoarse, symptomLoseAppetite = symptomLoseAppetite, symptomStomachGas = symptomStomachGas,
            symptomCoughNight = symptomCoughNight, symptomAcidRefluxNight = symptomAcidRefluxNight
        )
        insertRecord(newRecord)
    }

    fun isEntryValid(time: String): Boolean {
        if(time.isBlank()) return false
        return true
    }
}

class ResultViewModelFactory(private val resultDao: ResultDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            @Suppress("UNCHECKED_LIST")
            return ResultViewModel(resultDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}