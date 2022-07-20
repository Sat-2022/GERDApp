package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleep: Sleep)

    @Update
    suspend fun update(sleep: Sleep)

    @Delete
    suspend fun delete(sleep: Sleep)

    @Query("SELECT * from sleep WHERE id = :id")
    fun getRecord(id: Int): Flow<Sleep>

    @Query("SELECT * from sleep")
    fun getRecords(): Flow<List<Sleep>>

    @Query("SELECT * from sleep ORDER BY start_time DESC LIMIT 1")
    fun getRecent(): Flow<Sleep>
}