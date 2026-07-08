package com.carpes.planeschase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.carpes.planeschase.data.local.entity.PlaneSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaneSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sets: List<PlaneSetEntity>): List<Long>

    @Query("SELECT * FROM plane_sets ORDER BY releaseYear ASC")
    fun getAllSets(): Flow<List<PlaneSetEntity>>

    @Query("SELECT COUNT(*) FROM plane_sets")
    suspend fun count(): Int
}
