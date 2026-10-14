package com.zcj.qr.ui.generate.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.zcj.qr.crypto.Aes256GcmCrypto

class CustomGenerateViewModel : ViewModel() {
    val generatedContent = MutableLiveData<String?>()
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
            generatedContent.value = currentContent
        } catch (e: Exception) {
            showError.value = e.message ?: "生成失败"
        }
    }

    fun getContent(): String = currentContent
    fun isEncrypted(): Boolean = isEncrypted
}
