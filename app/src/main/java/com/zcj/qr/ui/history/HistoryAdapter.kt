package com.zcj.qr.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zcj.qr.R
import com.zcj.qr.data.model.ScanHistoryEntity
import com.zcj.qr.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onDelete: (ScanHistoryEntity) -> Unit
) : ListAdapter<ScanHistoryEntity, HistoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvType.text = getDisplayType(item.type)
        holder.binding.tvContent.text = truncateContent(item.content, 50)
        holder.binding.tvTime.text = formatTime(item.timestamp)
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
    }

    private fun getDisplayType(type: String): String = when (type) {
        "wifi" -> "WiFi"
        "contact" -> "联系人"
        "url" -> "网址"
        "phone" -> "电话"
        "sms" -> "短信"
        "email" -> "邮件"
        "encrypted" -> "🔒 加密"
        else -> "文本"
    }

    private fun truncateContent(content: String, maxLen: Int): String =
        if (content.length <= maxLen) content else "${content.take(maxLen)}..."

    private fun formatTime(timestamp: Long): String =
        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(java.util.Date(timestamp))

    companion object DiffCallback : DiffUtil.ItemCallback<ScanHistoryEntity>() {
        override fun areItemsTheSame(oldItem: ScanHistoryEntity, newItem: ScanHistoryEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ScanHistoryEntity, newItem: ScanHistoryEntity) =
            oldItem == newItem
    }
}
