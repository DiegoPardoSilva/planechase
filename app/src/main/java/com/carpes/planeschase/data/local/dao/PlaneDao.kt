package com.carpes.planeschase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.carpes.planeschase.data.local.entity.PlaneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(planes: List<PlaneEntity>)

    @Query("SELECT * FROM planes ORDER BY id ASC")
    fun getAllPlanes(): Flow<List<PlaneEntity>>

    @Query("SELECT * FROM planes WHERE setId IN (:setIds) ORDER BY id ASC")
    fun getPlanesBySetIds(setIds: List<Int>): Flow<List<PlaneEntity>>

    @Query("SELECT * FROM planes WHERE id = :id")
    suspend fun getPlaneById(id: Int): PlaneEntity?

    @Query("SELECT COUNT(*) FROM planes")
    suspend fun count(): Int
}
