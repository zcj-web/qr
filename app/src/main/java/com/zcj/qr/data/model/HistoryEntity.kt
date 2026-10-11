package com.zcj.qr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: String,          // "wifi"/"contact"/"url"/"phone"/"sms"/"email"/"text"/"encrypted"
    val timestamp: Long,
    val isEncrypted: Boolean = false
)

@Entity(tableName = "generate_history")
data class GenerateHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: String,
    val timestamp: Long,
    val hasEncryption: Boolean = false
)
