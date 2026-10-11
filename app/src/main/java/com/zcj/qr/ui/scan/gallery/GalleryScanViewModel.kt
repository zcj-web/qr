package com.zcj.qr.ui.scan.gallery

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.zcj.qr.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class GalleryScanViewModel(
    private val context: Context,
    private val repository: HistoryRepository
) : ViewModel() {

    val scannedResult = MutableLiveData<String?>()
    val showError = MutableLiveData<String?>()

    fun scanImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = inputStream?.let { BitmapFactory.decodeStream(it) } ?: run {
                    withContext(Dispatchers.Main) { showError.value = "无法加载图片" }
                    return@launch
                }
                val luminance = IntArray(bitmap.width * bitmap.height)
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                for (i in pixels.indices) {
                    val rgb = pixels[i]
                    val r = (rgb shr 16) and 0xFF
                    val g = (rgb shr 8) and 0xFF
                    val b = rgb and 0xFF
                    luminance[i] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                }
                val source = com.google.zxing.RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                val reader = MultiFormatReader()
                val result = reader.decode(binaryBitmap)
                withContext(Dispatchers.Main) {
                    scannedResult.value = result.text
                    val type = com.zcj.qr.util.ProtocolHandler.parse(result.text).let { t ->
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
                    val isEncrypted = result.text.startsWith("qr://")
                    repository.addScan(result.text, type, isEncrypted)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError.value = "未能识别二维码，请确保图片清晰"
                }
            }
        }
    }
}
