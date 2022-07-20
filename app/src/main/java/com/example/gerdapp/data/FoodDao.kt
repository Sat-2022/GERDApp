package com.example.gerdapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 suspend fun insert(food: Food)

 @Update
 suspend fun update(food: Food)

 @Delete
 suspend fun delete(food: Food)

 @Query("SELECT * from food WHERE id = :id")
 fun getRecord(id: Int): Flow<Food>

 @Query("SELECT * from food")
 fun getRecords(): Flow<List<Food>>

 @Query("SELECT * from food ORDER BY start_time DESC LIMIT 1")
 fun getRecent(): Flow<Food>
}