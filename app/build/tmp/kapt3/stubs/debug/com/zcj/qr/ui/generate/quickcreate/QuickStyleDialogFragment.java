package com.zcj.qr.ui.generate.quickcreate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u0000 ,2\u00020\u0001:\u0001,B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\rH\u0002J\b\u0010\u0016\u001a\u0004\u0018\u00010\u000bJ\u0006\u0010\u0017\u001a\u00020\rJ\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J$\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\b\u0010#\u001a\u00020\u0014H\u0016J\u0010\u0010$\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u001a\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020\u001c2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\b\u0010\'\u001a\u00020\u0014H\u0002J\b\u0010(\u001a\u00020\u0014H\u0002J\u0010\u0010)\u001a\u00020\u00142\u0006\u0010*\u001a\u00020+H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0010\u001a\u0010\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u000b0\u000b0\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/zcj/qr/ui/generate/quickcreate/QuickStyleDialogFragment;", "Landroidx/fragment/app/DialogFragment;", "()V", "_binding", "Lcom/zcj/qr/databinding/DialogQuickStyleBinding;", "binding", "getBinding", "()Lcom/zcj/qr/databinding/DialogQuickStyleBinding;", "currentLogoBitmap", "Landroid/graphics/Bitmap;", "lastLogoBytes", "", "lastSelectedStyle", "Lcom/zcj/qr/util/QrStyle;", "logoUri", "Landroid/net/Uri;", "pickLogo", "Landroidx/activity/result/ActivityResultLauncher;", "kotlin.jvm.PlatformType", "applyStyle", "", "style", "getLogoBytes", "getSelectedStyle", "onCancel", "dialog", "Landroid/content/DialogInterface;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onDismiss", "onViewCreated", "view", "saveLastSelection", "setupListeners", "showColorPicker", "isDot", "", "Companion", "app_debug"})
public final class QuickStyleDialogFragment extends androidx.fragment.app.DialogFragment {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG = "QuickStyleDialog";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_STYLE = "style";
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function2<? super com.zcj.qr.util.QrStyle, ? super java.lang.String, kotlin.Unit> onDismiss;
    @org.jetbrains.annotations.Nullable()
    private com.zcj.qr.databinding.DialogQuickStyleBinding _binding;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri logoUri;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentLogoBitmap;
    @org.jetbrains.annotations.NotNull()
    private com.zcj.qr.util.QrStyle lastSelectedStyle;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastLogoBytes;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> pickLogo = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.zcj.qr.ui.generate.quickcreate.QuickStyleDialogFragment.Companion Companion = null;
    
    public QuickStyleDialogFragment() {
        super();
    }
    
    private final com.zcj.qr.databinding.DialogQuickStyleBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void applyStyle(com.zcj.qr.util.QrStyle style) {
    }
    
    private final void setupListeners() {
    }
    
    private final void showColorPicker(boolean isDot) {
    }
    
    @java.lang.Override()
    public void onCancel(@org.jetbrains.annotations.NotNull()
    android.content.DialogInterface dialog) {
    }
    
    @java.lang.Override()
    public void onDismiss(@org.jetbrains.annotations.NotNull()
    android.content.DialogInterface dialog) {
    }
    
    private final void saveLastSelection() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.zcj.qr.util.QrStyle getSelectedStyle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLogoBytes() {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R0\u0010\u0006\u001a\u0018\u0012\u0004\u0012\u00020\b\u0012\u0006\u0012\u0004\u0018\u00010\u0004\u0012\u0004\u0012\u00020\t\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r\u00a8\u0006\u0011"}, d2 = {"Lcom/zcj/qr/ui/generate/quickcreate/QuickStyleDialogFragment$Companion;", "", "()V", "KEY_STYLE", "", "TAG", "onDismiss", "Lkotlin/Function2;", "Lcom/zcj/qr/util/QrStyle;", "", "getOnDismiss", "()Lkotlin/jvm/functions/Function2;", "setOnDismiss", "(Lkotlin/jvm/functions/Function2;)V", "newInstance", "Lcom/zcj/qr/ui/generate/quickcreate/QuickStyleDialogFragment;", "style", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final kotlin.jvm.functions.Function2<com.zcj.qr.util.QrStyle, java.lang.String, kotlin.Unit> getOnDismiss() {
            return null;
        }
        
        public final void setOnDismiss(@org.jetbrains.annotations.Nullable()
        kotlin.jvm.functions.Function2<? super com.zcj.qr.util.QrStyle, ? super java.lang.String, kotlin.Unit> p0) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.zcj.qr.ui.generate.quickcreate.QuickStyleDialogFragment newInstance(@org.jetbrains.annotations.NotNull()
        com.zcj.qr.util.QrStyle style) {
            return null;
        }
    }
}