package com.zcj.qr.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003\u001a\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006\u001a\u0010\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u0001H\u0002\u001a\u001a\u0010\n\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u0001\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"MIN_LUMINANCE_DIFF", "", "cropLogoContent", "Landroid/graphics/Bitmap;", "bitmap", "ensureScannableStyle", "Lcom/zcj/qr/util/QrStyle;", "style", "luminance", "color", "scale", "width", "height", "app_debug"})
public final class QrCodeGeneratorKt {
    
    /**
     * 计算两色之间的亮度差（0-255），低于此值则难以被扫码器识别
     */
    private static final int MIN_LUMINANCE_DIFF = 30;
    
    /**
     * 提取颜色的亮度分量（ITU-R BT.601 标准）
     */
    private static final int luminance(int color) {
        return 0;
    }
    
    /**
     * 校验颜色对比度，若低于阈值则自动修正为黑白高对比版本。
     * 确保生成的彩色二维码仍可被标准扫码器（微信/系统相机等）识别。
     */
    @org.jetbrains.annotations.NotNull()
    public static final com.zcj.qr.util.QrStyle ensureScannableStyle(@org.jetbrains.annotations.NotNull()
    com.zcj.qr.util.QrStyle style) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final android.graphics.Bitmap scale(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap $this$scale, int width, int height) {
        return null;
    }
    
    /**
     * 裁剪 Logo 图片的有效内容区域（去除纯色背景边距）
     * 原理：从四边向内扫描，跳过与左上角颜色相近的像素，直到遇到内容为止
     * 效果：Logo 透明区域透出下方 QR 模块，保证可扫描性
     */
    @org.jetbrains.annotations.NotNull()
    public static final android.graphics.Bitmap cropLogoContent(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
}