package com.zcj.qr.ui.scan.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.zcj.qr.crypto.Aes256GcmCrypto
import com.zcj.qr.crypto.QrProtocolParser
import com.zcj.qr.util.ProtocolHandler
import com.zcj.qr.util.ProtocolType

class ScanResultViewModel : ViewModel() {
    val content = MutableLiveData<String>()
    val protocolType = MutableLiveData<ProtocolType?>(null)
    val isDecrypting = MutableLiveData(false)
    val decryptedContent = MutableLiveData<String?>(null)
    val decodeAttempted = MutableLiveData(false)

    fun setContent(text: String) {
        content.value = text
        protocolType.value = ProtocolHandler.parse(text)
    }

    fun decrypt(password: String) {
        val cipher = QrProtocolParser.extractCipher(content.value ?: "")
        isDecrypting.value = true
        decryptedContent.value = null
        decodeAttempted.value = true
        // Run on background thread
        Thread {
            val result = Aes256GcmCrypto.decrypt(cipher, password)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                isDecrypting.value = false
                if (result != null) {
                    // Re-parse decrypted content to get correct protocol type
                    val newType = ProtocolHandler.parse(result)
                    content.value = result
                    protocolType.value = newType
                    decryptedContent.value = result
                } else {
                    decryptedContent.value = null
                }
            }
        }.start()
    }
}
