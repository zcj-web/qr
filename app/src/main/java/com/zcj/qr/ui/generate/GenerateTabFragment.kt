package com.zcj.qr.ui.generate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentGenerateTabBinding
import com.zcj.qr.ui.generate.quickcreate.QuickCreateActivity
import com.zcj.qr.ui.generate.custom.CustomGenerateActivity
import com.zcj.qr.ui.history.HistoryActivity
import android.content.Intent

class GenerateTabFragment : Fragment() {

    private var _binding: FragmentGenerateTabBinding? = null
    private val binding get() = _binding!!

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
        try {
            setupClickListeners()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "GenerateTabFragment crash: $e", e)
        }
    }

    private fun setupClickListeners() {
        binding.btnQuickCreate.setOnClickListener {
            startActivity(Intent(requireContext(), QuickCreateActivity::class.java))
        }
        binding.btnCustomText.setOnClickListener {
            startActivity(Intent(requireContext(), CustomGenerateActivity::class.java))
        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
