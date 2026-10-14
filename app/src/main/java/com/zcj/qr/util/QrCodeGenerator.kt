package com.zcj.qr.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcelable
import androidx.core.graphics.scale
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap
import kotlinx.parcelize.Parcelize

enum class DotShape { SQUARE, ROUND, DIAMOND, CIRCLE }

@Parcelize
data class QrStyle(
    val dotColor: Int = 0xFF1E293B.toInt(),
    val bgColor: Int = 0xFFFFFFFF.toInt(),
    val bgGradientEnd: Int? = null,
    val dotShape: DotShape = DotShape.SQUARE,
    @Transient val logoBitmap: android.graphics.Bitmap? = null,
    val logoSizeRatio: Float = 0.10f,
    val marginModules: Int = 4
) : Parcelable

/** 计算两色之间的亮度差（0-255），低于此值则难以被扫码器识别 */
private const val MIN_LUMINANCE_DIFF = 30

/** 提取颜色的亮度分量（ITU-R BT.601 标准） */
private fun luminance(color: Int): Int {
    val r = color ushr 16 and 0xFF
    val g = color ushr 8 and 0xFF
    val b = color and 0xFF
    return (r * 0.299 + g * 0.587 + b * 0.114).toInt()
}

/**
 * 校验颜色对比度，若低于阈值则自动修正为黑白高对比版本。
 * 确保生成的彩色二维码仍可被标准扫码器（微信/系统相机等）识别。
 */
fun ensureScannableStyle(style: QrStyle): QrStyle {
    val dotLum = luminance(style.dotColor)
    val bgLum = luminance(style.bgColor)
    val diff = Math.abs(dotLum - bgLum)
    if (diff >= MIN_LUMINANCE_DIFF) return style
    // 低对比度：根据哪种颜色更暗来决定用白底黑码还是黑底白码
    return if (dotLum < bgLum) {
        // 深色码点 + 浅色背景 → 强制黑白
        style.copy(dotColor = 0xFF000000.toInt(), bgColor = 0xFFFFFFFF.toInt())
    } else {
        // 浅色码点 + 深色背景 → 强制反色黑白
        style.copy(dotColor = 0xFFFFFFFF.toInt(), bgColor = 0xFF000000.toInt())
    }
}

object QrCodeGenerator {
    fun generate(content: String, size: Int = 800, style: QrStyle = QrStyle()): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            // marginModules: 静區模塊數（標準建議 >=4），比固定像素更可靠
            put(EncodeHintType.MARGIN, style.marginModules)
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            // 有 Logo 时使用 H 级纠错（可恢复 30% 损坏），无 Logo 用 M（15%）
            put(EncodeHintType.ERROR_CORRECTION,
                if (style.logoBitmap != null)
                    com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H
                else
                    com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M
            )
        }
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Background (solid or gradient)
        if (style.bgGradientEnd != null) {
            val paint = android.graphics.LinearGradient(
                0f, 0f, size.toFloat(), size.toFloat(),
                style.bgColor, style.bgGradientEnd,
                android.graphics.Shader.TileMode.CLAMP
            )
            val bgPaint = Paint().apply { this.shader = paint }
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)
        } else {
            canvas.drawColor(style.bgColor)
        }

        // Draw QR modules with custom shape and color
        // 使用 Paint 时明确设置 FILL 模式，避免 stroke 状态污染
        // 注意：不能用 style 作变量名，因为参数名已是 style
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = style.dotColor
            this.style = android.graphics.Paint.Style.FILL
        }

        val moduleSize = size.toFloat() / bitMatrix.width
        val radius = moduleSize * 0.46f  // 圆形半径：留 8% 边距
        val roundRadius = moduleSize * 0.18f  // 圆角半径：柔和圆角

        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                if (!bitMatrix.get(x, y)) continue

                val left = x * moduleSize
                val top = y * moduleSize
                val right = (x + 1) * moduleSize
                val bottom = (y + 1) * moduleSize
                val cx = (left + right) / 2f
                val cy = (top + bottom) / 2f

                when (style.dotShape) {
                    DotShape.SQUARE -> canvas.drawRect(left, top, right, bottom, dotPaint)
                    DotShape.ROUND -> canvas.drawRoundRect(left, top, right, bottom, roundRadius, roundRadius, dotPaint)
                    DotShape.DIAMOND -> {
                        // 菱形：连接四边中点，每次新建 Path（QR码模块数通常 < 500，开销可接受）
                        val diamondPath = Path().apply {
                            moveTo(cx, top)
                            lineTo(right, cy)
                            lineTo(cx, bottom)
                            lineTo(left, cy)
                            close()
                        }
                        canvas.drawPath(diamondPath, dotPaint)
                    }
                    DotShape.CIRCLE -> canvas.drawCircle(cx, cy, radius, dotPaint)
                }
            }
        }

        // Draw logo centered on QR code
        // 关键：Logo 图通常带有透明/白色背景，需先裁剪掉无效边距，只保留实际内容
        // 这样 Logo 的透明区域可以透出下方 QR 模块，保证可扫描性
        style.logoBitmap?.let { logo ->
            val croppedLogo = cropLogoContent(logo)
            val logoSize = (size * style.logoSizeRatio).toInt()
            val scaledLogo = croppedLogo.scale(logoSize, logoSize)
            val left = (size - logoSize) / 2
            val top = (size - logoSize) / 2
            canvas.drawBitmap(scaledLogo, left.toFloat(), top.toFloat(), null)
        }

        return result
    }
}

fun Bitmap.scale(width: Int, height: Int): Bitmap {
    return if (this.width == width && this.height == height) this
    else Bitmap.createScaledBitmap(this, width, height, true)
}

/**
 * 裁剪 Logo 图片的有效内容区域（去除纯色背景边距）
 * 原理：从四边向内扫描，跳过与左上角颜色相近的像素，直到遇到内容为止
 * 效果：Logo 透明区域透出下方 QR 模块，保证可扫描性
 */
fun cropLogoContent(bitmap: Bitmap): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    if (w <= 1 || h <= 1) return bitmap
    val pixels = IntArray(w * h)
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

    // 采样四角颜色作为背景色
    fun getBgColor(): Int {
        val samples = intArrayOf(
            pixels[0], pixels[w - 1],
            pixels[(h - 1) * w], pixels[(h - 1) * w + w - 1]
        )
        return samples[0]
    }
    val bgColor = getBgColor()

    // 判断是否为背景色（容差 20）
    fun isBgColor(px: Int): Boolean {
        val dr = (px ushr 16 and 0xFF) - (bgColor ushr 16 and 0xFF)
        val dg = (px ushr 8 and 0xFF) - (bgColor ushr 8 and 0xFF)
        val db = (px and 0xFF) - (bgColor and 0xFF)
        return dr * dr + dg * dg + db * db <= 400
    }

    // 找左边界：逐列扫描，找到第一列不全是背景色的位置
    var left = 0
    while (left < w) {
        var colHasContent = false
        for (y in 0 until h) {
            if (!isBgColor(pixels[y * w + left])) {
                colHasContent = true
                break
            }
        }
        if (colHasContent) break
        left++
    }
    // 找右边界
    var right = w - 1
    while (right >= left) {
        var colHasContent = false
        for (y in 0 until h) {
            if (!isBgColor(pixels[y * w + right])) {
                colHasContent = true
                break
            }
        }
        if (colHasContent) break
        right--
    }
    // 找上边界
    var top = 0
    while (top < h) {
        var rowHasContent = false
        for (x in left..right) {
            if (!isBgColor(pixels[top * w + x])) {
                rowHasContent = true
                break
            }
        }
        if (rowHasContent) break
        top++
    }
    // 找下边界
    var bottom = h - 1
    while (bottom >= top) {
        var rowHasContent = false
        for (x in left..right) {
            if (!isBgColor(pixels[bottom * w + x])) {
                rowHasContent = true
                break
            }
        }
        if (rowHasContent) break
        bottom--
    }

    val cropW = right - left + 1
    val cropH = bottom - top + 1
    if (cropW <= 0 || cropH <= 0) return bitmap
    return Bitmap.createBitmap(bitmap, left, top, cropW, cropH)
}
