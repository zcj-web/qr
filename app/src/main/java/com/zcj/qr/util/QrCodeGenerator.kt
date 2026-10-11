package com.zcj.qr.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
    val logoSizeRatio: Float = 0.2f,
    val marginPx: Int = 20
) : Parcelable

object QrCodeGenerator {
    fun generate(content: String, size: Int = 800, style: QrStyle = QrStyle()): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.MARGIN, style.marginPx)
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
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
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = style.dotColor
        }

        val moduleSize = size.toFloat() / bitMatrix.width
        val rect = RectF()

        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                if (!bitMatrix.get(x, y)) continue

                val left = x * moduleSize
                val top = y * moduleSize
                val right = (x + 1) * moduleSize
                val bottom = (y + 1) * moduleSize

                rect.set(left, top, right, bottom)

                when (style.dotShape) {
                    DotShape.SQUARE -> canvas.drawRect(rect, paint)
                    DotShape.ROUND -> canvas.drawRoundRect(rect, moduleSize / 2, moduleSize / 2, paint)
                    DotShape.DIAMOND -> {
                        val path = Path()
                        val cx = (left + right) / 2
                        val cy = (top + bottom) / 2
                        val half = moduleSize / 2
                        path.moveTo(cx, cy - half)
                        path.lineTo(cx + half, cy)
                        path.lineTo(cx, cy + half)
                        path.lineTo(cx - half, cy)
                        path.close()
                        canvas.drawPath(path, paint)
                    }
                    DotShape.CIRCLE -> canvas.drawCircle((left + right) / 2, (top + bottom) / 2, moduleSize / 2, paint)
                }
            }
        }

        // Draw logo
        style.logoBitmap?.let { logo ->
            val logoSize = (size * style.logoSizeRatio).toInt()
            val scaledLogo = logo.scale(logoSize, logoSize)
            val left = (size - logoSize) / 2
            val top = (size - logoSize) / 2
            // White border
            val borderPaint = Paint().apply { color = style.bgColor }
            canvas.drawBitmap(scaledLogo, left.toFloat() - 4, top.toFloat() - 4, borderPaint)
            canvas.drawBitmap(scaledLogo, left.toFloat(), top.toFloat(), null)
        }

        return result
    }
}

fun Bitmap.scale(width: Int, height: Int): Bitmap {
    return if (this.width == width && this.height == height) this
    else Bitmap.createScaledBitmap(this, width, height, true)
}
