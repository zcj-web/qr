package com.zcj.qr.ui.scan.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentGalleryScanBinding
import com.zcj.qr.ui.scan.result.ScanResultActivity

class GalleryScanFragment : Fragment() {

    private var _binding: FragmentGalleryScanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryScanViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.scanImage(it) }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            binding.tvError.text = "需要存储权限"
            binding.tvError.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        android.util.Log.d("QrApp", "GalleryScanFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        try {
            setupClickListeners()
            observeResult()
            checkStoragePermission()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "GalleryScanFragment crash: $e", e)
        }
    }

    private fun checkStoragePermission() {
        val needsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        }
        if (needsPermission) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            requestStoragePermission.launch(permission)
        }
    }

    private fun setupClickListeners() {
        binding.btnPickImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun observeResult() {
        viewModel.scannedResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                startActivity(Intent(requireContext(), ScanResultActivity::class.java).apply {
                    putExtra("content", it)
                })
            }
        }
        viewModel.showError.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
