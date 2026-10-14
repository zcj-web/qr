package com.zcj.qr.ui.generate.custom

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentCustomGenerateBinding
import com.zcj.qr.util.DotShape
import com.zcj.qr.util.QrStyle
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomGenerateFragment : Fragment() {

    private var _binding: FragmentCustomGenerateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomGenerateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomGenerateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        observeState()
    }

    private fun setupUi() {
        binding.btnGenerate.setOnClickListener {
            val content = binding.etContent.text?.toString()?.trim() ?: ""
            if (content.isEmpty()) {
                binding.etContent.error = "请输入内容"
                return@setOnClickListener
            }
            val useEncryption = binding.switchEncrypt.isChecked
            val password = binding.etPassword.text?.toString() ?: ""
            val iterations = when (binding.spinnerIterations.selectedItemPosition) {
                0 -> 10_000
                1 -> 100_000
                else -> 500_000
            }

            viewModel.generate(content, useEncryption, password, iterations)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun getSavedStyle(): QrStyle {
        val prefs = requireActivity().getSharedPreferences("qr_style_pref", Activity.MODE_PRIVATE)
        return QrStyle(
            dotColor = prefs.getInt("dot_color", 0xFF1E293B.toInt()),
            bgColor = prefs.getInt("bg_color", 0xFFFFFFFF.toInt()),
            dotShape = DotShape.values().getOrNull(prefs.getInt("dot_shape", 0)) ?: DotShape.SQUARE
        )
    }

    private fun observeState() {
        viewModel.generatedContent.observe(viewLifecycleOwner) { content ->
            content?.let {
                // 确保导航前 SharedPreferences 已写入最新样式
                val prefs = requireActivity().getSharedPreferences("qr_style_pref", Activity.MODE_PRIVATE)
                android.util.Log.d("CustomGenFragment", "observeState: saving style")
                prefs.edit()
                    .putInt("dot_color", (binding.vDotColor.background as android.graphics.drawable.ColorDrawable).color)
                    .putInt("bg_color", (binding.vBgColor.background as android.graphics.drawable.ColorDrawable).color)
                    .apply()
                val savedStyle = getSavedStyle()
                android.util.Log.d("CustomGenFragment", "observeState: savedStyle.shape=${savedStyle.dotShape}, logo_path=${prefs.getString("logo_file_path", "")}")

                val bundle = Bundle().also { b ->
                    b.putString("content", it)
                    b.putParcelable("style", savedStyle)
                }

                // logo 仅通过 SharedPreferences 传递给预览页，不存入数据库（避免大数据导致 CursorWindow 崩溃）
                val styleJson = "${savedStyle.dotShape.ordinal}:${Integer.toHexString(savedStyle.dotColor and 0x00FFFFFF)}:${Integer.toHexString(savedStyle.bgColor and 0x00FFFFFF)}"
                val useEncryption = binding.switchEncrypt.isChecked
                android.util.Log.d("CustomGenFragment", "saving history: shape=${savedStyle.dotShape}, useEnc=$useEncryption")
                // 异步保存历史记录（GlobalScope 不与 Fragment 生命周期绑定）
                kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val db = QrDatabase.getDatabase(requireActivity().applicationContext)
                        val repo = HistoryRepository(db)
                        // 从 SharedPreferences 读取 logo 缓存路径
                        val logoFilePath = prefs.getString("logo_file_path", null)
                        repo.addGenerate(it, "custom_text", useEncryption, styleJson, logoFilePath)
                        android.util.Log.d("CustomGenFragment", "history saved OK, logoPath=$logoFilePath")
                    } catch (e: Exception) {
                        android.util.Log.e("CustomGenFragment", "history save FAILED: ${e.message}", e)
                    }
                }

                android.util.Log.d("CustomGenFragment", "observeState: navigating with bundle=$bundle")
                findNavController().navigate(
                    R.id.action_customGenerateFragment_to_preview,
                    bundle
                )
            }
        }
        viewModel.showError.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
