package com.carpes.planeschase.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "planes",
    foreignKeys = [
        ForeignKey(
            entity = PlaneSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("setId")],
)
data class PlaneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val image: String,
    val description: String,
    val chaosAbility: String?,
    val typeLine: String,
    val setId: Int,
)
