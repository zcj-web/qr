package com.zcj.qr.ui.scan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentScanTabBinding
import com.zcj.qr.ui.scan.camera.CameraScanViewModel
import com.zcj.qr.ui.scan.gallery.GalleryScanViewModel


class ScanTabFragment : Fragment() {

    private var _binding: FragmentScanTabBinding? = null
    private val binding get() = _binding!!

    private val cameraViewModel: CameraScanViewModel by viewModels()
    private val galleryViewModel: GalleryScanViewModel by viewModels()

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
        setupClickListeners()
        observeResults()
    }

    private fun setupClickListeners() {
        binding.btnCameraScan.setOnClickListener {
            findNavController().navigate(R.id.action_scanTab_to_cameraScan)
        }
        binding.btnGalleryScan.setOnClickListener {
            findNavController().navigate(R.id.action_scanTab_to_galleryScan)
        }
        binding.btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), com.zcj.qr.ui.history.HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeResults() {
        cameraViewModel.scannedResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                val bundle = Bundle().also { it.putString("content", result) }
                findNavController().navigate(R.id.action_scanTab_to_result, bundle)
            }
        }
        galleryViewModel.scannedResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                val bundle = Bundle().also { it.putString("content", result) }
                findNavController().navigate(R.id.action_scanTab_to_result, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
