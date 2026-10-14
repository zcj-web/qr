package com.zcj.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zcj.qr.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, "需要相机权限才能扫描", Toast.LENGTH_LONG).show()
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupTabs()
            setupThemeToggle()
            checkPermissions()
        } catch (e: Exception) {
            android.util.Log.e("QrApp", "MainActivity onCreate crash: " + e.toString(), e)
            e.printStackTrace()
            finishAffinity()
        }
    }

    private fun cycleThemeMode(): Int {
        val current = getStoredThemeMode()
        val next = when (current) {
            MODE_LIGHT -> MODE_NIGHT
            MODE_NIGHT -> MODE_AUTO
            else -> MODE_LIGHT
        }
        saveThemeMode(next)
        recreate()
        return next
    }

    private fun setupThemeToggle() {
        binding.btnThemeToggle.setOnClickListener { cycleThemeMode() }
        updateThemeIcon()
    }

    private fun updateThemeIcon() {
        val mode = getStoredThemeMode()
        binding.btnThemeToggle.setImageResource(
            when (mode) {
                MODE_LIGHT -> R.drawable.ic_sun
                MODE_NIGHT -> R.drawable.ic_moon
                else -> R.drawable.ic_sun
            }
        )
    }

    private fun setupTabs() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "扫描"
                else -> "生成"
            }
        }.attach()

        applyGlassStyleToTabLayout()
    }

    private fun applyGlassStyleToTabLayout() {
        val night = getStoredThemeMode() == MODE_NIGHT ||
            (getStoredThemeMode() == MODE_AUTO &&
                resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES)
        binding.tabLayout.background = ContextCompat.getDrawable(
            this, if (night) R.drawable.glass_card_night else R.drawable.glass_card_day
        )
        binding.tabLayout.setSelectedTabIndicatorColor(
            ContextCompat.getColor(this, if (night) R.color.night_primary else R.color.day_primary)
        )
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    fun requestCameraPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        updateThemeIcon()
    }
}