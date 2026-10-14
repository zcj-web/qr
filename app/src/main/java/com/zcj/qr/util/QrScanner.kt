package com.zcj.qr.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.RGBLuminanceSource
import java.util.EnumMap

object QrScanner {

    private val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
        put(DecodeHintType.POSSIBLE_FORMATS, listOf(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.UPC_A,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.ITF
        ))
    }

    /** 带对比度增强的纯黑白二值图（最高辨识度） */
    private fun toPureBlackWhite(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        // 先灰度
        val gray = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        Canvas(gray).let { canvas ->
            Paint().apply {
                colorFilter = android.graphics.ColorMatrixColorFilter(floatArrayOf(
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }.also { canvas.drawBitmap(bitmap, 0f, 0f, it) }
        }
        // 转 ARGB8888 后做 Otsu 式二值化
        val argb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(w * h)
        gray.getPixels(srcPixels, 0, w, 0, 0, w, h)
        val dst = IntArray(w * h)
        // 计算全局阈值（取灰度直方图中谷值）
        val hist = IntArray(256)
        for (p in srcPixels) hist[p ushr 16 and 0xFF]++
        var total = 0
        var sum = 0
        for (i in 0..255) { sum += i * hist[i]; total += hist[i] }
        var bestTh = 128
        var bestVar = 0
        var w0 = 0; var s0 = 0
        for (th in 1..254) {
            w0 += hist[th - 1]; s0 += (th - 1) * hist[th - 1]
            val w1 = total - w0
            if (w1 <= 0) break
            val mean0 = s0.toFloat() / w0
            val mean1 = (sum - s0).toFloat() / w1
            val varBetween = w0 * w1 * (mean0 - mean1) * (mean0 - mean1)
            if (varBetween > bestVar) { bestVar = varBetween.toInt(); bestTh = th }
        }
        val threshold = bestTh.coerceIn(30, 220)
        for (i in dst.indices) {
            val g = srcPixels[i] ushr 16 and 0xFF
            val v = if (g < threshold) 0 else 255
            dst[i] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
        }
        argb.setPixels(dst, 0, w, 0, 0, w, h)
        return argb
    }

    /** Gamma 校正增强对比度 */
    private fun gammaCorrect(bitmap: Bitmap, gamma: Float = 0.8f): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        val invGamma = 1f / gamma
        for (i in pixels.indices) {
            val r = (pixels[i] ushr 16 and 0xFF).toFloat() / 255f
            val g = (pixels[i] ushr 8 and 0xFF).toFloat() / 255f
            val b = (pixels[i] and 0xFF).toFloat() / 255f
            val nr = (pow(r, invGamma) * 255f).toInt().coerceIn(0, 255)
            val ng = (pow(g, invGamma) * 255f).toInt().coerceIn(0, 255)
            val nb = (pow(b, invGamma) * 255f).toInt().coerceIn(0, 255)
            pixels[i] = ((0xFF000000L or (nr.toLong() shl 16) or (ng.toLong() shl 8) or nb.toLong())).toInt()
        }
        result.setPixels(pixels, 0, w, 0, 0, w, h)
        return result
    }

    private fun pow(base: Float, exp: Float): Float {
        return if (base <= 0f) 0f else Math.pow(base.toDouble(), exp.toDouble()).toFloat()
    }

    /** 自适应局部阈值（将图像分为 4x4 区块，每区块独立二值化） */
    private fun adaptiveThreshold(bitmap: Bitmap, blockSize: Int = 16): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        // 先灰度
        val gray = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        Canvas(gray).let { canvas ->
            Paint().apply {
                colorFilter = android.graphics.ColorMatrixColorFilter(floatArrayOf(
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }.also { canvas.drawBitmap(bitmap, 0f, 0f, it) }
        }
        val src = IntArray(w * h)
        gray.getPixels(src, 0, w, 0, 0, w, h)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        val bh = (h + blockSize - 1) / blockSize
        val bw = (w + blockSize - 1) / blockSize
        for (by in 0 until bh) {
            for (bx in 0 until bw) {
                var sum = 0; var count = 0
                val y0 = by * blockSize; val y1 = minOf(y0 + blockSize, h)
                val x0 = bx * blockSize; val x1 = minOf(x0 + blockSize, w)
                for (y in y0 until y1) {
                    for (x in x0 until x1) {
                        sum += src[y * w + x] ushr 16 and 0xFF
                        count++
                    }
                }
                val th = (sum / count).coerceIn(40, 200)
                for (y in y0 until y1) {
                    for (x in x0 until x1) {
                        val g = src[y * w + x] ushr 16 and 0xFF
                        val v = if (g < th) 0 else 255
                        dst[y * w + x] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
                    }
                }
            }
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** Unsharp mask sharpening - enhances edges for rounded/circular QR dots */
    private fun unsharpMask(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val gray = toGrayBitmap(bitmap)
        val src = IntArray(w * h)
        gray.getPixels(src, 0, w, 0, 0, w, h)
        // 3x3 Gaussian blur
        val blurred = IntArray(w * h)
        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                var sum = 0
                for (dy in -1..1) for (dx in -1..1) {
                    val p = src[(y + dy) * w + (x + dx)] ushr 16 and 0xFF
                    sum += when { dy == 0 && dx == 0 -> 16; (dy == 0 || dx == 0) && Math.abs(dy) + Math.abs(dx) == 1 -> 4; else -> 1 }
                }
                blurred[y * w + x] = (sum / 25).coerceIn(0, 255)
            }
        }
        // Edge = original - blurred (detail layer)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        for (i in src.indices) {
            val orig = src[i] ushr 16 and 0xFF
            val blur = blurred[i]
            val edge = (orig - blur).coerceIn(-128, 128)
            // Sharpened = original + strength * edge
            val sharp = (orig + edge * 2).coerceIn(0, 255)
            dst[i] = ((0xFF000000L or (sharp.toLong() shl 16) or (sharp.toLong() shl 8) or sharp.toLong())).toInt()
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** Morphological dilation - makes thin/rounded dots thicker and more square-like */
    private fun dilate(bitmap: Bitmap, iterations: Int = 1): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        // First convert to pure black-white for morphological ops
        val pureBW = toPureBlackWhite(bitmap)
        val src = IntArray(w * h)
        pureBW.getPixels(src, 0, w, 0, 0, w, h)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // Dilate: a pixel is white if any neighbor in 3x3 is white
        for (iter in 0 until iterations) {
            val dst = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    var isWhite = (src[y * w + x] ushr 16 and 0xFF) > 128
                    if (!isWhite) {
                        for (dy in -1..1) for (dx in -1..1) {
                            val ny = y + dy; val nx = x + dx
                            if (ny in 0 until h && nx in 0 until w) {
                                if ((src[ny * w + nx] ushr 16 and 0xFF) > 128) { isWhite = true; break }
                            }
                        }
                    }
                    val v = if (isWhite) 255 else 0
                    dst[y * w + x] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
                }
            }
            src.copyInto(dst)
            src.forEachIndexed { i, _ -> src[i] = dst[i] }
            if (iter == 0) {
                // Copy to result only on first iteration
                result.setPixels(dst, 0, w, 0, 0, w, h)
            }
        }
        // Actually let me just do it properly
        val pixels2 = IntArray(w * h)
        pureBW.getPixels(pixels2, 0, w, 0, 0, w, h)
        val out = IntArray(w * h)
        for (iter in 0 until iterations) {
            pixels2.copyInto(out)
            for (y in 1 until h - 1) {
                for (x in 1 until w - 1) {
                    val idx = y * w + x
                    var isWhite = (pixels2[idx] ushr 16 and 0xFF) > 128
                    if (!isWhite) {
                        for (dy in -1..1) for (dx in -1..1) {
                            if ((pixels2[(y + dy) * w + (x + dx)] ushr 16 and 0xFF) > 128) {
                                isWhite = true; break
                            }
                        }
                    }
                    val v = if (isWhite) 255 else 0
                    out[idx] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
                }
            }
            out.copyInto(pixels2)
        }
        result.setPixels(pixels2, 0, w, 0, 0, w, h)
        return result
    }

    /** Strong contrast stretch - maximizes difference between dark and light */
    private fun strongContrast(bitmap: Bitmap): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val gray = toGrayBitmap(bitmap)
        val src = IntArray(w * h)
        gray.getPixels(src, 0, w, 0, 0, w, h)
        // Find min/max in the QR region (skip border)
        var minV = 255; var maxV = 0
        val margin = w / 10
        for (y in margin until h - margin step 2) {
            for (x in margin until w - margin step 2) {
                val v = src[y * w + x] ushr 16 and 0xFF
                if (v < minV) minV = v
                if (v > maxV) maxV = v
            }
        }
        if (maxV - minV < 20) return bitmap // Already high contrast
        val range = maxV - minV
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        for (i in src.indices) {
            val v = src[i] ushr 16 and 0xFF
            val stretched = ((v - minV) * 255 / range).coerceIn(0, 255)
            dst[i] = ((0xFF000000L or (stretched.toLong() shl 16) or (stretched.toLong() shl 8) or stretched.toLong())).toInt()
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** HSV 饱和度阈值二值化：对彩色二维码（低明暗对比、高饱和度差异）特别有效 */
    private fun hsvSaturationThreshold(bitmap: Bitmap): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val src = IntArray(w * h)
        bitmap.getPixels(src, 0, w, 0, 0, w, h)
        // 计算全局饱和度直方图，用 Otsu 找最佳分割点
        val satCounts = IntArray(256)
        var satSum = 0
        for (i in src.indices) {
            val r = src[i] ushr 16 and 0xFF
            val g = src[i] ushr 8 and 0xFF
            val b = src[i] and 0xFF
            val maxC = maxOf(r, g, b); val minC = minOf(r, g, b)
            val sat = if (maxC == 0) 0 else ((maxC - minC) * 255 / maxC).toInt()
            satCounts[sat]++
            satSum += sat
        }
        var bestTh = 128; var bestVar = 0
        var w0 = 0; var s0 = 0
        for (th in 30..225) {
            w0 += satCounts[th - 1]; s0 += (th - 1) * satCounts[th - 1]
            val w1 = w * h - w0
            if (w1 <= 0) break
            val mean0 = s0.toFloat() / w0
            val mean1 = (satSum - s0).toFloat() / w1
            val varBetween = w0 * w1 * (mean0 - mean1) * (mean0 - mean1)
            if (varBetween > bestVar) { bestVar = varBetween.toInt(); bestTh = th }
        }
        val threshold = bestTh.coerceIn(30, 200)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        for (i in src.indices) {
            val r = src[i] ushr 16 and 0xFF
            val g = src[i] ushr 8 and 0xFF
            val b = src[i] and 0xFF
            val maxC = maxOf(r, g, b); val minC = minOf(r, g, b)
            val sat = if (maxC == 0) 0 else ((maxC - minC) * 255 / maxC).toInt()
            val v = if (sat >= threshold) 0 else 255
            dst[i] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** 饱和像素对比拉伸：高饱和区域做对比度增强，去色区域保持原样 */
    private fun contrastPreserveSaturation(bitmap: Bitmap): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val src = IntArray(w * h)
        bitmap.getPixels(src, 0, w, 0, 0, w, h)
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        for (i in src.indices) {
            val r = src[i] ushr 16 and 0xFF
            val g = src[i] ushr 8 and 0xFF
            val b = src[i] and 0xFF
            val maxC = maxOf(r, g, b); val minC = minOf(r, g, b)
            val sat = if (maxC == 0) 0f else (maxC - minC) / 255f
            val lum = (r * 0.299f + g * 0.587f + b * 0.114f).toInt()
            val v = if (sat > 0.15f) {
                // 高饱和像素：拉伸对比度
                val stretch = ((lum - 64) * 1.8f + 128.0f).coerceIn(0.0f, 255.0f)
                if (stretch < 128.0f) 0 else 255
            } else {
                // 低饱和像素：保持原样二值化
                if (lum < 128) 0 else 255
            }
            dst[i] = ((0xFF000000L or (v.toLong() shl 16) or (v.toLong() shl 8) or v.toLong())).toInt()
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** 形态学闭运算：先膨胀后腐蚀，修复压缩/Logo 造成的模块断裂 */
    private fun morphClose(bitmap: Bitmap, kernelSize: Int = 3): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val src = IntArray(w * h)
        bitmap.getPixels(src, 0, w, 0, 0, w, h)
        // 二值化：取亮度中值作为阈值
        var sum = 0; var count = 0
        for (i in src.indices) {
            val v = src[i] ushr 16 and 0xFF
            sum += v; count++
        }
        val th = (sum / count).coerceIn(60, 200)
        val bw = IntArray(w * h)
        for (i in src.indices) {
            val v = src[i] ushr 16 and 0xFF
            bw[i] = if (v < th) 0 else 255
        }
        // 膨胀：黑像素扩张（取 3x3 邻域最小值）
        val dilated = IntArray(w * h)
        for (y in 0 until h) for (x in 0 until w) {
            var minV = 255
            for (dy in -kernelSize..kernelSize) for (dx in -kernelSize..kernelSize) {
                val ny = y + dy; val nx = x + dx
                if (ny in 0 until h && nx in 0 until w) {
                    val v = bw[ny * w + nx]
                    if (v < minV) minV = v
                }
            }
            dilated[y * w + x] = minV
        }
        // 腐蚀：黑像素收缩（取 3x3 邻域最大值）
        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dst = IntArray(w * h)
        for (y in 0 until h) for (x in 0 until w) {
            var maxV = 0
            for (dy in -kernelSize..kernelSize) for (dx in -kernelSize..kernelSize) {
                val ny = y + dy; val nx = x + dx
                if (ny in 0 until h && nx in 0 until w) {
                    val v = dilated[ny * w + nx]
                    if (v > maxV) maxV = v
                }
            }
            dst[y * w + x] = maxV
        }
        result.setPixels(dst, 0, w, 0, 0, w, h)
        return result
    }

    /** 单次扫描，支持多种二值化器 */
    /** 单次扫描，支持多种二值化器 */
    fun scanOnce(bitmap: Bitmap): String? {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
        val reader = MultiFormatReader().apply { setHints(hints) }
        // 策略1：HybridBinarizer（默认）
        runCatching { return reader.decode(BinaryBitmap(HybridBinarizer(source)))?.text }
        // 策略2：GlobalHistogramBinarizer（全局直方图，适合低对比度）
        runCatching { return reader.decode(BinaryBitmap(GlobalHistogramBinarizer(source)))?.text }
        // 策略3：纯黑白（Otsu 自动阈值）
        runCatching {
            val pureBW = toPureBlackWhite(bitmap)
            val p = IntArray(pureBW.width * pureBW.height)
            pureBW.getPixels(p, 0, pureBW.width, 0, 0, pureBW.width, pureBW.height)
            return reader.decode(BinaryBitmap(HybridBinarizer(
                RGBLuminanceSource(pureBW.width, pureBW.height, p)
            )))?.text
        }
        // 策略4：自适应局部阈值
        runCatching {
            val adapt = adaptiveThreshold(bitmap)
            val p = IntArray(adapt.width * adapt.height)
            adapt.getPixels(p, 0, adapt.width, 0, 0, adapt.width, adapt.height)
            return reader.decode(BinaryBitmap(HybridBinarizer(
                RGBLuminanceSource(adapt.width, adapt.height, p)
            )))?.text
        }
        return null
    }

    fun toGrayBitmap(bitmap: Bitmap): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val gray = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        Canvas(gray).let { canvas ->
            Paint().apply {
                colorFilter = android.graphics.ColorMatrixColorFilter(floatArrayOf(
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0.299f, 0.587f, 0.114f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }.also { canvas.drawBitmap(bitmap, 0f, 0f, it) }
        }
        return gray
    }

    fun invertBitmap(bitmap: Bitmap): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        val inv = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        val out = pixels.map { 0xFFFFFFFF.toInt() - it }.toIntArray()
        inv.setPixels(out, 0, w, 0, 0, w, h)
        return inv
    }

    /**
     * 多策略扫描，提高识别成功率
     * 策略覆盖：原始 + Hybrid/全局直方图/纯黑白/自适应阈值
     *              + 缩小/放大 + 灰度 + 反色 + gamma校正 + 组合变换
     */
    fun scanWithMultipleAttempts(bitmap: Bitmap): String? {
        if (bitmap.width == 0 || bitmap.height == 0) return null

        // --- 原始图像，多种二值化器 ---
        runCatching { return scanOnce(bitmap) }

        // --- 尺寸变换 ---
        runCatching {
            val small = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
            return scanOnce(small)
        }
        runCatching {
            // 放大 1.5x（某些美化二维码在低分辨率下反而更好认）
            val big = Bitmap.createScaledBitmap(bitmap, bitmap.width * 3 / 2, bitmap.height * 3 / 2, true)
            return scanOnce(big)
        }

        // --- 灰度化后扫描 ---
        runCatching { return scanOnce(toGrayBitmap(bitmap)) }
        // --- 反色后扫描 ---
        runCatching { return scanOnce(invertBitmap(bitmap)) }
        // --- 灰度+反色 ---
        runCatching { return scanOnce(invertBitmap(toGrayBitmap(bitmap))) }

        // --- gamma 校正增强对比度 ---
        runCatching { return scanOnce(gammaCorrect(bitmap, 0.7f)) }
        runCatching { return scanOnce(gammaCorrect(bitmap, 1.3f)) }
        runCatching { return scanOnce(gammaCorrect(toGrayBitmap(bitmap), 0.7f)) }
        runCatching { return scanOnce(gammaCorrect(invertBitmap(bitmap), 0.7f)) }

        // --- 自适应阈值 + 组合 ---
        runCatching { return scanOnce(adaptiveThreshold(bitmap)) }
        runCatching { return scanOnce(adaptiveThreshold(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(adaptiveThreshold(invertBitmap(bitmap))) }
        runCatching { return scanOnce(adaptiveThreshold(toGrayBitmap(invertBitmap(bitmap)))) }

        // --- 纯黑白 + 组合 ---
        runCatching { return scanOnce(toPureBlackWhite(bitmap)) }
        runCatching { return scanOnce(toPureBlackWhite(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(invertBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(gammaCorrect(bitmap, 0.7f))) }
        runCatching { return scanOnce(toPureBlackWhite(strongContrast(bitmap))) }

        // --- Unsharp mask + 组合（增强圆角边缘） ---
        runCatching { return scanOnce(unsharpMask(bitmap)) }
        runCatching { return scanOnce(unsharpMask(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(unsharpMask(invertBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(unsharpMask(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(unsharpMask(toGrayBitmap(bitmap)))) }

        // --- Morphological dilation + 组合（让圆形模块变方） ---
        runCatching { return scanOnce(dilate(bitmap, 1)) }
        runCatching { return scanOnce(dilate(toGrayBitmap(bitmap), 1)) }
        runCatching { return scanOnce(dilate(invertBitmap(bitmap), 1)) }
        runCatching { return scanOnce(toPureBlackWhite(dilate(bitmap, 1))) }
        runCatching { return scanOnce(toPureBlackWhite(dilate(toGrayBitmap(bitmap), 1))) }

        // --- Strong contrast + 组合 ---
        runCatching { return scanOnce(strongContrast(bitmap)) }
        runCatching { return scanOnce(strongContrast(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(strongContrast(invertBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(strongContrast(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(strongContrast(toGrayBitmap(bitmap)))) }

        // --- Adaptive threshold with different block sizes ---
        runCatching { return scanOnce(adaptiveThreshold(bitmap, 8)) }
        runCatching { return scanOnce(adaptiveThreshold(bitmap, 32)) }
        runCatching { return scanOnce(adaptiveThreshold(toGrayBitmap(bitmap), 8)) }
        runCatching { return scanOnce(adaptiveThreshold(toGrayBitmap(bitmap), 32)) }

        // --- HSV 饱和度二值化（针对彩色二维码，如紫色模块+灰色背景） ---
        runCatching { return scanOnce(hsvSaturationThreshold(bitmap)) }
        runCatching { return scanOnce(hsvSaturationThreshold(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(hsvSaturationThreshold(invertBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(hsvSaturationThreshold(bitmap))) }

        // --- 饱和对比拉伸 ---
        runCatching { return scanOnce(contrastPreserveSaturation(bitmap)) }
        runCatching { return scanOnce(contrastPreserveSaturation(toGrayBitmap(bitmap))) }
        runCatching { return scanOnce(toPureBlackWhite(contrastPreserveSaturation(bitmap))) }

        // --- HSV + 形态学闭运算（修复压缩/Logo 导致的模块断裂） ---
        runCatching { return scanOnce(morphClose(hsvSaturationThreshold(bitmap), 1)) }
        runCatching { return scanOnce(morphClose(hsvSaturationThreshold(bitmap), 2)) }
        runCatching { return scanOnce(morphClose(hsvSaturationThreshold(toGrayBitmap(bitmap)), 1)) }
        runCatching { return scanOnce(toPureBlackWhite(morphClose(hsvSaturationThreshold(bitmap), 1))) }

        // --- HSV + 对比拉伸组合（针对严重压缩的彩色二维码） ---
        runCatching {
            val hsv = hsvSaturationThreshold(bitmap)
            return scanOnce(strongContrast(hsv))
        }
        runCatching {
            val hsv = hsvSaturationThreshold(bitmap)
            return scanOnce(toPureBlackWhite(strongContrast(hsv)))
        }

        // --- Combined: unsharp + adaptive ---
        runCatching { return scanOnce(adaptiveThreshold(unsharpMask(bitmap), 16)) }
        runCatching { return scanOnce(adaptiveThreshold(unsharpMask(toGrayBitmap(bitmap)), 16)) }

        return null
    }
}
