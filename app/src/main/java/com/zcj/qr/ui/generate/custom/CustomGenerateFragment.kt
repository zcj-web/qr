package com.zcj.qr.ui.generate.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentCustomGenerateBinding


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
                1 -> 500_000
                else -> 100_000
            }

            viewModel.generate(content, useEncryption, password, iterations)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun observeState() {
        viewModel.generatedBitmap.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.let {
                binding.ivQr.setImageBitmap(it)
                findNavController().navigate(
                    R.id.action_customGenerateFragment_to_preview,
                    Bundle().also { it.putBoolean("bitmapExists", true) }
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
