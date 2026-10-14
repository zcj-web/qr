package com.zcj.qr.ui.generate.custom;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\u0006H\u0002J\b\u0010\u0018\u001a\u00020\u0012H\u0002J\b\u0010\u0019\u001a\u00020\u0014H\u0002J\u0012\u0010\u001a\u001a\u00020\u00122\b\u0010\u0017\u001a\u0004\u0018\u00010\u0006H\u0002J\n\u0010\u001b\u001a\u0004\u0018\u00010\u0016H\u0002J\b\u0010\u001c\u001a\u00020\u0012H\u0016J\u0012\u0010\u001d\u001a\u00020\u00122\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u0012H\u0002J\b\u0010!\u001a\u00020\u0012H\u0002J\b\u0010\"\u001a\u00020\u0012H\u0002J\u0010\u0010#\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0010\u0010$\u001a\u00020\u00122\u0006\u0010%\u001a\u00020\u0016H\u0002J\u0010\u0010&\u001a\u00020\u00122\u0006\u0010\'\u001a\u00020(H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0010\u0012\f\u0012\n \n*\u0004\u0018\u00010\t0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000e\u00a8\u0006)"}, d2 = {"Lcom/zcj/qr/ui/generate/custom/CustomGenerateActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/zcj/qr/databinding/FragmentCustomGenerateBinding;", "logoUri", "Landroid/net/Uri;", "pickImage", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "repository", "Lcom/zcj/qr/data/repository/HistoryRepository;", "getRepository", "()Lcom/zcj/qr/data/repository/HistoryRepository;", "repository$delegate", "Lkotlin/Lazy;", "applyStyleToUI", "", "style", "Lcom/zcj/qr/util/QrStyle;", "decodeLogoBitmap", "Landroid/graphics/Bitmap;", "uri", "generateAndNavigate", "getCurrentStyle", "grantUriPermissionsToUri", "loadLogoBitmapFromPrefs", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "resetLogoToDefault", "restoreLogoFromPrefs", "revokeUriPermissions", "saveLastStyle", "saveLogoBitmapToPrefs", "bitmap", "showColorPicker", "isDotColor", "", "app_debug"})
public final class CustomGenerateActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.zcj.qr.databinding.FragmentCustomGenerateBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri logoUri;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> pickImage = null;
    
    public CustomGenerateActivity() {
        super();
    }
    
    private final com.zcj.qr.data.repository.HistoryRepository getRepository() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void applyStyleToUI(com.zcj.qr.util.QrStyle style) {
    }
    
    private final void restoreLogoFromPrefs() {
    }
    
    /**
     * 重置 Logo 为未选择状态
     */
    private final void resetLogoToDefault() {
    }
    
    private final com.zcj.qr.util.QrStyle getCurrentStyle() {
        return null;
    }
    
    private final void saveLastStyle(com.zcj.qr.util.QrStyle style) {
    }
    
    private final void showColorPicker(boolean isDotColor) {
    }
    
    private final android.graphics.Bitmap decodeLogoBitmap(android.net.Uri uri) {
        return null;
    }
    
    /**
     * 为 MediaStore URI 授予读取权限，供后续 Activity 使用
     */
    private final void grantUriPermissionsToUri(android.net.Uri uri) {
    }
    
    private final void revokeUriPermissions() {
    }
    
    /**
     * 将 Bitmap 编码为 PNG 文件并保存路径到 SharedPreferences
     */
    private final void saveLogoBitmapToPrefs(android.graphics.Bitmap bitmap) {
    }
    
    /**
     * 从 SharedPreferences 读取 Logo Bitmap 字节并解码
     */
    private final android.graphics.Bitmap loadLogoBitmapFromPrefs() {
        return null;
    }
    
    private final void generateAndNavigate() {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
}