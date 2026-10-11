package com.zcj.qr.ui.history

import androidx.lifecycle.ViewModel
import com.zcj.qr.data.repository.HistoryRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    val scanHistory = repository.scanHistory
    val generateHistory = repository.generateHistory

    fun deleteScan(id: Long) {
        viewModelScope.launch { repository.deleteScan(id) }
    }
}
