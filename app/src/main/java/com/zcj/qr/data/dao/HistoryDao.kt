package com.zcj.qr.data.dao

import androidx.room.*
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.data.model.GenerateHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    fun getLatest(limit: Int): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}

@Dao
interface GenerateHistoryDao {
    @Query("SELECT * FROM generate_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<GenerateHistoryEntity>>

    @Query("SELECT * FROM generate_history ORDER BY timestamp DESC LIMIT :limit")
    fun getLatest(limit: Int): Flow<List<GenerateHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GenerateHistoryEntity): Long

    @Query("DELETE FROM generate_history WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM generate_history")
    suspend fun clearAll()
}
