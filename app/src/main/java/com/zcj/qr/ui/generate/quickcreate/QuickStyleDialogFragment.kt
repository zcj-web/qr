package com.zcj.qr.ui.generate.quickcreate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zcj.qr.R
import com.zcj.qr.databinding.DialogQuickStyleBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrStyle

class QuickStyleDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "QuickStyleDialog"
        private const val KEY_STYLE = "style"
        var onDismiss: ((QrStyle, String?) -> Unit)? = null
        fun newInstance(style: QrStyle) = QuickStyleDialogFragment().apply {
            arguments = Bundle().apply { putParcelable(KEY_STYLE, style) }
        }
    }

    private var _binding: DialogQuickStyleBinding? = null
    private val binding get() = _binding!!
    private var logoUri: Uri? = null
    private var currentLogoBitmap: Bitmap? = null
    // 保存最终选择的样式，避免 onDismiss 时 binding 已销毁
    private var lastSelectedStyle = QrStyle()
    private var lastLogoBytes: String? = null

    private val pickLogo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            logoUri = it
            try {
                val stream = requireContext().contentResolver.openInputStream(it)
                currentLogoBitmap = BitmapFactory.decodeStream(stream)
                stream?.close()
                binding.ivLogo.setImageBitmap(currentLogoBitmap)
                binding.ivLogo.setColorFilter(null)
            } catch (_: Exception) {}
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogQuickStyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val style = arguments?.getParcelable<QrStyle>(KEY_STYLE) ?: QrStyle()
        applyStyle(style)
        setupListeners()
    }

    private fun applyStyle(style: QrStyle) {
        // 恢复 RadioGroup 选中状态
        val viewId = when (style.dotShape) {
            DotShape.ROUND -> R.id.rbRound
            DotShape.DIAMOND -> R.id.rbDiamond
            DotShape.CIRCLE -> R.id.rbCircle
            else -> R.id.rbSquare
        }
        binding.rgShape.check(viewId)
        // 恢复颜色
        binding.vDotColor.setBackgroundColor(style.dotColor)
        binding.vBgColor.setBackgroundColor(style.bgColor)
        // 恢复 Logo
        if (style.logoBitmap != null) {
            binding.ivLogo.setImageBitmap(style.logoBitmap)
            binding.ivLogo.setColorFilter(null)
        } else {
            binding.ivLogo.setImageResource(R.drawable.ic_gallery)
            binding.ivLogo.setColorFilter(requireContext().getColor(R.color.day_on_surface_variant))
        }
    }

    private fun setupListeners() {
        // Logo 选择
        binding.ivLogo.setOnClickListener { pickLogo.launch("image/*") }

        // 码点颜色点击
        binding.vDotColor.setOnClickListener { showColorPicker(isDot = true) }
        // 背景颜色点击
        binding.vBgColor.setOnClickListener { showColorPicker(isDot = false) }
    }

    private fun showColorPicker(isDot: Boolean) {
        val colors = intArrayOf(
            0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFF1E293B.toInt(),
            0xFF4F46E5.toInt(), 0xFF7C3AED.toInt(), 0xFFDC2626.toInt(),
            0xFF059669.toInt(), 0xFFD97706.toInt(), 0xFF2563EB.toInt(),
            0xFF6B7280.toInt(), 0xFFEC4899.toInt(), 0xFF0891B2.toInt(),
        )
        val names = arrayOf("白色","黑色","深蓝灰","靛蓝","紫色","红色","绿色","琥珀","蓝色","灰色","粉色","青色")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (isDot) "选择码点颜色" else "选择背景颜色")
            .setItems(names) { _, which ->
                val color = colors[which]
                if (isDot) binding.vDotColor.setBackgroundColor(color)
                else binding.vBgColor.setBackgroundColor(color)
            }
            .show()
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        saveLastSelection()
        onDismiss?.invoke(lastSelectedStyle, lastLogoBytes)
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        saveLastSelection()
        onDismiss?.invoke(lastSelectedStyle, lastLogoBytes)
        super.onDismiss(dialog)
    }

    private fun saveLastSelection() {
        if (_binding != null) {
            lastSelectedStyle = getSelectedStyle()
            lastLogoBytes = getLogoBytes()
        }
    }

    fun getSelectedStyle(): QrStyle = lastSelectedStyle
    fun getLogoBytes(): String? = lastLogoBytes

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
