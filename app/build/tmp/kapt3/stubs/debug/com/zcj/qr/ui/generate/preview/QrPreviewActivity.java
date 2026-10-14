package com.zcj.qr.ui.generate.preview;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J \u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rH\u0002J\n\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0010\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0012\u0010\u0013\u001a\u00020\b2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\u0010\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u000fH\u0002J\b\u0010\u0018\u001a\u00020\bH\u0002J\u0010\u0010\u0019\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\u001a\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u000fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/zcj/qr/ui/generate/preview/QrPreviewActivity;", "Lcom/zcj/qr/BaseActivity;", "()V", "binding", "Lcom/zcj/qr/databinding/ActivityQrPreviewBinding;", "currentSafeStyle", "Lcom/zcj/qr/util/QrStyle;", "generateAndShow", "", "content", "", "style", "contrastOk", "", "loadLogoBitmap", "Landroid/graphics/Bitmap;", "loadStyle", "intent", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "saveToGallery", "bitmap", "setupBack", "setupButtons", "shareBitmap", "app_debug"})
public final class QrPreviewActivity extends com.zcj.qr.BaseActivity {
    private com.zcj.qr.databinding.ActivityQrPreviewBinding binding;
    @org.jetbrains.annotations.Nullable()
    private com.zcj.qr.util.QrStyle currentSafeStyle;
    
    public QrPreviewActivity() {
        super();
    }
    
    private final com.zcj.qr.util.QrStyle loadStyle(android.content.Intent intent) {
        return null;
    }
    
    private final android.graphics.Bitmap loadLogoBitmap() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void generateAndShow(java.lang.String content, com.zcj.qr.util.QrStyle style, boolean contrastOk) {
    }
    
    private final void setupButtons(java.lang.String content) {
    }
    
    private final void saveToGallery(android.graphics.Bitmap bitmap) {
    }
    
    private final void shareBitmap(android.graphics.Bitmap bitmap) {
    }
    
    private final void setupBack() {
    }
}