package com.zcj.qr.crypto;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004J \u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0007H\u0002J0\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u0016H\u0002J \u0010\u001c\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\b\b\u0002\u0010\u0017\u001a\u00020\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/zcj/qr/crypto/Aes256GcmCrypto;", "", "()V", "ALGORITHM", "", "IterationOptions", "", "", "getIterationOptions", "()Ljava/util/List;", "KEY_LENGTH_BITS", "NONCE_LENGTH", "PBKDF2_ITERATIONS_DEFAULT", "SALT_LENGTH", "TAG_LENGTH_BITS", "VERSION", "decrypt", "base64Ciphertext", "password", "deriveKey", "Ljavax/crypto/spec/SecretKeySpec;", "salt", "", "iterations", "encodeOutput", "version", "nonce", "cipherText", "encrypt", "plaintext", "app_debug"})
public final class Aes256GcmCrypto {
    private static final int VERSION = 1;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH_BITS = 256;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int SALT_LENGTH = 16;
    private static final int NONCE_LENGTH = 12;
    private static final int PBKDF2_ITERATIONS_DEFAULT = 100000;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.Integer> IterationOptions = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.zcj.qr.crypto.Aes256GcmCrypto INSTANCE = null;
    
    private Aes256GcmCrypto() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getIterationOptions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String encrypt(@org.jetbrains.annotations.NotNull()
    java.lang.String plaintext, @org.jetbrains.annotations.NotNull()
    java.lang.String password, int iterations) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String decrypt(@org.jetbrains.annotations.NotNull()
    java.lang.String base64Ciphertext, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    private final javax.crypto.spec.SecretKeySpec deriveKey(java.lang.String password, byte[] salt, int iterations) {
        return null;
    }
    
    private final java.lang.String encodeOutput(int version, int iterations, byte[] salt, byte[] nonce, byte[] cipherText) {
        return null;
    }
}