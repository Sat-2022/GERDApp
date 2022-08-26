package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * from event WHERE id = :id")
    fun getRecord(id: Int): Flow<Event>

    @Query("SELECT * from event")
    fun getRecords(): Flow<List<Event>>

    @Query("SELECT * from event ORDER BY start_time DESC LIMIT 1")
    fun getRecent(): Flow<Event>
}