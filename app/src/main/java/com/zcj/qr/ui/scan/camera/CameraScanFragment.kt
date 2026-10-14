package com.zcj.qr.ui.scan.camera

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.ResultPoint
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.zcj.qr.R
import com.zcj.qr.data.QrDatabase
import com.zcj.qr.data.repository.HistoryRepository
import com.zcj.qr.databinding.FragmentCameraScanBinding
import com.zcj.qr.ui.scan.result.ScanResultActivity
import com.zcj.qr.util.ProtocolHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraScanFragment : Fragment() {

    private var _binding: FragmentCameraScanBinding? = null
    private val binding get() = _binding!!
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
        android.util.Log.d("QrApp", "CameraScanFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        try {
            setupBarcodeScanner()
            setupToggleFlash()
            setupBackButton()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "CameraScanFragment crash: $e", e)
        }
    }

    private fun setupBarcodeScanner() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (isScanning) {
                    result?.let {
                        isScanning = false
                        binding.barcodeScanner.pause()
                        val content = it.text
                        lifecycleScope.launch {
                            val type = ProtocolHandler.parse(content).let { t ->
                                when (t) {
                                    is com.zcj.qr.util.ProtocolType.WIFI -> "wifi"
                                    is com.zcj.qr.util.ProtocolType.CONTACT -> "contact"
                                    is com.zcj.qr.util.ProtocolType.URL -> "url"
                                    is com.zcj.qr.util.ProtocolType.PHONE -> "phone"
                                    is com.zcj.qr.util.ProtocolType.SMS -> "sms"
                                    is com.zcj.qr.util.ProtocolType.EMAIL -> "email"
                                    is com.zcj.qr.util.ProtocolType.ENCRYPTED -> "encrypted"
                                    else -> "text"
                                }
                            }
                            val isEncrypted = content.startsWith("qr://")
                            withContext(Dispatchers.IO) {
                                HistoryRepository(QrDatabase.getDatabase(requireContext())).addScan(content, type, isEncrypted)
                            }
                        }
                        val intent = Intent(requireContext(), ScanResultActivity::class.java).apply {
                            putExtra("content", content)
                        }
                        startActivity(intent)
                        requireActivity().finish()
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
