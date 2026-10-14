package com.zcj.qr.ui.scan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentScanTabBinding
import com.zcj.qr.ui.scan.camera.CameraScanActivity
import com.zcj.qr.ui.scan.camera.CameraScanViewModel
import com.zcj.qr.ui.scan.gallery.GalleryScanActivity
import com.zcj.qr.ui.scan.result.ScanResultActivity
import com.zcj.qr.ui.history.HistoryActivity

class ScanTabFragment : Fragment() {

    private var _binding: FragmentScanTabBinding? = null
    private val binding get() = _binding!!

    private val cameraViewModel: CameraScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            setupClickListeners()
            observeResults()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "ScanTabFragment crash: $e", e)
        }
    }

    private fun setupClickListeners() {
        binding.btnCameraScan.setOnClickListener {
            startActivity(Intent(requireContext(), CameraScanActivity::class.java))
        }
        binding.btnGalleryScan.setOnClickListener {
            startActivity(Intent(requireContext(), GalleryScanActivity::class.java))
        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }
    }

    private fun observeResults() {
        cameraViewModel.scannedResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                startActivity(Intent(requireContext(), ScanResultActivity::class.java).apply {
                    putExtra("content", it)
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
