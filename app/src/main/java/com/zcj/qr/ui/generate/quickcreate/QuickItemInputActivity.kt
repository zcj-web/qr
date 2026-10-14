package com.zcj.qr.ui.generate.quickcreate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.zcj.qr.BaseActivity
import com.zcj.qr.R
import com.zcj.qr.crypto.Aes256GcmCrypto
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.ActivityQuickItemInputBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrStyle
import com.zcj.qr.ui.generate.preview.QrPreviewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickItemInputActivity : BaseActivity() {

    enum class QuickType(val label: String, val extraKey: String) {
        WIFI("WiFi 二维码", "wifi"),
        CONTACT("联系人二维码", "contact"),
        APP("应用二维码", "app"),
        URL("网址二维码", "url"),
        PHONE("电话二维码", "phone"),
        SMS("短信二维码", "sms"),
        EMAIL("邮件二维码", "email")
    }

    private lateinit var binding: ActivityQuickItemInputBinding
    private val repository by lazy { HistoryRepository(QrDatabase.getDatabase(applicationContext)) }
    private var logoUri: Uri? = null
    private var logoFile: java.io.File? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            logoUri = it
            try {
                val stream = contentResolver.openInputStream(it)
                val bitmap = stream?.let { BitmapFactory.decodeStream(it) }
                stream?.close()
                if (bitmap != null) {
                    binding.ivLogoPreview.setImageBitmap(bitmap)
                    binding.btnRemoveLogo.visibility = View.VISIBLE
                    binding.tvLogoHint.visibility = View.GONE
                    // 在 IO 线程异步写入缓存文件，避免阻塞 UI 线程导致跳转延迟
                    lifecycleScope.launch(Dispatchers.IO) {
                        logoFile = saveLogoToCache(bitmap)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickItemInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeStr = intent.getStringExtra("type") ?: return
        val type = QuickType.values().find { it.extraKey == typeStr }
        if (type == null) { finish(); return }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = type.label

        applyTypeFields(type)
        applyStyleToUI(QrStyle())  // 每次创建都使用默认样式

        binding.switchEncrypt.setOnCheckedChangeListener { _, checked ->
            binding.layoutPassword.visibility = if (checked) View.VISIBLE else View.GONE
        }

        binding.vDotColor.setOnClickListener { showColorPicker(isDot = true) }
        binding.vBgColor.setOnClickListener { showColorPicker(isDot = false) }

        binding.ivLogoPreview.setOnClickListener { pickImage.launch("image/*") }
        binding.btnRemoveLogo.setOnClickListener {
            logoUri = null
            logoFile = null
            getSharedPreferences("qr_style_pref", MODE_PRIVATE).edit().remove("logo_bytes").apply()
            logoFile?.delete()
            binding.ivLogoPreview.setImageResource(R.drawable.ic_gallery)
            binding.ivLogoPreview.setColorFilter(getColor(R.color.day_on_surface_variant))
            binding.btnRemoveLogo.visibility = View.GONE
            binding.tvLogoHint.visibility = View.VISIBLE
        }

        binding.btnGenerate.setOnClickListener { generateAndNavigate(type) }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun applyTypeFields(type: QuickType) {
        val allLayouts = listOf(
            binding.layoutWifi, binding.layoutContact, binding.layoutApp,
            binding.layoutUrl, binding.layoutPhone, binding.layoutSms, binding.layoutEmail
        )
        allLayouts.forEach { it.visibility = View.GONE }
        when (type) {
            QuickType.WIFI -> binding.layoutWifi.visibility = View.VISIBLE
            QuickType.CONTACT -> binding.layoutContact.visibility = View.VISIBLE
            QuickType.APP -> binding.layoutApp.visibility = View.VISIBLE
            QuickType.URL -> binding.layoutUrl.visibility = View.VISIBLE
            QuickType.PHONE -> binding.layoutPhone.visibility = View.VISIBLE
            QuickType.SMS -> binding.layoutSms.visibility = View.VISIBLE
            QuickType.EMAIL -> binding.layoutEmail.visibility = View.VISIBLE
        }
    }

    private fun applyStyleToUI(style: QrStyle) {
        binding.vDotColor.setBackgroundColor(style.dotColor)
        binding.vBgColor.setBackgroundColor(style.bgColor)
    }

    /** 将 Logo Bitmap 保存为缓存文件（PNG压缩） */
    private fun saveLogoToCache(bitmap: Bitmap): java.io.File? {
        return try {
            val file = java.io.File(cacheDir, "qr_logo.png")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
            }
            // 同时将路径存入 SharedPreferences，方便历史记录引用
            getSharedPreferences("qr_style_pref", MODE_PRIVATE).edit()
                .putString("logo_file_path", file.absolutePath)
                .apply()
            android.util.Log.d("QuickItemInputActivity", "saved logo to cache: ${file.absolutePath}")
            file
        } catch (_: Exception) { null }
    }

    private fun getCurrentStyle(): QrStyle {
        val dotColor = (binding.vDotColor.background as android.graphics.drawable.ColorDrawable).color
        val bgColor = (binding.vBgColor.background as android.graphics.drawable.ColorDrawable).color
        return QrStyle(dotColor = dotColor, bgColor = bgColor, dotShape = DotShape.SQUARE)
    }

    private fun showColorPicker(isDot: Boolean) {
        val colors = intArrayOf(
            0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFF1E293B.toInt(),
            0xFF4F46E5.toInt(), 0xFF7C3AED.toInt(), 0xFFDC2626.toInt(),
            0xFF059669.toInt(), 0xFFD97706.toInt(), 0xFF2563EB.toInt(),
            0xFF6B7280.toInt(), 0xFFEC4899.toInt(), 0xFF0891B2.toInt(),
        )
        val names = arrayOf("白色","黑色","深蓝灰","靛蓝","紫色","红色","绿色","琥珀","蓝色","灰色","粉色","青色")
        android.app.AlertDialog.Builder(this)
            .setTitle(if (isDot) "选择码点颜色" else "选择背景颜色")
            .setItems(names) { _, which ->
                val color = colors[which]
                if (isDot) binding.vDotColor.setBackgroundColor(color)
                else binding.vBgColor.setBackgroundColor(color)
            }
            .show()
    }

    private fun generateAndNavigate(type: QuickType) {
        val content = buildContent(type)
        if (content.isNullOrEmpty()) {
            Toast.makeText(this, "请填写必要信息", Toast.LENGTH_SHORT).show()
            return
        }

        val useEncryption = binding.switchEncrypt.isChecked
        val password = binding.etEncryptPassword.text?.toString()?.trim() ?: ""
        val iterations = when (binding.spinnerIterations.selectedItemPosition) {
            0 -> 10_000
            1 -> 100_000
            else -> 500_000
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val finalContent = if (useEncryption) {
                if (password.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@QuickItemInputActivity, "加密需要输入密码", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                "qr://${Aes256GcmCrypto.encrypt(content, password, iterations)}"
            } else content

            val style = getCurrentStyle()

            // 从 logoUri 同步保存 logo 文件，避免异步 logoFile 未完成导致的竞态条件
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
                android.util.Log.e("QuickItemInputActivity", "save logo failed: ${e.message}", e)
                null
            }

            val styleJson = "${style.dotShape.ordinal}:${Integer.toHexString(style.dotColor and 0x00FFFFFF)}:${Integer.toHexString(style.bgColor and 0x00FFFFFF)}"
            repository.addGenerate(finalContent, type.extraKey, useEncryption, styleJson, logoFilePath)

            withContext(Dispatchers.Main) {
                val intent = android.content.Intent(this@QuickItemInputActivity, QrPreviewActivity::class.java).apply {
                    putExtra("content", finalContent)
                    putExtra("dot_color", style.dotColor)
                    putExtra("bg_color", style.bgColor)
                    putExtra("dot_shape", style.dotShape.ordinal)
                }
                logoFilePath?.let { intent.putExtra("logo_file_path", it) }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun buildContent(type: QuickType): String? {
        return when (type) {
            QuickType.WIFI -> {
                val ssid = binding.etSsid.text?.toString()?.trim() ?: ""
                val password = binding.etPassword.text?.toString()?.trim() ?: ""
                if (ssid.isEmpty()) return null
                val encryption = when {
                    binding.radioWep.isChecked -> "WEP"
                    binding.radioNone.isChecked -> "nopass"
                    else -> "WPA"
                }
                if (encryption == "nopass") "WIFI:S:$ssid;T:nopass;;" else "WIFI:S:$ssid;T:$encryption;P:$password;;"
            }
            QuickType.CONTACT -> {
                val name = binding.etName.text?.toString()?.trim() ?: ""
                val phone = binding.etContactPhone.text?.toString()?.trim() ?: ""
                val email = binding.etContactEmail.text?.toString()?.trim() ?: ""
                if (name.isEmpty() && phone.isEmpty() && email.isEmpty()) return null
                buildString {
                    append("BEGIN:VCARD\nVERSION:3.0\n")
                    if (name.isNotEmpty()) append("FN:$name\n")
                    if (phone.isNotEmpty()) append("TEL:$phone\n")
                    if (email.isNotEmpty()) append("EMAIL:$email\n")
                    append("END:VCARD")
                }
            }
            QuickType.APP -> {
                val packageName = binding.etPackageName.text?.toString()?.trim() ?: ""
                if (packageName.isEmpty()) return null
                "market://details?id=$packageName"
            }
            QuickType.URL -> binding.etUrl.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
            QuickType.PHONE -> binding.etPhone.text?.toString()?.trim()?.let { "tel:$it" }
            QuickType.SMS -> {
                val phone = binding.etSmsPhone.text?.toString()?.trim() ?: ""
                val msg = binding.etSmsMessage.text?.toString()?.trim() ?: ""
                if (phone.isEmpty()) return null
                if (msg.isNotEmpty()) "smsto:$phone:$msg" else "smsto:$phone:;"
            }
            QuickType.EMAIL -> {
                val email = binding.etEmail.text?.toString()?.trim() ?: ""
                val subject = binding.etSubject.text?.toString()?.trim() ?: ""
                val body = binding.etBody.text?.toString()?.trim() ?: ""
                if (email.isEmpty()) return null
                "mailto:$email${if (subject.isNotEmpty()) "?subject=$subject" else ""}${if (body.isNotEmpty()) "&body=$body" else ""}"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
