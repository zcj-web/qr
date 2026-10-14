package com.zcj.qr.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\u000b\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002J\u001a\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\nH\u0002J\u001a\u0010\u000e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0002J\u0010\u0010\u0011\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002J\u000e\u0010\u0012\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007J\u001a\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\u0014\u001a\u00020\nH\u0002J\u0018\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0010H\u0002J\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\b\u001a\u00020\u0007J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u00192\u0006\u0010\b\u001a\u00020\u0007J\u0010\u0010\u001b\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002J\u000e\u0010\u001c\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007J\u0010\u0010\u001d\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002J\u0010\u0010\u001e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/zcj/qr/util/QrScanner;", "", "()V", "hints", "Ljava/util/EnumMap;", "Lcom/google/zxing/DecodeHintType;", "adaptiveThreshold", "Landroid/graphics/Bitmap;", "bitmap", "blockSize", "", "contrastPreserveSaturation", "dilate", "iterations", "gammaCorrect", "gamma", "", "hsvSaturationThreshold", "invertBitmap", "morphClose", "kernelSize", "pow", "base", "exp", "scanOnce", "", "scanWithMultipleAttempts", "strongContrast", "toGrayBitmap", "toPureBlackWhite", "unsharpMask", "app_debug"})
public final class QrScanner {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.EnumMap<com.google.zxing.DecodeHintType, java.lang.Object> hints = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.zcj.qr.util.QrScanner INSTANCE = null;
    
    private QrScanner() {
        super();
    }
    
    /**
     * 带对比度增强的纯黑白二值图（最高辨识度）
     */
    private final android.graphics.Bitmap toPureBlackWhite(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * Gamma 校正增强对比度
     */
    private final android.graphics.Bitmap gammaCorrect(android.graphics.Bitmap bitmap, float gamma) {
        return null;
    }
    
    private final float pow(float base, float exp) {
        return 0.0F;
    }
    
    /**
     * 自适应局部阈值（将图像分为 4x4 区块，每区块独立二值化）
     */
    private final android.graphics.Bitmap adaptiveThreshold(android.graphics.Bitmap bitmap, int blockSize) {
        return null;
    }
    
    /**
     * Unsharp mask sharpening - enhances edges for rounded/circular QR dots
     */
    private final android.graphics.Bitmap unsharpMask(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * Morphological dilation - makes thin/rounded dots thicker and more square-like
     */
    private final android.graphics.Bitmap dilate(android.graphics.Bitmap bitmap, int iterations) {
        return null;
    }
    
    /**
     * Strong contrast stretch - maximizes difference between dark and light
     */
    private final android.graphics.Bitmap strongContrast(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * HSV 饱和度阈值二值化：对彩色二维码（低明暗对比、高饱和度差异）特别有效
     */
    private final android.graphics.Bitmap hsvSaturationThreshold(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * 饱和像素对比拉伸：高饱和区域做对比度增强，去色区域保持原样
     */
    private final android.graphics.Bitmap contrastPreserveSaturation(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * 形态学闭运算：先膨胀后腐蚀，修复压缩/Logo 造成的模块断裂
     */
    private final android.graphics.Bitmap morphClose(android.graphics.Bitmap bitmap, int kernelSize) {
        return null;
    }
    
    /**
     * 单次扫描，支持多种二值化器
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String scanOnce(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap toGrayBitmap(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap invertBitmap(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * 多策略扫描，提高识别成功率
     * 策略覆盖：原始 + Hybrid/全局直方图/纯黑白/自适应阈值
     *             + 缩小/放大 + 灰度 + 反色 + gamma校正 + 组合变换
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String scanWithMultipleAttempts(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
}