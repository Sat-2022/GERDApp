package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OthersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(others: Others)

    @Update
    suspend fun update(others: Others)

    @Delete
    suspend fun delete(others: Others)

    @Query("SELECT * from others WHERE id = :id")
    fun getRecord(id: Int): Flow<Others>

    @Query("SELECT * from others")
    fun getRecords(): Flow<List<Others>>

    @Query("SELECT * from others ORDER BY start_time DESC LIMIT 1")
    fun getRecent(): Flow<Others>
}