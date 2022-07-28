package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: Result)

    @Update
    suspend fun update(result: Result)

    @Delete
    suspend fun delete(result: Result)

    @Query("SELECT * from result WHERE id = :id")
    fun getRecord(id: Int): Flow<Result>

    @Query("SELECT * from result")
    fun getRecords(): Flow<List<Result>>

    @Query("SELECT * from result ORDER BY time")
    fun getRecent(): Flow<List<Result>>

    @Query("SELECT * from result WHERE id = 1")
    fun getFirst(): Flow<Result>
}