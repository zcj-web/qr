package com.zcj.qr.ui.generate.quickcreate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zcj.qr.BaseActivity
import com.zcj.qr.R
import com.zcj.qr.databinding.ActivityQuickCreateBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrStyle
import com.zcj.qr.ui.generate.preview.QrPreviewActivity
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuickCreateActivity : BaseActivity() {

    private lateinit var binding: ActivityQuickCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val content = intent.getStringExtra("content")
        if (!content.isNullOrEmpty()) {
            val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
            val spShape = prefs.getInt("dot_shape", 0)
            val spDotColor = prefs.getInt("dot_color", 0xFF1E293B.toInt())
            val spBgColor = prefs.getInt("bg_color", 0xFFFFFFFF.toInt())
            val style = QrStyle(
                dotColor = spDotColor,
                bgColor = spBgColor,
                dotShape = DotShape.values().getOrNull(spShape) ?: DotShape.SQUARE
            )
            // 从缓存文件读取 Logo（避免大 Base64 字符串导致 TransactionTooLargeException）
            val cachedLogoFile = java.io.File(cacheDir, "qr_logo.png")

            // 异步保存历史记录
            val styleJson = "$spShape:${Integer.toHexString(spDotColor and 0x00FFFFFF)}:${Integer.toHexString(spBgColor and 0x00FFFFFF)}"
            val logoFilePath = if (cachedLogoFile.exists()) cachedLogoFile.absolutePath else null
            android.util.Log.d("QuickCreateAct", "saving history: content=${content.take(30)}, shape=$spShape, logo=$logoFilePath")
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                try {
                    val db = QrDatabase.getDatabase(applicationContext)
                    val repo = HistoryRepository(db)
                    repo.addGenerate(content, "quick_dialog", false, styleJson, logoFilePath)
                    android.util.Log.d("QuickCreateAct", "history saved OK")
                } catch (e: Exception) {
                    android.util.Log.e("QuickCreateAct", "history save FAILED: ${e.message}", e)
                }
            }

            startActivity(Intent(this, QrPreviewActivity::class.java).apply {
                putExtra("content", content)
                putExtra("dot_color", style.dotColor)
                putExtra("bg_color", style.bgColor)
                putExtra("dot_shape", style.dotShape.ordinal)
                if (cachedLogoFile.exists()) {
                    putExtra("logo_file_path", cachedLogoFile.absolutePath)
                }
            })
            finish()
            return
        }

        setupGrid()
    }

    private fun setupGrid() {
        val items = listOf(
            QuickItem(R.drawable.ic_wifi, "WiFi", QuickType.WIFI),
            QuickItem(R.drawable.ic_contact, "联系人", QuickType.CONTACT),
            QuickItem(R.drawable.ic_app, "应用", QuickType.APP),
            QuickItem(R.drawable.ic_url, "网站", QuickType.URL),
            QuickItem(R.drawable.ic_phone, "电话", QuickType.PHONE),
            QuickItem(R.drawable.ic_sms, "短信", QuickType.SMS),
            QuickItem(R.drawable.ic_email, "邮件", QuickType.EMAIL)
        )

        binding.quickGrid.layoutManager = GridLayoutManager(this, 4)
        binding.quickGrid.adapter = QuickGridAdapter(items)
    }

    inner class QuickGridAdapter(
        private val items: List<QuickItem>
    ) : RecyclerView.Adapter<QuickGridAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.ivIcon)
            val label: TextView = view.findViewById(R.id.tvLabel)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quick_create, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.icon.setImageResource(item.icon)
            holder.label.text = item.label
            holder.itemView.setOnClickListener {
                startActivity(Intent(this@QuickCreateActivity, QuickItemInputActivity::class.java).apply {
                    putExtra("type", item.type.extraKey)
                })
            }
        }

        override fun getItemCount() = items.size
    }

    enum class QuickType(val extraKey: String) {
        WIFI("wifi"), CONTACT("contact"), APP("app"), URL("url"),
        PHONE("phone"), SMS("sms"), EMAIL("email")
    }
    data class QuickItem(val icon: Int, val label: String, val type: QuickType)
}
