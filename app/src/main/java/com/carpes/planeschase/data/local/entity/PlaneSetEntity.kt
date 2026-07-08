package com.carpes.planeschase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plane_sets")
data class PlaneSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val code: String,
    val releaseYear: Int,
    val image: String,
)
