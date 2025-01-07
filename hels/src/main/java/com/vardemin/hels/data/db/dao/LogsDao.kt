package com.vardemin.hels.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vardemin.hels.data.db.entity.LogItemEntity

@Dao
internal interface LogsDao {

    @Query("SELECT * FROM logs WHERE sessionId = :sessionId ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getSessionLogs(sessionId: String, limit: Int): List<LogItemEntity>

    @Query("SELECT * FROM logs WHERE sessionId = :sessionId AND dateTime < :last ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getSessionLogsAfter(sessionId: String, limit: Int, last: Long): List<LogItemEntity>

    @Query("SELECT * FROM logs WHERE id = :id")
    suspend fun getLogById(id: String): LogItemEntity?

    @Query("SELECT COUNT(id) FROM logs WHERE sessionId = :sessionId")
    suspend fun getSessionLogsCount(sessionId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(vararg logs: LogItemEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLog(log: LogItemEntity)

    @Delete
    suspend fun deleteLogs(vararg logs: LogItemEntity)

    @Query("DELETE FROM logs WHERE id=:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM logs WHERE sessionId = :sessionId")
    suspend fun clearLogsForSession(sessionId: String)

    @Query("DELETE FROM logs")
    suspend fun clearAll()
}