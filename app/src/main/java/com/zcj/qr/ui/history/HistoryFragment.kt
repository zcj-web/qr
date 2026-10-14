package com.zcj.qr.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zcj.qr.databinding.FragmentScanHistoryBinding
import com.zcj.qr.ui.history.HistoryAdapter
import com.zcj.qr.ui.history.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentScanHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        android.util.Log.d("QrApp", "HistoryFragment onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        try {
            setupRecyclerView()
            observeData()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "HistoryFragment crash: $e", e)
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onDeleteScan = { entity -> viewModel.deleteScan(entity.id) },
            onDeleteGenerate = { entity -> viewModel.deleteGenerate(entity.id) }
        )
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.scanHistory.collectLatest { scanList ->
                viewModel.generateHistory.collectLatest { generateList ->
                    val allItems = (scanList.map { HistoryItem.Scan(it) } + generateList.map { HistoryItem.Generate(it) })
                        .sortedByDescending { 
                            when (it) {
                                is HistoryItem.Scan -> it.entity.timestamp
                                is HistoryItem.Generate -> it.entity.timestamp
                            }
                        }
                    adapter.submitList(allItems)
                    binding.tvEmpty.visibility = if (allItems.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
