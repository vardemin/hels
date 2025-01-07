package com.vardemin.hels.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vardemin.hels.data.db.entity.LiteRequestEntity
import com.vardemin.hels.data.db.entity.RequestEntity

@Dao
internal interface RequestsDao {
    @Query("SELECT * FROM lite_requests WHERE sessionId = :sessionId ORDER BY time DESC LIMIT :limit")
    suspend fun getSessionRequests(sessionId: String, limit: Int): List<LiteRequestEntity>

    @Query("SELECT * FROM lite_requests WHERE sessionId = :sessionId AND time < :last ORDER BY time DESC LIMIT :limit")
    suspend fun getSessionRequestsAfter(sessionId: String, limit: Int, last: Long): List<LiteRequestEntity>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getRequestById(id: String): RequestEntity?

    @Query("SELECT COUNT(id) FROM requests WHERE sessionId = :sessionId")
    suspend fun getSessionRequestsCount(sessionId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(vararg requests: RequestEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRequests(vararg requests: RequestEntity)

    @Query("DELETE FROM requests WHERE id=:id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun deleteRequests(vararg requests: RequestEntity)

    @Query("DELETE FROM requests WHERE sessionId = :sessionId")
    suspend fun clearRequestsForSession(sessionId: String)

    @Query("DELETE FROM requests")
    suspend fun clearAll()
}