package com.zcj.qr.ui.scan.share

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.zcj.qr.BaseActivity
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.ui.scan.result.ScanResultActivity
import com.zcj.qr.util.QrScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareScanActivity : BaseActivity() {

    private val repository by lazy { HistoryRepository(QrDatabase.getDatabase(applicationContext)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                }
            }
            else -> null
        }

        if (uri == null) {
            Toast.makeText(this, "未收到图片", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bitmap = decodeBitmap(uri)
        if (bitmap == null) {
            Toast.makeText(this, "无法读取图片，请选择图片文件", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val result = QrScanner.scanWithMultipleAttempts(bitmap)
        if (result != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val type = when {
                    result.startsWith("qr://") -> "encrypted"
                    result.startsWith("WIFI:") -> "wifi"
                    result.startsWith("BEGIN:VCARD") -> "contact"
                    result.startsWith("http://") || result.startsWith("https://") || result.startsWith("market://") -> "url"
                    result.startsWith("tel:") -> "phone"
                    result.startsWith("smsto:") -> "sms"
                    result.startsWith("mailto:") -> "email"
                    else -> "text"
                }
                repository.addScan(result, type, result.startsWith("qr://"))
            }
            val intent = Intent(this, ScanResultActivity::class.java)
            intent.putExtra("content", result)
            startActivity(intent)
        } else {
            Toast.makeText(this, "未检测到二维码，请确保图片清晰且包含二维码", Toast.LENGTH_LONG).show()
        }
        finish()
    }

    private fun decodeBitmap(uri: Uri): Bitmap? {
        return try {
            val stream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            null
        }
    }
}
