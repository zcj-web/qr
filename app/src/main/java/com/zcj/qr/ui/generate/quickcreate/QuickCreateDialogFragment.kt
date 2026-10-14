package com.zcj.qr.ui.generate.quickcreate

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zcj.qr.R
import com.zcj.qr.databinding.DialogWifiBinding
import com.zcj.qr.databinding.DialogContactBinding
import com.zcj.qr.databinding.DialogUrlBinding
import com.zcj.qr.databinding.DialogPhoneBinding
import com.zcj.qr.databinding.DialogSmsBinding
import com.zcj.qr.databinding.DialogEmailBinding
import com.zcj.qr.ui.generate.quickcreate.QuickCreateFragment.QuickType

class QuickCreateDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "QuickCreateDialog"
        const val RESULT_KEY = "quick_create_result"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val type = args.getString("type") ?: return super.onCreateDialog(savedInstanceState)
        val label = args.getString("label") ?: ""

        return when (type) {
            QuickType.WIFI.name -> createWifiDialog()
            QuickType.CONTACT.name -> createContactDialog()
            QuickType.APP.name, QuickType.URL.name -> createUrlDialog()
            QuickType.PHONE.name -> createPhoneDialog()
            QuickType.SMS.name -> createSmsDialog()
            QuickType.EMAIL.name -> createEmailDialog()
            else -> super.onCreateDialog(savedInstanceState)
        }
    }

    private fun createWifiDialog(): Dialog {
        val binding = DialogWifiBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("WiFi 二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val ssid = binding.etSsid.text?.toString()?.trim() ?: ""
                val password = binding.etPassword.text?.toString()?.trim() ?: ""
                val encryption = when {
                    binding.radioWpa.isChecked -> "WPA"
                    binding.radioWep.isChecked -> "WEP"
                    else -> "nopass"
                }
                val wifiStr = buildWifiString(ssid, password, encryption)
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", wifiStr)
                    it.putString("type", "wifi")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun createContactDialog(): Dialog {
        val binding = DialogContactBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("联系人二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val vcard = buildVCard(
                    binding.etName.text?.toString() ?: "",
                    binding.etPhone.text?.toString() ?: "",
                    binding.etEmail.text?.toString() ?: ""
                )
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", vcard)
                    it.putString("type", "contact")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun createUrlDialog(): Dialog {
        val binding = DialogUrlBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("网址二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val url = binding.etUrl.text?.toString()?.trim() ?: ""
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", url)
                    it.putString("type", "url")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun createPhoneDialog(): Dialog {
        val binding = DialogPhoneBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("电话二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val phone = binding.etPhone.text?.toString()?.trim() ?: ""
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", "tel:$phone")
                    it.putString("type", "phone")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun createSmsDialog(): Dialog {
        val binding = DialogSmsBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("短信二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val phone = binding.etPhone.text?.toString()?.trim() ?: ""
                val message = binding.etMessage.text?.toString()?.trim() ?: ""
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", "smsto:$phone:$message")
                    it.putString("type", "sms")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun createEmailDialog(): Dialog {
        val binding = DialogEmailBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("邮件二维码")
            .setView(binding.root)
            .setPositiveButton("生成") { _, _ ->
                val email = binding.etEmail.text?.toString()?.trim() ?: ""
                val subject = binding.etSubject.text?.toString()?.trim() ?: ""
                val body = binding.etBody.text?.toString()?.trim() ?: ""
                setFragmentResult(RESULT_KEY, Bundle().also { 
                    it.putString("content", "mailto:$email?subject=$subject&body=$body")
                    it.putString("type", "email")
                })
                dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun buildWifiString(ssid: String, password: String, encryption: String): String {
        return if (encryption == "nopass") {
            "WIFI:S:$ssid;T:nopass;;"
        } else {
            "WIFI:S:$ssid;T:$encryption;P:$password;;"
        }
    }

    private fun buildVCard(name: String, phone: String, email: String): String {
        return buildString {
            append("BEGIN:VCARD\nVERSION:3.0\n")
            append("FN:$name\n")
            if (phone.isNotEmpty()) append("TEL:$phone\n")
            if (email.isNotEmpty()) append("EMAIL:$email\n")
            append("END:VCARD")
        }
    }
}
