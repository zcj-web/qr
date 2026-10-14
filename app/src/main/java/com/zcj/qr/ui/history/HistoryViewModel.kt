package com.zcj.qr.ui.history

import androidx.lifecycle.ViewModel
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repository = HistoryRepository(QrDatabase.getDatabase(com.zcj.qr.QrApp.context))
    val scanHistory = repository.scanHistory
    val generateHistory = repository.generateHistory

    fun deleteScan(id: Long) {
        viewModelScope.launch { repository.deleteScan(id) }
    }

    fun deleteGenerate(id: Long) {
        viewModelScope.launch { repository.deleteGenerate(id) }
    }
}
