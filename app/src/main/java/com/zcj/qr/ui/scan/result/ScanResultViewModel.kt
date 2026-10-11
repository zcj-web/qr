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

    fun setContent(text: String) {
        content.value = text
        protocolType.value = ProtocolHandler.parse(text)
    }

    fun decrypt(password: String) {
        val cipher = QrProtocolParser.extractCipher(content.value ?: "")
        isDecrypting.value = true
        decryptedContent.value = null
        // Run on background thread
        Thread {
            val result = Aes256GcmCrypto.decrypt(cipher, password)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                isDecrypting.value = false
                decryptedContent.value = result
            }
        }.start()
    }
}
