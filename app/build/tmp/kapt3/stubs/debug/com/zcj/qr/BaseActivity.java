package com.zcj.qr;

/**
 * 所有 Activity 继承此类，自动应用存储的主题（日间/夜间）
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0002J\u0006\u0010\u0005\u001a\u00020\u0006J\u0012\u0010\u0007\u001a\u00020\u00042\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\u000e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0006\u00a8\u0006\f"}, d2 = {"Lcom/zcj/qr/BaseActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "applyStoredTheme", "", "getStoredThemeMode", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "saveThemeMode", "mode", "app_debug"})
public class BaseActivity extends androidx.appcompat.app.AppCompatActivity {
    
    public BaseActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * 根据 SharedPreferences 中的存储模式设置主题，必须在 super.onCreate() 之前调用
     */
    private final void applyStoredTheme() {
    }
    
    /**
     * 切换主题后调用，存入 SharedPreferences
     */
    public final void saveThemeMode(int mode) {
    }
    
    /**
     * 读取当前存储的主题模式
     */
    public final int getStoredThemeMode() {
        return 0;
    }
}