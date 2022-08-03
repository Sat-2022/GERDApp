package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drug: Drug)

    @Update
    suspend fun update(drug: Drug)

    @Delete
    suspend fun delete(drug: Drug)

    @Query("SELECT * from drug WHERE id = :id")
    fun getRecord(id: Int): Flow<Drug>

    @Query("SELECT * from drug")
    fun getRecords(): Flow<List<Drug>>

    @Query("SELECT * from drug ORDER BY start_time DESC LIMIT 1")
    fun getRecent(): Flow<Drug>
}