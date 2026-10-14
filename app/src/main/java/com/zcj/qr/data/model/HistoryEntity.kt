package com.zcj.qr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: String,
    val timestamp: Long,
    val isEncrypted: Boolean = false
)

@Entity(tableName = "generate_history")
data class GenerateHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: String,
    val timestamp: Long,
    val hasEncryption: Boolean = false,
    val logoFilePath: String? = null,
    // 样式 JSON：包含 dot_shape(0), dot_color, bg_color
    val styleJson: String? = null
)
