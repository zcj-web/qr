package com.zcj.qr.crypto

object QrProtocolParser {
    const val PREFIX = "qr://"

    fun isEncrypted(content: String): Boolean = content.startsWith(PREFIX)

    fun extractCipher(content: String): String = content.removePrefix(PREFIX)
}
