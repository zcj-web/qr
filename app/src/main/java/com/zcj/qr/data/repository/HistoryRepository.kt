package com.zcj.qr.data.repository

import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.data.model.GenerateHistoryEntity
import kotlinx.coroutines.flow.Flow


class HistoryRepository constructor(private val db: QrDatabase) {
    val scanHistory: Flow<List<ScanHistoryEntity>> = db.scanHistoryDao().getAll()
    val generateHistory: Flow<List<GenerateHistoryEntity>> = db.generateHistoryDao().getAll()

    fun scanHistoryLatest(n: Int): Flow<List<ScanHistoryEntity>> = db.scanHistoryDao().getLatest(n)
    fun generateHistoryLatest(n: Int): Flow<List<GenerateHistoryEntity>> = db.generateHistoryDao().getLatest(n)

    suspend fun addScan(content: String, type: String, encrypted: Boolean = false) {
        db.scanHistoryDao().insert(ScanHistoryEntity(content = content, type = type, timestamp = System.currentTimeMillis(), isEncrypted = encrypted))
    }

    suspend fun addGenerate(content: String, type: String, encrypted: Boolean = false) {
        db.generateHistoryDao().insert(GenerateHistoryEntity(content = content, type = type, timestamp = System.currentTimeMillis(), hasEncryption = encrypted))
    }

    suspend fun deleteScan(id: Long) = db.scanHistoryDao().delete(id)
    suspend fun deleteGenerate(id: Long) = db.generateHistoryDao().delete(id)
    suspend fun clearScans() = db.scanHistoryDao().clearAll()
    suspend fun clearGenerates() = db.generateHistoryDao().clearAll()
}
