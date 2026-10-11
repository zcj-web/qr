package com.zcj.qr.ui.scan.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zcj.qr.R
import com.zcj.qr.crypto.Aes256GcmCrypto
import com.zcj.qr.crypto.QrProtocolParser
import com.zcj.qr.databinding.ActivityScanResultBinding
import com.zcj.qr.util.ProtocolHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding
    private val viewModel: ScanResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent.getStringExtra("content") ?: return
        viewModel.setContent(content)

        setupObservers()
        setupButtons()
    }

    private fun setupObservers() {
        viewModel.protocolType.observe(this) { type ->
            type?.let {
                binding.tvType.text = ProtocolHandler.getDisplayName(it)
                binding.tvContent.text = ProtocolHandler.getContentPreview(it, viewModel.content.value ?: "")
                updateActionButtonText(it)
                binding.btnDecrypt.visibility = if (it is com.zcj.qr.util.ProtocolType.ENCRYPTED) View.VISIBLE else View.GONE
            }
        }

        viewModel.isDecrypting.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.tvContent.visibility = if (loading) View.GONE else View.VISIBLE
            binding.btnCopy.visibility = if (loading) View.GONE else View.VISIBLE
            binding.btnAction.visibility = if (loading) View.GONE else View.VISIBLE
            binding.btnDecrypt.visibility = if (loading) View.GONE else (if (viewModel.protocolType.value is com.zcj.qr.util.ProtocolType.ENCRYPTED) View.VISIBLE else View.GONE)
        }

        viewModel.decryptedContent.observe(this) { decrypted ->
            decrypted?.let {
                binding.tvContent.text = it
                Toast.makeText(this, "解密成功", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "解密失败，密码可能不正确", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        binding.btnCopy.setOnClickListener {
            val text = binding.tvContent.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("二维码内容", text))
            Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show()
        }

        binding.btnAction.setOnClickListener {
            val type = viewModel.protocolType.value
            type?.let {
                val intent = ProtocolHandler.getActionIntent(it, viewModel.content.value ?: "")
                intent?.let { i -> startActivity(i) }
                    ?: run { Toast.makeText(this, "无法处理此内容", Toast.LENGTH_SHORT).show() }
            }
        }

        binding.btnDecrypt.setOnClickListener {
            showPasswordDialog()
        }

        binding.tvContent.setOnLongClickListener {
            val text = binding.tvContent.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("二维码内容", text))
            Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun updateActionButtonText(type: com.zcj.qr.util.ProtocolType) {
        val text = when (type) {
            is com.zcj.qr.util.ProtocolType.WIFI -> "连接 WiFi"
            is com.zcj.qr.util.ProtocolType.CONTACT -> "添加联系人"
            is com.zcj.qr.util.ProtocolType.URL -> "打开网站"
            is com.zcj.qr.util.ProtocolType.PHONE -> "拨打电话"
            is com.zcj.qr.util.ProtocolType.SMS -> "发送短信"
            is com.zcj.qr.util.ProtocolType.EMAIL -> "发送邮件"
            is com.zcj.qr.util.ProtocolType.TEXT -> "查看"
            is com.zcj.qr.util.ProtocolType.ENCRYPTED -> "解密"
        }
        binding.btnAction.text = text
    }

    private fun showPasswordDialog() {
        val input = android.widget.EditText(this).apply {
            hint = "请输入解密密码"
            setSingleLine()
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        AlertDialog.Builder(this)
            .setTitle("输入密码解密")
            .setView(input)
            .setPositiveButton("解密") { _, _ ->
                val password = input.text.toString()
                if (password.isEmpty()) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.decrypt(password)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
