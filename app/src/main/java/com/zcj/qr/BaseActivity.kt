package com.zcj.qr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal const val PREF_THEME_MODE = "theme_mode"
internal const val MODE_AUTO = -1
internal const val MODE_LIGHT = 1 // AppCompatDelegate.MODE_NIGHT_NO
internal const val MODE_NIGHT = 2 // AppCompatDelegate.MODE_NIGHT_YES

/** 所有 Activity 继承此类，自动应用存储的主题（日间/夜间） */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyStoredTheme()
        super.onCreate(savedInstanceState)
    }

    /** 根据 SharedPreferences 中的存储模式设置主题，必须在 super.onCreate() 之前调用 */
    private fun applyStoredTheme() {
        val prefs = getSharedPreferences(PREF_THEME_MODE, MODE_PRIVATE)
        val mode = prefs.getInt(PREF_THEME_MODE, MODE_AUTO)
        val useNight = mode == MODE_NIGHT ||
            (mode == MODE_AUTO && resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES)
        setTheme(if (useNight) R.style.Theme_QrCode_Night else R.style.Theme_QrCode)
    }

    /** 切换主题后调用，存入 SharedPreferences */
    fun saveThemeMode(mode: Int) {
        getSharedPreferences(PREF_THEME_MODE, MODE_PRIVATE)
            .edit()
            .putInt(PREF_THEME_MODE, mode)
            .apply()
    }

    /** 读取当前存储的主题模式 */
    fun getStoredThemeMode(): Int =
        getSharedPreferences(PREF_THEME_MODE, MODE_PRIVATE).getInt(PREF_THEME_MODE, MODE_AUTO)
}
