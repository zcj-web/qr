package com.zcj.qr.ui.generate.preview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.zcj.qr.R
import com.zcj.qr.databinding.ActivityQrPreviewBinding
import com.zcj.qr.util.QrCodeGenerator
import com.zcj.qr.util.QrStyle
import com.zcj.qr.util.DotShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class QrPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent.getStringExtra("content") ?: ""

        binding.tvContent.text = content
        generateAndShow(content)

        setupButtons(content)
        setupBack()
    }

    private fun generateAndShow(content: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.ivQr.visibility = android.view.View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = QrCodeGenerator.generate(content, size = 1200)
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = android.view.View.GONE
                binding.ivQr.visibility = android.view.View.VISIBLE
                binding.ivQr.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupButtons(content: String) {
        binding.btnSave.setOnClickListener {
            binding.ivQr.drawable?.let { drawable ->
                val bitmap = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                    ?: return@setOnClickListener
                saveToGallery(bitmap)
            }
        }

        binding.btnShare.setOnClickListener {
            binding.ivQr.drawable?.let { drawable ->
                val bitmap = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                    ?: return@setOnClickListener
                shareBitmap(bitmap)
            }
        }
    }

    private fun saveToGallery(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileName = "QR_${System.currentTimeMillis()}.png"
                val filesDir = externalCacheDir ?: cacheDir
                val file = java.io.File(filesDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                withContext(Dispatchers.Main) {
                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    intent.data = Uri.fromFile(file)
                    sendBroadcast(intent)
                    Toast.makeText(this@QrPreviewActivity, "已保存到相册", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QrPreviewActivity, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareBitmap(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileName = "QR_${System.currentTimeMillis()}.png"
                val filesDir = externalCacheDir ?: cacheDir
                val file = java.io.File(filesDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    this@QrPreviewActivity,
                    "${applicationContext.packageName}.fileprovider",
                    file
                )
                withContext(Dispatchers.Main) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(intent, "分享二维码"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QrPreviewActivity, "分享失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
