package com.zcj.qr.ui.generate.custom

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zcj.qr.R
import com.zcj.qr.crypto.Aes256GcmCrypto
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.FragmentCustomGenerateBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrStyle
import com.zcj.qr.ui.generate.preview.QrPreviewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomGenerateActivity : AppCompatActivity() {

    private lateinit var binding: FragmentCustomGenerateBinding
    private val repository by lazy { HistoryRepository(QrDatabase.getDatabase(applicationContext)) }
    private var logoUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            logoUri = it
            // 在 IO 线程解码和编码，避免阻塞 UI 线程导致跳转延迟
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap: android.graphics.Bitmap? = try {
                    val stream = contentResolver.openInputStream(it)
                    if (stream != null) {
                        android.graphics.BitmapFactory.decodeStream(stream)?.also { stream.close() }
                    } else null
                } catch (e: Exception) { null }
                if (bitmap != null) {
                    // 写回主线程更新 UI
                    withContext(Dispatchers.Main) {
                        binding.ivLogoPreview.setImageBitmap(bitmap)
                        binding.btnRemoveLogo.visibility = View.VISIBLE
                        binding.tvLogoHint.visibility = View.GONE
                        saveLogoBitmapToPrefs(bitmap)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCustomGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        applyStyleToUI(QrStyle())

        binding.vDotColor.setOnClickListener { showColorPicker(isDotColor = true) }
        binding.vBgColor.setOnClickListener { showColorPicker(isDotColor = false) }

        binding.switchEncrypt.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutPassword.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Logo 选择：点击图标准备，点击区域触发
        binding.ivLogoPreview.setOnClickListener { pickImage.launch("image/*") }
        binding.btnRemoveLogo.setOnClickListener {
            logoUri = null
            revokeUriPermissions()
            getSharedPreferences("qr_style_pref", MODE_PRIVATE).edit().remove("logo_file_path").apply()
            binding.ivLogoPreview.setImageResource(R.drawable.ic_gallery)
            binding.ivLogoPreview.setColorFilter(getColor(R.color.day_on_surface_variant))
            binding.btnRemoveLogo.visibility = View.GONE
            binding.tvLogoHint.visibility = View.VISIBLE
        }

        binding.btnGenerate.setOnClickListener { generateAndNavigate() }
    }

    private fun applyStyleToUI(style: QrStyle) {
        binding.vDotColor.setBackgroundColor(style.dotColor)
        binding.vBgColor.setBackgroundColor(style.bgColor)
        // 恢复上次选择的 Logo
        restoreLogoFromPrefs()
    }

    private fun restoreLogoFromPrefs() {
        val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
        // 优先从缓存文件路径恢复
        val filePath = prefs.getString("logo_file_path", "")?.takeIf { it.isNotEmpty() }
        if (filePath != null && java.io.File(filePath).exists()) {
            try {
                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
                if (bitmap != null) {
                    binding.ivLogoPreview.setImageBitmap(bitmap)
                    binding.btnRemoveLogo.visibility = View.VISIBLE
                    binding.tvLogoHint.visibility = View.GONE
                    return
                }
            } catch (_: Exception) {}
        }
        // fallback: 旧版 logo_uri（兼容历史数据）
        val uriStr = prefs.getString("logo_uri", "")?.takeIf { it.isNotEmpty() }
        if (uriStr != null) {
            val uri = android.net.Uri.parse(uriStr)
            logoUri = uri
            try {
                val stream = contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                stream?.close()
                if (bitmap != null) {
                    binding.ivLogoPreview.setImageBitmap(bitmap)
                    binding.btnRemoveLogo.visibility = View.VISIBLE
                    binding.tvLogoHint.visibility = View.GONE
                    return
                }
            } catch (e: Exception) {
                android.util.Log.w("QrApp", "restoreLogoFromPrefs failed: ${e.message}")
            }
        }
        // 无 logo
        binding.ivLogoPreview.setImageResource(R.drawable.ic_gallery)
        binding.ivLogoPreview.setColorFilter(getColor(R.color.day_on_surface_variant))
        binding.btnRemoveLogo.visibility = View.GONE
        binding.tvLogoHint.visibility = View.VISIBLE
    }

    /** 重置 Logo 为未选择状态 */
    private fun resetLogoToDefault() {
        logoUri = null
        revokeUriPermissions()
        getSharedPreferences("qr_style_pref", MODE_PRIVATE).edit().remove("logo_file_path").apply()
        binding.ivLogoPreview.setImageResource(R.drawable.ic_gallery)
        binding.ivLogoPreview.setColorFilter(getColor(R.color.day_on_surface_variant))
        binding.btnRemoveLogo.visibility = View.GONE
        binding.tvLogoHint.visibility = View.VISIBLE
    }

    private fun getCurrentStyle(): QrStyle {
        val dotColor = (binding.vDotColor.background as android.graphics.drawable.ColorDrawable).color
        val bgColor = (binding.vBgColor.background as android.graphics.drawable.ColorDrawable).color
        return QrStyle(dotColor = dotColor, bgColor = bgColor, dotShape = DotShape.SQUARE)
    }

    private fun saveLastStyle(style: QrStyle) {
        val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
        prefs.edit()
            .putInt("dot_color", style.dotColor)
            .putInt("bg_color", style.bgColor)
            .putInt("dot_shape", style.dotShape.ordinal)
            .apply()
    }

    private fun showColorPicker(isDotColor: Boolean) {
        val colors = intArrayOf(
            0xFFFFFFFF.toInt(),   // 白色
            0xFF000000.toInt(),   // 黑色
            0xFF1E293B.toInt(),   // 深蓝灰
            0xFF4F46E5.toInt(),   // 靛蓝
            0xFF7C3AED.toInt(),   // 紫色
            0xFFDC2626.toInt(),   // 红色
            0xFF059669.toInt(),   // 绿色
            0xFFD97706.toInt(),   // 琥珀
            0xFF2563EB.toInt(),   // 蓝色
            0xFF6B7280.toInt(),   // 灰色
            0xFFEC4899.toInt(),   // 粉色
            0xFF0891B2.toInt(),   // 青色
        )
        val names = arrayOf("白色", "黑色", "深蓝灰", "靛蓝", "紫色", "红色", "绿色", "琥珀", "蓝色", "灰色", "粉色", "青色")

        android.app.AlertDialog.Builder(this)
            .setTitle(if (isDotColor) "选择码点颜色" else "选择背景颜色")
            .setItems(names) { _, which ->
                val color = colors[which]
                if (isDotColor) binding.vDotColor.setBackgroundColor(color)
                else binding.vBgColor.setBackgroundColor(color)
            }
            .show()
    }

    private fun decodeLogoBitmap(uri: Uri): Bitmap? {
        return try {
            val stream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            null
        }
    }

    /** 为 MediaStore URI 授予读取权限，供后续 Activity 使用 */
    private fun grantUriPermissionsToUri(uri: Uri?) {
        uri ?: return
        try {
            // 尝试获取持久权限（Android 7.0+）
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: Exception) {
            android.util.Log.w("QrApp", "grantUriPermissionsToUri failed: ${e.message}")
        }
    }

    private fun revokeUriPermissions() {
        logoUri?.let { uri ->
            try {
                contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) { }
        }
    }

    /** 将 Bitmap 编码为 PNG 文件并保存路径到 SharedPreferences */
    private fun saveLogoBitmapToPrefs(bitmap: android.graphics.Bitmap) {
        try {
            val ts = System.currentTimeMillis()
            val file = java.io.File(cacheDir, "qr_logo_$ts.png")
            file.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 80, out)
            }
            if (file.exists()) {
                getSharedPreferences("qr_style_pref", MODE_PRIVATE).edit()
                    .putString("logo_file_path", file.absolutePath)
                    .apply()
                android.util.Log.d("CustomGenActivity", "saveLogoBitmapToPrefs: saved to $file")
            }
        } catch (e: Exception) {
            android.util.Log.e("CustomGenActivity", "saveLogoBitmapToPrefs failed: ${e.message}")
        }
    }

    /** 从 SharedPreferences 读取 Logo Bitmap 字节并解码 */
    private fun loadLogoBitmapFromPrefs(): android.graphics.Bitmap? {
        return try {
            val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
            val base64 = prefs.getString("logo_bytes", "") ?: return null
            if (base64.isEmpty()) return null
            val bytes = android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            android.util.Log.e("CustomGenActivity", "loadLogoBitmapFromPrefs failed: ${e.message}")
            null
        }
    }

    private fun generateAndNavigate() {
        val content = binding.etContent.text?.toString()?.trim() ?: ""
        if (content.isEmpty()) {
            binding.etContent.error = "请输入内容"
            return
        }

        val style = getCurrentStyle()

        val useEncryption = binding.switchEncrypt.isChecked
        val password = binding.etPassword.text?.toString() ?: ""
        val iterations = when (binding.spinnerIterations.selectedItemPosition) {
            0 -> 10_000
            1 -> 100_000
            else -> 500_000
        }

        // 构建样式 JSON 保存到历史记录（手动拼接，避免引入 JSON 库）
        val styleJson = "${style.dotShape.ordinal}:${Integer.toHexString(style.dotColor and 0x00FFFFFF)}:${Integer.toHexString(style.bgColor and 0x00FFFFFF)}"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val finalContent = if (useEncryption) {
                    if (password.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CustomGenerateActivity, "加密需要输入密码", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                    android.util.Log.d("CustomGenActivity", "encrypting content, iterations=$iterations")
                    "qr://${Aes256GcmCrypto.encrypt(content, password, iterations)}"
                } else {
                    content
                }
                android.util.Log.d("CustomGenActivity", "finalContent length=${finalContent.length}")

                // 保存 logo 到缓存文件并记录路径（替代 Base64）
                val logoFilePath = try {
                    logoUri?.let { uri ->
                        val ts = System.currentTimeMillis()
                        val cacheFile = java.io.File(cacheDir, "qr_logo_$ts.png")
                        val stream = contentResolver.openInputStream(uri)
                        stream?.use { s ->
                            val bmp = android.graphics.BitmapFactory.decodeStream(s)
                            bmp?.compress(android.graphics.Bitmap.CompressFormat.PNG, 80, cacheFile.outputStream())
                        }
                        if (cacheFile.exists()) cacheFile.absolutePath else null
                    }
                } catch (e: Exception) {
                    android.util.Log.e("CustomGenActivity", "save logo file failed: ${e.message}", e)
                    null
                }
                android.util.Log.d("CustomGenActivity", "logoFilePath=${logoFilePath}")

                android.util.Log.d("CustomGenActivity", "calling repository.addGenerate")
                repository.addGenerate(finalContent, if (useEncryption) "encrypted" else "text", useEncryption, styleJson, logoFilePath)
                android.util.Log.d("CustomGenActivity", "repository.addGenerate done")

                // 保存样式到 SharedPreferences，避免通过 Intent 传递大对象导致 Binder 事务失败
                android.util.Log.d("CustomGenActivity", "saving prefs")
                val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
                prefs.edit()
                    .putInt("dot_color", style.dotColor)
                    .putInt("bg_color", style.bgColor)
                    .putInt("dot_shape", style.dotShape.ordinal)
                    .apply()
                // 同时缓存 logo 文件路径，方便预览页读取
                if (!logoFilePath.isNullOrEmpty()) {
                    prefs.edit().putString("logo_file_path", logoFilePath).apply()
                } else {
                    prefs.edit().remove("logo_file_path").apply()
                }

                android.util.Log.d("CustomGenActivity", "starting QrPreviewActivity")
                val intent = Intent(this@CustomGenerateActivity, QrPreviewActivity::class.java)
                intent.putExtra("content", finalContent)
                intent.putExtra("dot_color", style.dotColor)
                intent.putExtra("bg_color", style.bgColor)
                intent.putExtra("dot_shape", style.dotShape.ordinal)
                // 将 logo 文件路径通过 Intent 传递，避免 resetLogoToDefault 清空 SharedPreferences 后预览页读不到
                if (!logoFilePath.isNullOrEmpty()) {
                    intent.putExtra("logo_file_path", logoFilePath)
                }
                try {
                    startActivity(intent)
                    android.util.Log.d("CustomGenActivity", "startActivity done, finishing")
                } catch (se: SecurityException) {
                    android.util.Log.e("CustomGenActivity", "SecurityException: ${se.message}", se)
                    throw se
                }
                // 重置 Logo 为未选择状态
                resetLogoToDefault()
                finish()
            } catch (e: Exception) {
                android.util.Log.e("CustomGenActivity", "generateAndNavigate FAILED: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.tvError.text = "生成失败: ${e.message}\n${e.javaClass.simpleName}"
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
