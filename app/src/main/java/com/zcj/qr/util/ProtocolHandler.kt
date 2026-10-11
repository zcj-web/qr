package com.zcj.qr.util

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings

sealed class ProtocolType {
    data object WIFI : ProtocolType()
    data object CONTACT : ProtocolType()
    data object URL : ProtocolType()
    data object PHONE : ProtocolType()
    data object SMS : ProtocolType()
    data object EMAIL : ProtocolType()
    data class TEXT(val content: String) : ProtocolType()
    data class ENCRYPTED(val cipher: String) : ProtocolType()
}

object ProtocolHandler {
    fun parse(content: String): ProtocolType {
        return when {
            content.startsWith("qr://") -> ProtocolType.ENCRYPTED(content.removePrefix("qr://"))
            content.startsWith("WIFI:") -> ProtocolType.WIFI
            content.startsWith("BEGIN:VCARD") -> ProtocolType.CONTACT
            content.startsWith("http://") || content.startsWith("https://") -> ProtocolType.URL
            content.startsWith("tel:") -> ProtocolType.PHONE
            content.startsWith("smsto:") -> ProtocolType.SMS
            content.startsWith("mailto:") -> ProtocolType.EMAIL
            else -> ProtocolType.TEXT(content)
        }
    }

    fun getDisplayName(type: ProtocolType): String = when (type) {
        is ProtocolType.WIFI -> "WiFi 网络"
        is ProtocolType.CONTACT -> "联系人"
        is ProtocolType.URL -> "网址"
        is ProtocolType.PHONE -> "电话号码"
        is ProtocolType.SMS -> "短信"
        is ProtocolType.EMAIL -> "电子邮件"
        is ProtocolType.TEXT -> "文本"
        is ProtocolType.ENCRYPTED -> "加密内容"
    }

    fun getContentPreview(type: ProtocolType, content: String): String = when (type) {
        is ProtocolType.WIFI -> parseWifiSsid(content)
        is ProtocolType.CONTACT -> extractContactName(content)
        is ProtocolType.URL -> content
        is ProtocolType.PHONE -> content.removePrefix("tel:")
        is ProtocolType.SMS -> content.removePrefix("smsto:")
        is ProtocolType.EMAIL -> content.removePrefix("mailto:")
        is ProtocolType.TEXT -> content
        is ProtocolType.ENCRYPTED -> "🔒 加密内容 (点击解密)"
    }

    fun getActionIntent(type: ProtocolType, content: String): Intent? = when (type) {
        is ProtocolType.WIFI -> Intent(Settings.ACTION_WIFI_SETTINGS)
        is ProtocolType.CONTACT -> Intent(Intent.ACTION_INSERT).also {
            it.type = ContactsContract.Contacts.CONTENT_TYPE
        }
        is ProtocolType.URL -> Intent(Intent.ACTION_VIEW, Uri.parse(content))
        is ProtocolType.PHONE -> Intent(Intent.ACTION_DIAL, Uri.parse(content))
        is ProtocolType.SMS -> Intent(Intent.ACTION_SENDTO, Uri.parse(content))
        is ProtocolType.EMAIL -> Intent(Intent.ACTION_SEND).also {
            it.type = "message/rfc822"
            it.putExtra(Intent.EXTRA_EMAIL, arrayOf(content.removePrefix("mailto:")))
        }
        is ProtocolType.TEXT -> null
        is ProtocolType.ENCRYPTED -> null
    }

    private fun parseWifiSsid(wifiStr: String): String {
        val match = Regex("""WIFI:S:([^;]+)""").find(wifiStr)
        return match?.groupValues?.get(1) ?: wifiStr
    }

    private fun extractContactName(vcard: String): String {
        val fnMatch = Regex("""FN(?:\n|;[^:]*):(.+)""").find(vcard)
        val nMatch = Regex("""N(?:\n|;[^:]*):([^;]+)""").find(vcard)
        return fnMatch?.groupValues?.get(1)
            ?: nMatch?.groupValues?.get(1)
            ?: "联系人"
    }
}
