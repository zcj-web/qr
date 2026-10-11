package com.zcj.qr.ui.scan.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentGalleryScanBinding
import android.content.Intent

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
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeResult()
        checkStoragePermission()
    }

    private fun checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
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
                findNavController().navigate(
                    R.id.action_scanTab_to_result,
                    Bundle().also { b -> b.putString("content", it) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
