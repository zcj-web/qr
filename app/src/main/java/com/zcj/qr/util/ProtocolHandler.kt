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
            content.startsWith("http://") || content.startsWith("https://") || content.startsWith("market://") || content.startsWith("sms:") || content.startsWith("geo:") -> ProtocolType.URL
            content.startsWith("tel:") -> ProtocolType.PHONE
            content.startsWith("smsto:") -> ProtocolType.SMS
            content.startsWith("mailto:") -> ProtocolType.EMAIL
            isDomain(content) -> ProtocolType.URL
            else -> ProtocolType.TEXT(content)
        }
    }

    /** 判断是否为裸域名（如 baidu.com / www.baidu.com） */
    private fun isDomain(text: String): Boolean {
        if (text.length < 4) return false
        return Regex("""^[a-zA-Z0-9][-a-zA-Z0-9]*(\.[a-zA-Z]{2,})+$""").matches(text.trim())
    }

    /** 为裸域名自动补全 https:// */
    fun resolveUrl(content: String): String {
        return if (isDomain(content.trim())) "https://$content" else content
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
        is ProtocolType.ENCRYPTED -> "🔒 加密内容 (点击解密)"
        else -> content
    }

    fun getActionIntent(type: ProtocolType, content: String): Intent? = when (type) {
        is ProtocolType.WIFI -> Intent(Settings.ACTION_WIFI_SETTINGS)
        is ProtocolType.CONTACT -> buildContactIntent(content)
        is ProtocolType.URL -> {
            val url = resolveUrl(content)
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
        is ProtocolType.PHONE -> Intent(Intent.ACTION_DIAL, Uri.parse(content))
        is ProtocolType.SMS -> buildSmsIntent(content)
        is ProtocolType.EMAIL -> buildEmailIntent(content)
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

    private fun parseSmsDisplay(content: String): String {
        val parts = content.removePrefix("smsto:").split(":", limit = 2)
        val phone = parts.getOrElse(0) { "" }
        val body = parts.getOrElse(1) { "" }
        return if (body.isNotEmpty()) "$phone\n$body" else phone
    }

    private fun parseEmailDisplay(content: String): String {
        val withoutPrefix = content.removePrefix("mailto:")
        val questionIdx = withoutPrefix.indexOf('?')
        return if (questionIdx >= 0) withoutPrefix.substring(0, questionIdx) else withoutPrefix
    }

    private fun buildContactIntent(vcard: String): Intent? {
        var name: String? = null
        var phone: String? = null
        var email: String? = null
        var org: String? = null

        vcard.lines().forEach { line ->
            when {
                line.startsWith("FN:") -> name = line.substringAfter("FN:").trim()
                line.startsWith("TEL") -> phone = line.substringAfter(":").trim()
                line.startsWith("EMAIL") -> email = line.substringAfter(":").trim()
                line.startsWith("ORG:") -> org = line.substringAfter("ORG:").trim()
            }
        }

        return Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            if (name != null) putExtra(ContactsContract.Intents.Insert.NAME, name)
            if (phone != null) putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            if (email != null) putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            if (org != null) putExtra(ContactsContract.Intents.Insert.COMPANY, org)
        }
    }

    private fun buildSmsIntent(content: String): Intent? {
        val withoutPrefix = content.removePrefix("smsto:")
        val colonIdx = withoutPrefix.indexOf(':')
        val phone = if (colonIdx >= 0) withoutPrefix.substring(0, colonIdx) else withoutPrefix
        val body = if (colonIdx >= 0 && colonIdx < withoutPrefix.length - 1)
            withoutPrefix.substring(colonIdx + 1) else ""

        return Intent(Intent.ACTION_SENDTO).apply {
            // Use plain smsto: URI without path slash to avoid showing "/number"
            data = Uri.parse("smsto:$phone")
            putExtra("sms_body", body)
        }
    }

    private fun buildEmailIntent(content: String): Intent? {
        val withoutPrefix = content.removePrefix("mailto:")
        val questionIdx = withoutPrefix.indexOf('?')

        val recipients = if (questionIdx >= 0)
            withoutPrefix.substring(0, questionIdx) else withoutPrefix

        var subject = ""
        var body = ""
        if (questionIdx >= 0) {
            val query = withoutPrefix.substring(questionIdx + 1)
            query.split("&").forEach { param ->
                val pair = param.split("=", limit = 2)
                when {
                    pair.getOrNull(0) == "subject" -> subject = pair.getOrNull(1)?.let { Uri.decode(it) } ?: ""
                    pair.getOrNull(0) == "body" -> body = pair.getOrNull(1)?.let { Uri.decode(it) } ?: ""
                }
            }
        }

        return Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipients))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }
}
