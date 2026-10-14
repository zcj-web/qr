package com.zcj.qr.ui.scan.gallery

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.util.QrScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryScanViewModel : ViewModel() {
    private val repository = HistoryRepository(QrDatabase.getDatabase(com.zcj.qr.QrApp.context))

    val scannedResult = MutableLiveData<String?>()
    val showError = MutableLiveData<String?>()

    fun scanImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = com.zcj.qr.QrApp.context.contentResolver.openInputStream(uri)
                val bitmap = inputStream?.let { BitmapFactory.decodeStream(it) }
                    ?: run { withContext(Dispatchers.Main) { showError.value = "无法加载图片" }; return@launch }

                if (bitmap.width == 0 || bitmap.height == 0) {
                    withContext(Dispatchers.Main) { showError.value = "图片格式无效" }
                    return@launch
                }

                // 使用多策略扫描（与 ShareScanActivity 一致）
                val result = QrScanner.scanWithMultipleAttempts(bitmap)
                if (result != null) {
                    withContext(Dispatchers.Main) {
                        scannedResult.value = result
                        val type = com.zcj.qr.util.ProtocolHandler.parse(result).let { t ->
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
                        val isEncrypted = result.startsWith("qr://")
                        repository.addScan(result, type, isEncrypted)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showError.value = "未能识别二维码，请确保图片清晰且包含二维码"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError.value = "未能识别二维码，请确保图片清晰且包含二维码"
                }
            }
        }
    }
}
