package com.carpes.planeschase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Entity(tableName = "deck_plane_cross_ref", primaryKeys = ["deckId", "planeId"])
data class DeckPlaneCrossRef(
    val deckId: Int,
    val planeId: Int
)
