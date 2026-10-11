package com.zcj.qr

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zcj.qr.ui.generate.GenerateTabFragment
import com.zcj.qr.ui.scan.ScanTabFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ScanTabFragment()
        else -> GenerateTabFragment()
    }
}
