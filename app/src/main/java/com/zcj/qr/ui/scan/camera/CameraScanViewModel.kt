package com.zcj.qr.ui.scan.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraScanViewModel : ViewModel() {
    private val repository = HistoryRepository(QrDatabase.getDatabase(com.zcj.qr.QrApp.context))

    val scannedResult = MutableLiveData<String?>()

    fun onScanResult(content: String) {
        scannedResult.value = content
        val type = com.zcj.qr.util.ProtocolHandler.parse(content).let { t ->
            when (t) {
                is com.zcj.qr.util.ProtocolType.WIFI -> "wifi"
                is com.zcj.qr.util.ProtocolType.CONTACT -> "contact"
                is com.zcj.qr.util.ProtocolType.URL -> "url"
                is com.zcj.qr.util.ProtocolType.PHONE -> "phone"
                is com.zcj.qr.util.ProtocolType.SMS -> "sms"
                is com.zcj.qr.util.ProtocolType.EMAIL -> "email"
                is com.zcj.qr.util.ProtocolType.ENCRYPTED -> "encrypted"
                else -> "text"
            }
        }
        val isEncrypted = content.startsWith("qr://")
        CoroutineScope(Dispatchers.IO).launch {
            repository.addScan(content, type, isEncrypted)
        }
    }
}
