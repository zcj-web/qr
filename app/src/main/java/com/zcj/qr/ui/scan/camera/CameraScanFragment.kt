package com.zcj.qr.ui.scan.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentCameraScanBinding

class CameraScanFragment : Fragment() {

    private var _binding: FragmentCameraScanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraScanViewModel by viewModels()
    private var isScanning = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBarcodeScanner()
        setupToggleFlash()
        setupBackButton()
    }

    private fun setupBarcodeScanner() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (isScanning) {
                    result?.let {
                        viewModel.onScanResult(it.text)
                        isScanning = false
                        binding.barcodeScanner.pause()
                        findNavController().navigate(
                            R.id.action_scanTab_to_result,
                            Bundle().also { b -> b.putString("content", it.text) }
                        )
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
        binding.barcodeScanner.resume()
    }

    private fun setupToggleFlash() {
        binding.btnFlash.setOnClickListener {
            // Flash toggle not available in this ZXing version
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            isScanning = false
            binding.barcodeScanner.pause()
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isScanning) binding.barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        if (isScanning) binding.barcodeScanner.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
