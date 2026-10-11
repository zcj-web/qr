package com.zcj.qr.ui.generate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.FragmentGenerateTabBinding
import com.zcj.qr.util.QrCodeGenerator
import com.zcj.qr.util.QrStyle
import com.zcj.qr.util.DotShape
import com.zcj.qr.ui.generate.quickcreate.QuickCreateDialogFragment

class GenerateTabFragment : Fragment() {

    private var _binding: FragmentGenerateTabBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { HistoryRepository(QrDatabase.getDatabase(requireContext())) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerateTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupResultListener()
    }

    private fun setupClickListeners() {
        binding.btnQuickCreate.setOnClickListener {
            findNavController().navigate(R.id.action_generateTab_to_quickCreate)
        }
        binding.btnCustomText.setOnClickListener {
            findNavController().navigate(R.id.action_generateTab_to_customGenerate)
        }
        binding.btnHistory.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.zcj.qr.ui.history.HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener(QuickCreateDialogFragment.RESULT_KEY) { _, bundle ->
            val content = bundle.getString("content") ?: return@setFragmentResultListener
            val type = bundle.getString("type") ?: "text"
            val isEncrypted = content.startsWith("qr://")

            lifecycleScope.launch {
                repository.addGenerate(content, type, isEncrypted)
            }

            val style = QrStyle(dotShape = DotShape.ROUND)
            val bitmap = QrCodeGenerator.generate(content, size = 800, style = style)

            val intent = android.content.Intent(requireContext(), com.zcj.qr.ui.generate.preview.QrPreviewActivity::class.java).apply {
                putExtra("content", content)
                putExtra("bitmap", bitmap)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
