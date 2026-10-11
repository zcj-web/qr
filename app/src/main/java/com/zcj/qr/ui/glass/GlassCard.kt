package com.zcj.qr.ui.glass

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.zcj.qr.R
import com.zcj.qr.databinding.GlassCardBinding

class GlassCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = GlassCardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val isDark = ContextCompat.getColor(context, R.color.night_surface) == 0xFF000000.toInt()
        val bgColor = if (isDark) 0xCC000000.toInt() else 0xCCFFFFFF.toInt()
        setBackgroundColor(bgColor)
        setupBlurEffect()
    }

    private fun setupBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setRenderEffect(
                RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.MIRROR)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            elevation = 8f
        }
        background?.alpha = 204
    }
}
