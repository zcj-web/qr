package com.zcj.qr.ui.generate.quickcreate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zcj.qr.R
import com.zcj.qr.databinding.FragmentQuickCreateBinding
import com.zcj.qr.ui.generate.quickcreate.QuickCreateFragment.QuickType

class QuickCreateFragment : Fragment() {

    private var _binding: FragmentQuickCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGrid()
    }

    private fun setupGrid() {
        val items = listOf(
            QuickItem(R.drawable.ic_wifi, "WiFi", QuickType.WIFI),
            QuickItem(R.drawable.ic_contact, "联系人", QuickType.CONTACT),
            QuickItem(R.drawable.ic_app, "应用", QuickType.APP),
            QuickItem(R.drawable.ic_url, "网站", QuickType.URL),
            QuickItem(R.drawable.ic_phone, "电话", QuickType.PHONE),
            QuickItem(R.drawable.ic_sms, "短信", QuickType.SMS),
            QuickItem(R.drawable.ic_email, "邮件", QuickType.EMAIL)
        )

        binding.quickGrid.removeAllViews()
        items.forEach { item ->
            val icon = item.icon
            val label = item.label
            val type = item.type
            val card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_quick_create, binding.quickGrid, false)

            card.findViewById<android.widget.ImageView>(R.id.ivIcon)?.setImageResource(icon)
            card.findViewById<android.widget.TextView>(R.id.tvLabel)?.text = label
            card.setOnClickListener {
                val bundle = Bundle().also { 
            it.putString("type", type.name)
            it.putString("label", label)
        }
                findNavController().navigate(R.id.action_quickCreateFragment_to_dialog, bundle)
            }
            binding.quickGrid.addView(card)
        }
    }

    enum class QuickType { WIFI, CONTACT, APP, URL, PHONE, SMS, EMAIL }

    data class QuickItem(val icon: Int, val label: String, val type: QuickType)
}
