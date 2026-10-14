package com.zcj.qr.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zcj.qr.R
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.data.model.GenerateHistoryEntity
import com.zcj.qr.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

sealed class HistoryItem {
    data class Scan(val entity: ScanHistoryEntity) : HistoryItem()
    data class Generate(val entity: GenerateHistoryEntity) : HistoryItem()
}

class HistoryAdapter(
    private val onDeleteScan: (ScanHistoryEntity) -> Unit = {},
    private val onDeleteGenerate: (GenerateHistoryEntity) -> Unit = {},
    private val onClickScan: (ScanHistoryEntity) -> Unit = {},
    private val onClickGenerate: (GenerateHistoryEntity) -> Unit = {}
) : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is HistoryItem.Scan -> 0
        is HistoryItem.Generate -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryItem.Scan -> bindScanItem(holder, item.entity)
            is HistoryItem.Generate -> bindGenerateItem(holder, item.entity)
        }
    }

    private fun bindScanItem(holder: ViewHolder, entity: ScanHistoryEntity) {
        holder.binding.tvType.text = getDisplayType(entity.type, isScan = true)
        holder.binding.tvContent.text = truncateContent(entity.content, 50)
        holder.binding.tvTime.text = formatTime(entity.timestamp)
        holder.binding.ivTypeIcon.setImageResource(getTypeIcon(entity.type))
        holder.binding.tvTag.text = "📷 扫描"
        holder.binding.tvTag.visibility = android.view.View.VISIBLE
        holder.binding.btnDelete.setOnClickListener { onDeleteScan(entity) }
        holder.binding.root.setOnClickListener { onClickScan(entity) }
        holder.binding.root.setBackgroundColor(
            holder.binding.root.context.getColor(R.color.day_surface)
        )
    }

    private fun bindGenerateItem(holder: ViewHolder, entity: GenerateHistoryEntity) {
        holder.binding.tvType.text = getDisplayType(entity.type, isScan = false)
        holder.binding.tvContent.text = truncateContent(entity.content, 50)
        holder.binding.tvTime.text = formatTime(entity.timestamp)
        holder.binding.ivTypeIcon.setImageResource(getTypeIcon(entity.type))
        holder.binding.tvTag.text = "✏️ 生成"
        holder.binding.tvTag.visibility = android.view.View.VISIBLE
        holder.binding.btnDelete.setOnClickListener { onDeleteGenerate(entity) }
        holder.binding.root.setOnClickListener { onClickGenerate(entity) }
        holder.binding.root.setBackgroundColor(
            holder.binding.root.context.getColor(R.color.day_surface_variant)
        )
    }

    private fun getDisplayType(type: String, isScan: Boolean): String = when (type) {
        "wifi" -> "WiFi"
        "contact" -> "联系人"
        "url" -> "网址"
        "phone" -> "电话"
        "sms" -> "短信"
        "email" -> "邮件"
        "encrypted" -> "🔒 加密"
        else -> if (isScan) "文本" else "生成"
    }

    private fun getTypeIcon(type: String): Int = when (type) {
        "wifi" -> R.drawable.ic_wifi
        "contact" -> R.drawable.ic_contact
        "url" -> R.drawable.ic_url
        "phone" -> R.drawable.ic_phone
        "sms" -> R.drawable.ic_sms
        "email" -> R.drawable.ic_email
        "encrypted" -> R.drawable.ic_custom
        else -> R.drawable.ic_quick_create
    }

    private fun truncateContent(content: String, maxLen: Int): String =
        if (content.length <= maxLen) content else "${content.take(maxLen)}..."

    private fun formatTime(timestamp: Long): String =
        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(java.util.Date(timestamp))

    companion object DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return when {
                oldItem is HistoryItem.Scan && newItem is HistoryItem.Scan -> oldItem.entity.id == newItem.entity.id
                oldItem is HistoryItem.Generate && newItem is HistoryItem.Generate -> oldItem.entity.id == newItem.entity.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
