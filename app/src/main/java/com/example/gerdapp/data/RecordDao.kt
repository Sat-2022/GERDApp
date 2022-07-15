package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * from record WHERE id = :id")
    fun getRecord(id: Int): Flow<Record>

    @Query("SELECT * from record")
    fun getRecords(): Flow<List<Record>>
}