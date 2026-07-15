package com.carpes.planeschase.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.carpes.planeschase.data.local.entity.DeckEntity
import com.carpes.planeschase.data.local.entity.DeckPlaneCrossRef
import com.carpes.planeschase.data.local.entity.PlaneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckPlaneCrossRef(crossRef: DeckPlaneCrossRef)

    @Delete
    suspend fun deleteDeckPlaneCrossRef(crossRef: DeckPlaneCrossRef)

    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Transaction
    @Query("SELECT * FROM planes INNER JOIN deck_plane_cross_ref ON planes.id = deck_plane_cross_ref.planeId WHERE deck_plane_cross_ref.deckId = :deckId")
    fun getPlanesInDeck(deckId: Int): Flow<List<PlaneEntity>>

    @Query("DELETE FROM deck_plane_cross_ref WHERE deckId = :deckId")
    suspend fun clearDeck(deckId: Int)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)
}
