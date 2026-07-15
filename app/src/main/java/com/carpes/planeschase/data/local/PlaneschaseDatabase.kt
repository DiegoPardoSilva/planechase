package com.carpes.planeschase.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carpes.planeschase.data.local.dao.DeckDao
import com.carpes.planeschase.data.local.dao.PlaneDao
import com.carpes.planeschase.data.local.dao.PlaneSetDao
import com.carpes.planeschase.data.local.entity.DeckEntity
import com.carpes.planeschase.data.local.entity.DeckPlaneCrossRef
import com.carpes.planeschase.data.local.entity.PlaneEntity
import com.carpes.planeschase.data.local.entity.PlaneSetEntity

@Database(
    entities = [PlaneSetEntity::class, PlaneEntity::class, DeckEntity::class, DeckPlaneCrossRef::class],
    version = 2,
    exportSchema = false,
)
abstract class PlaneschaseDatabase : RoomDatabase() {

    abstract fun planeDao(): PlaneDao
    abstract fun planeSetDao(): PlaneSetDao
    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var INSTANCE: PlaneschaseDatabase? = null

        fun getInstance(context: Context): PlaneschaseDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PlaneschaseDatabase::class.java,
                    "planeschase.db",
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
