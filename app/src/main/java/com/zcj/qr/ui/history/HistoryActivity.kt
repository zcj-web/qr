package com.zcj.qr.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zcj.qr.BaseActivity
import com.zcj.qr.R
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.ActivityHistoryBinding
import com.zcj.qr.ui.generate.preview.QrPreviewActivity
import com.zcj.qr.ui.scan.result.ScanResultActivity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val repository by lazy { HistoryRepository(QrDatabase.getDatabase(applicationContext)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnClearAll.setOnClickListener { showClearConfirmDialog() }

        setupRecyclerView()
        observeHistory()

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun showClearConfirmDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("清空历史记录")
            .setMessage("确定要清除所有扫描和生成记录吗？此操作不可撤销。")
            .setPositiveButton("清空") { _, _ ->
                lifecycleScope.launch {
                    repository.clearScans()
                    repository.clearGenerates()
                }
                Toast.makeText(this, "已清空", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onDeleteScan = { entity ->
                lifecycleScope.launch { repository.deleteScan(entity.id) }
            },
            onDeleteGenerate = { entity ->
                lifecycleScope.launch { repository.deleteGenerate(entity.id) }
            },
            onClickScan = { entity ->
                startActivity(Intent(this, ScanResultActivity::class.java).apply {
                    putExtra("content", entity.content)
                })
            },
            onClickGenerate = { entity ->
                val prefs = getSharedPreferences("qr_style_pref", MODE_PRIVATE)
                val entityStyle = entity.styleJson ?: ""
                var dotColor = 0xFF1E293B.toInt()
                var bgColor = 0xFFFFFFFF.toInt()
                var shapeIdx = 0
                if (entityStyle.isNotEmpty()) {
                    val parts = entityStyle.split(":")
                    if (parts.size >= 3) {
                        shapeIdx = parts[0].toIntOrNull() ?: 0
                        dotColor = 0xFF000000.toInt() or (parts[1].toIntOrNull(16) ?: 0xFF1E293B.toInt())
                        bgColor = 0xFF000000.toInt() or (parts[2].toIntOrNull(16) ?: 0xFFFFFFFF.toInt())
                    }
                }
                prefs.edit()
                    .putInt("dot_shape", shapeIdx)
                    .putInt("dot_color", dotColor)
                    .putInt("bg_color", bgColor)
                    .apply()

                android.util.Log.d("HistoryActivity", "restore style: shape=$shapeIdx, dotColor=0x${Integer.toHexString(dotColor)}, bgColor=0x${Integer.toHexString(bgColor)}, logoPath=${entity.logoFilePath}")

                val intent = Intent(this, QrPreviewActivity::class.java).apply {
                    putExtra("content", entity.content)
                    putExtra("dot_color", dotColor)
                    putExtra("bg_color", bgColor)
                    putExtra("dot_shape", shapeIdx)
                    entity.logoFilePath?.let { putExtra("logo_file_path", it) }
                }
                startActivity(intent)
            }
        )
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }
    }

    private fun observeHistory() {
        lifecycleScope.launch {
            combine(
                repository.scanHistory,
                repository.generateHistory
            ) { scans, generates ->
                val scanItems = scans.map { HistoryItem.Scan(it) }
                val generateItems = generates.map { HistoryItem.Generate(it) }
                (scanItems + generateItems).sortedByDescending { 
                    when (it) {
                        is HistoryItem.Scan -> it.entity.timestamp
                        is HistoryItem.Generate -> it.entity.timestamp
                    }
                }
            }.collectLatest { allItems ->
                adapter.submitList(allItems)
                binding.tvEmpty.visibility = if (allItems.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
