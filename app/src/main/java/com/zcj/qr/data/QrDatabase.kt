package com.zcj.qr.data

import android.content.Context
import androidx.room.*
import com.zcj.qr.data.dao.GenerateHistoryDao
import com.zcj.qr.data.dao.ScanHistoryDao
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.data.model.GenerateHistoryEntity
import kotlinx.coroutines.flow.Flow

@Database(entities = [ScanHistoryEntity::class, GenerateHistoryEntity::class], version = 1, exportSchema = false)
abstract class QrDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun generateHistoryDao(): GenerateHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QrDatabase? = null

        fun getDatabase(context: Context): QrDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrDatabase::class.java,
                    "qr_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
