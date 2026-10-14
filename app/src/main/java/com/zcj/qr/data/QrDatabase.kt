package com.zcj.qr.data

import android.content.Context
import androidx.room.*
import com.zcj.qr.data.dao.GenerateHistoryDao
import com.zcj.qr.data.dao.ScanHistoryDao
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.data.model.GenerateHistoryEntity
import kotlinx.coroutines.flow.Flow

@Database(entities = [ScanHistoryEntity::class, GenerateHistoryEntity::class], version = 5, exportSchema = false)
abstract class QrDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun generateHistoryDao(): GenerateHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QrDatabase? = null

        fun getDatabase(context: Context): QrDatabase {
            synchronized(this) {
                if (INSTANCE != null) return INSTANCE!!
            }
            val instance = Room.databaseBuilder(
                context.applicationContext,
                QrDatabase::class.java,
                "qr_database"
            ).fallbackToDestructiveMigration()
            .build()
            synchronized(this) {
                if (INSTANCE == null) INSTANCE = instance
            }
            return INSTANCE!!
        }
    }
}
