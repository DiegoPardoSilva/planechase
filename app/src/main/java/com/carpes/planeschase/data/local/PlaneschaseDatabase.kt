package com.carpes.planeschase.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carpes.planeschase.data.local.dao.PlaneDao
import com.carpes.planeschase.data.local.dao.PlaneSetDao
import com.carpes.planeschase.data.local.entity.PlaneEntity
import com.carpes.planeschase.data.local.entity.PlaneSetEntity

@Database(
    entities = [PlaneSetEntity::class, PlaneEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PlaneschaseDatabase : RoomDatabase() {

    abstract fun planeDao(): PlaneDao
    abstract fun planeSetDao(): PlaneSetDao

    companion object {
        @Volatile
        private var INSTANCE: PlaneschaseDatabase? = null

        fun getInstance(context: Context): PlaneschaseDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PlaneschaseDatabase::class.java,
                    "planeschase.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
