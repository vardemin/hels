package com.vardemin.hels.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vardemin.hels.data.db.entity.EventEntity

@Dao
internal interface EventsDao {
    @Query("SELECT * FROM events WHERE sessionId = :sessionId ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getSessionEvents(sessionId: String, limit: Int): List<EventEntity>

    @Query("SELECT * FROM events WHERE sessionId = :sessionId AND dateTime < :last ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getSessionEventsAfter(sessionId: String, limit: Int, last: Long): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT COUNT(id) FROM events WHERE sessionId = :sessionId")
    suspend fun getSessionEventsCount(sessionId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(vararg logs: EventEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEvent(log: EventEntity)

    @Delete
    suspend fun deleteEvents(vararg logs: EventEntity)

    @Query("DELETE FROM events WHERE id=:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM events WHERE sessionId = :sessionId")
    suspend fun clearEventsForSession(sessionId: String)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}