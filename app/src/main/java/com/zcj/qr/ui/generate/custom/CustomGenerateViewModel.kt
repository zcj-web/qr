package com.zcj.qr.ui.generate.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.zcj.qr.crypto.Aes256GcmCrypto
import com.zcj.qr.crypto.QrProtocolParser
import com.zcj.qr.util.QrCodeGenerator
import com.zcj.qr.util.QrStyle
import com.zcj.qr.util.DotShape

class CustomGenerateViewModel : ViewModel() {
    val generatedBitmap = MutableLiveData<android.graphics.Bitmap?>()
    val showError = MutableLiveData<String?>()

    private var currentContent: String = ""
    private var isEncrypted: Boolean = false

    fun generate(content: String, useEncryption: Boolean, password: String, iterations: Int) {
        try {
            currentContent = if (useEncryption) {
                if (password.isEmpty()) {
                    showError.value = "加密需要输入密码"
                    return
                }
                isEncrypted = true
                "qr://${Aes256GcmCrypto.encrypt(content, password, iterations)}"
            } else {
                isEncrypted = false
                content
            }

            val style = QrStyle(
                dotColor = 0xFF1E293B.toInt(),
                bgColor = 0xFFFFFFFF.toInt(),
                dotShape = DotShape.ROUND
            )
            val bitmap = QrCodeGenerator.generate(currentContent, size = 800, style = style)
            generatedBitmap.value = bitmap
        } catch (e: Exception) {
            showError.value = e.message ?: "生成失败"
        }
    }

    fun getContent(): String = currentContent
    fun isEncrypted(): Boolean = isEncrypted
}
