package com.zcj.qr.ui.generate.quickcreate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\'B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001c\u001a\u00020\u0016H\u0002J\u0012\u0010\u001d\u001a\u00020\u00142\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020!H\u0016J\u0012\u0010\"\u001a\u0004\u0018\u00010\u00062\u0006\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020!H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006("}, d2 = {"Lcom/zcj/qr/ui/generate/quickcreate/QuickItemInputActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/zcj/qr/databinding/ActivityQuickItemInputBinding;", "logoFile", "Ljava/io/File;", "logoUri", "Landroid/net/Uri;", "pickImage", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "repository", "Lcom/zcj/qr/data/repository/HistoryRepository;", "getRepository", "()Lcom/zcj/qr/data/repository/HistoryRepository;", "repository$delegate", "Lkotlin/Lazy;", "applyStyleToUI", "", "style", "Lcom/zcj/qr/util/QrStyle;", "applyTypeFields", "type", "Lcom/zcj/qr/ui/generate/quickcreate/QuickItemInputActivity$QuickType;", "buildContent", "generateAndNavigate", "getCurrentStyle", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onSupportNavigateUp", "", "saveLogoToCache", "bitmap", "Landroid/graphics/Bitmap;", "showColorPicker", "isDot", "QuickType", "app_debug"})
public final class QuickItemInputActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.zcj.qr.databinding.ActivityQuickItemInputBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri logoUri;
    @org.jetbrains.annotations.Nullable()
    private java.io.File logoFile;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> pickImage = null;
    
    public QuickItemInputActivity() {
        super();
    }
    
    private final com.zcj.qr.data.repository.HistoryRepository getRepository() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void applyTypeFields(com.zcj.qr.ui.generate.quickcreate.QuickItemInputActivity.QuickType type) {
    }
    
    private final void applyStyleToUI(com.zcj.qr.util.QrStyle style) {
    }
    
    /**
     * 将 Logo Bitmap 保存为缓存文件（PNG压缩）
     */
    private final java.io.File saveLogoToCache(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    private final com.zcj.qr.util.QrStyle getCurrentStyle() {
        return null;
    }
    
    private final void showColorPicker(boolean isDot) {
    }
    
    private final void generateAndNavigate(com.zcj.qr.ui.generate.quickcreate.QuickItemInputActivity.QuickType type) {
    }
    
    private final java.lang.String buildContent(com.zcj.qr.ui.generate.quickcreate.QuickItemInputActivity.QuickType type) {
        return null;
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007j\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/zcj/qr/ui/generate/quickcreate/QuickItemInputActivity$QuickType;", "", "label", "", "extraKey", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "getExtraKey", "()Ljava/lang/String;", "getLabel", "WIFI", "CONTACT", "APP", "URL", "PHONE", "SMS", "EMAIL", "app_debug"})
    public static enum QuickType {
        /*public static final*/ WIFI /* = new WIFI(null, null) */,
        /*public static final*/ CONTACT /* = new CONTACT(null, null) */,
        /*public static final*/ APP /* = new APP(null, null) */,
        /*public static final*/ URL /* = new URL(null, null) */,
        /*public static final*/ PHONE /* = new PHONE(null, null) */,
        /*public static final*/ SMS /* = new SMS(null, null) */,
        /*public static final*/ EMAIL /* = new EMAIL(null, null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String extraKey = null;
        
        QuickType(java.lang.String label, java.lang.String extraKey) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getExtraKey() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.zcj.qr.ui.generate.quickcreate.QuickItemInputActivity.QuickType> getEntries() {
            return null;
        }
    }
}