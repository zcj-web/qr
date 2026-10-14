package com.zcj.qr.ui.generate.quickcreate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.zcj.qr.R
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.FragmentQuickCreateBinding
import com.zcj.qr.ui.generate.preview.QrPreviewActivity
import com.zcj.qr.ui.generate.quickcreate.QuickCreateFragment.QuickType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuickCreateFragment : Fragment() {

    private var _binding: FragmentQuickCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGrid()
        setupResultListener()
    }

    /** 监听 QuickCreateDialogFragment 的结果，生成后导航到预览 */
    private fun setupResultListener() {
        setFragmentResultListener(QuickCreateDialogFragment.RESULT_KEY) { _, bundle ->
            val content = bundle.getString("content") ?: return@setFragmentResultListener
            val type = bundle.getString("type", "text")
            val prefs = requireActivity().getSharedPreferences("qr_style_pref", android.content.Context.MODE_PRIVATE)
            val dotColor = prefs.getInt("dot_color", 0xFF1E293B.toInt())
            val bgColor = prefs.getInt("bg_color", 0xFFFFFFFF.toInt())
            val shapeIdx = prefs.getInt("dot_shape", 0)
            val logoFilePath = prefs.getString("logo_file_path", null)

            // 保存样式到数据库，确保历史点击查看时显示正确颜色
            val styleJson = "$shapeIdx:${Integer.toHexString(dotColor and 0x00FFFFFF)}:${Integer.toHexString(bgColor and 0x00FFFFFF)}"
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val db = QrDatabase.getDatabase(requireActivity().applicationContext)
                    val repo = HistoryRepository(db)
                    repo.addGenerate(content, "quick_dialog", false, styleJson, logoFilePath)
                } catch (e: Exception) {
                    android.util.Log.e("QuickCreateFragment", "history save failed: ${e.message}", e)
                }
            }

            val intent = android.content.Intent(requireContext(), QrPreviewActivity::class.java).apply {
                putExtra("content", content)
                putExtra("dot_color", dotColor)
                putExtra("bg_color", bgColor)
                putExtra("dot_shape", shapeIdx)
                logoFilePath?.let { putExtra("logo_file_path", it) }
            }
            startActivity(intent)
        }
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

        binding.quickGrid.removeAllViews()
        items.forEach { item ->
            val icon = item.icon
            val label = item.label
            val type = item.type
            val card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_quick_create, binding.quickGrid, false)

            card.findViewById<android.widget.ImageView>(R.id.ivIcon)?.setImageResource(icon)
            card.findViewById<android.widget.TextView>(R.id.tvLabel)?.text = label
            card.setOnClickListener {
                val intent = Intent(requireContext(), QuickCreateActivity::class.java).apply {
                    putExtra("type", type.name)
                    putExtra("label", label)
                }
                startActivity(intent)
            }
            binding.quickGrid.addView(card)
        }
    }

    enum class QuickType { WIFI, CONTACT, APP, URL, PHONE, SMS, EMAIL }

    data class QuickItem(val icon: Int, val label: String, val type: QuickType)
}
