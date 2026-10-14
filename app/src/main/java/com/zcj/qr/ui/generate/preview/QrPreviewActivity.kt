package com.zcj.qr.ui.generate.preview

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.zcj.qr.R
import com.zcj.qr.databinding.ActivityQrPreviewBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrCodeGenerator
import com.zcj.qr.util.QrStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class QrPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrPreviewBinding
    // 缓存当前安全样式（低对比度时自动调整为黑白）
    private var currentSafeStyle: com.zcj.qr.util.QrStyle? = null

    private fun loadStyle(intent: Intent): QrStyle {
        // 1. 优先从 Intent 直接读取各字段（history/QuickItemInputActivity 路径）
        if (intent.hasExtra("dot_color") && intent.hasExtra("bg_color") && intent.hasExtra("dot_shape")) {
            val intentDotColor = intent.getIntExtra("dot_color", 0xFF1E293B.toInt())
            val intentBgColor = intent.getIntExtra("bg_color", 0xFFFFFFFF.toInt())
            val intentDotShape = intent.getIntExtra("dot_shape", 0)
            return QrStyle(
                dotColor = intentDotColor,
                bgColor = intentBgColor,
                dotShape = DotShape.values().getOrNull(intentDotShape) ?: DotShape.SQUARE
            )
        }

        // 2. fallback: 尝试 Parcelable QrStyle（CustomGenerateFragment 导航路径）
        val styleFromIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("style", QrStyle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("style") as? QrStyle
        }
        if (styleFromIntent != null) return styleFromIntent

        // 3. 最终 fallback: SharedPreferences
        val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
        return QrStyle(
            dotColor = prefs.getInt("dot_color", 0xFF1E293B.toInt()),
            bgColor = prefs.getInt("bg_color", 0xFFFFFFFF.toInt()),
            dotShape = DotShape.values().getOrNull(prefs.getInt("dot_shape", 0)) ?: DotShape.SQUARE
        )
    }

    // 优先从 Intent extra 读取 logo 文件路径（CustomGenerateActivity 会通过 Intent 传递），fallback 到 SharedPreferences
    private fun loadLogoBitmap(): android.graphics.Bitmap? {
        val tag = "QrPreviewActivity"
        // 优先读 Intent extra 中的 logo_file_path
        val filePath = intent.getStringExtra("logo_file_path")?.takeIf { it.isNotEmpty() }
        if (filePath != null) {
            return try {
                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
                if (bitmap != null) bitmap else {
                    android.util.Log.e(tag, "loadLogo from file failed: $filePath")
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e(tag, "loadLogo from file failed: ${e.message}")
                null
            }
        }
        // fallback: 读 SharedPreferences 中的文件路径
        val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
        val savedPath = prefs.getString("logo_file_path", "") ?: ""
        if (savedPath.isNotEmpty()) {
            return try {
                android.graphics.BitmapFactory.decodeFile(savedPath)
            } catch (e: Exception) {
                android.util.Log.e(tag, "loadLogo from saved path failed: ${e.message}")
                null
            }
        }
        // 最终 fallback: Intent extra URI
        return try {
            val uriStr = intent.getStringExtra("logo_uri")?.takeIf { it.isNotEmpty() }
                ?: return null
            val uri = android.net.Uri.parse(uriStr)
            val stream = contentResolver.openInputStream(uri)
            android.graphics.BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            android.util.Log.e(tag, "loadLogo from URI failed: ${e.message}")
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent.getStringExtra("content") ?: ""
        val baseStyle = loadStyle(intent)
        val logoBitmap = loadLogoBitmap()
        val style = if (logoBitmap != null) baseStyle.copy(logoBitmap = logoBitmap) else baseStyle
        // 校验对比度，必要时自动修正
        val safeStyle = com.zcj.qr.util.ensureScannableStyle(style)
        currentSafeStyle = safeStyle
        val contrastOk = safeStyle.dotColor == style.dotColor && safeStyle.bgColor == style.bgColor

        binding.tvContent.text = content
        generateAndShow(content, safeStyle, contrastOk)

        setupButtons(content)
        setupBack()
    }

    private fun generateAndShow(content: String, style: QrStyle, contrastOk: Boolean) {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.ivQr.visibility = android.view.View.GONE
        binding.tvError.visibility = android.view.View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = QrCodeGenerator.generate(content, size = 1200, style = style)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.ivQr.visibility = android.view.View.VISIBLE
                    binding.ivQr.setImageBitmap(bitmap)
                    // 对比度不足时提示用户
                    if (!contrastOk) {
                        android.widget.Toast.makeText(
                            this@QrPreviewActivity,
                            "所选颜色对比度过低，已自动调整为黑白以确保可扫描",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("QrPreviewActivity", "QR generation failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.tvError.visibility = android.view.View.VISIBLE
                    binding.tvError.text = "生成失败: ${e.message}"
                }
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
                val values = android.content.ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_${System.currentTimeMillis()}.png")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QrCode")
                values.put(MediaStore.Images.Media.IS_PENDING, 1)

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw Exception("无法获取保存路径")

                contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)

                withContext(Dispatchers.Main) {
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
                val file = java.io.File(cacheDir, "QR_share_${System.currentTimeMillis()}.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val uri = FileProvider.getUriForFile(
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
        binding.btnBack.setOnClickListener { finish() }
    }
}
