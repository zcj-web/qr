package com.zcj.qr.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\u0006H\u0002J\u0012\u0010\t\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\u0006H\u0002J\u0010\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0018\u0010\u000b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\u0006J\u0016\u0010\u000e\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\u0006J\u000e\u0010\u000f\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0006H\u0002J\u000e\u0010\u0013\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\u0006J\u0010\u0010\u0014\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006H\u0002J\u0010\u0010\u0015\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006H\u0002J\u0010\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0002J\u000e\u0010\u0018\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006\u00a8\u0006\u0019"}, d2 = {"Lcom/zcj/qr/util/ProtocolHandler;", "", "()V", "buildContactIntent", "Landroid/content/Intent;", "vcard", "", "buildEmailIntent", "content", "buildSmsIntent", "extractContactName", "getActionIntent", "type", "Lcom/zcj/qr/util/ProtocolType;", "getContentPreview", "getDisplayName", "isDomain", "", "text", "parse", "parseEmailDisplay", "parseSmsDisplay", "parseWifiSsid", "wifiStr", "resolveUrl", "app_debug"})
public final class ProtocolHandler {
    @org.jetbrains.annotations.NotNull()
    public static final com.zcj.qr.util.ProtocolHandler INSTANCE = null;
    
    private ProtocolHandler() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.zcj.qr.util.ProtocolType parse(@org.jetbrains.annotations.NotNull()
    java.lang.String content) {
        return null;
    }
    
    /**
     * 判断是否为裸域名（如 baidu.com / www.baidu.com）
     */
    private final boolean isDomain(java.lang.String text) {
        return false;
    }
    
    /**
     * 为裸域名自动补全 https://
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String resolveUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String content) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName(@org.jetbrains.annotations.NotNull()
    com.zcj.qr.util.ProtocolType type) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getContentPreview(@org.jetbrains.annotations.NotNull()
    com.zcj.qr.util.ProtocolType type, @org.jetbrains.annotations.NotNull()
    java.lang.String content) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.content.Intent getActionIntent(@org.jetbrains.annotations.NotNull()
    com.zcj.qr.util.ProtocolType type, @org.jetbrains.annotations.NotNull()
    java.lang.String content) {
        return null;
    }
    
    private final java.lang.String parseWifiSsid(java.lang.String wifiStr) {
        return null;
    }
    
    private final java.lang.String extractContactName(java.lang.String vcard) {
        return null;
    }
    
    private final java.lang.String parseSmsDisplay(java.lang.String content) {
        return null;
    }
    
    private final java.lang.String parseEmailDisplay(java.lang.String content) {
        return null;
    }
    
    private final android.content.Intent buildContactIntent(java.lang.String vcard) {
        return null;
    }
    
    private final android.content.Intent buildSmsIntent(java.lang.String content) {
        return null;
    }
    
    private final android.content.Intent buildEmailIntent(java.lang.String content) {
        return null;
    }
}