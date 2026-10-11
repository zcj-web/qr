package com.zcj.qr.crypto

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import android.os.Build

object Aes256GcmCrypto {
    private const val VERSION = 1
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_LENGTH_BITS = 256
    private const val TAG_LENGTH_BITS = 128
    private const val SALT_LENGTH = 16
    private const val NONCE_LENGTH = 12
    private const val PBKDF2_ITERATIONS_DEFAULT = 100_000

    val IterationOptions = listOf(10_000, 100_000, 500_000)

    fun encrypt(plaintext: String, password: String, iterations: Int = PBKDF2_ITERATIONS_DEFAULT): String {
        require(iterations in IterationOptions) { "iterations must be one of $IterationOptions" }
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt, iterations)
        val nonce = ByteArray(NONCE_LENGTH).also { SecureRandom().nextBytes(it) }

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, nonce))
        val cipherText = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        return encodeOutput(VERSION, iterations, salt, nonce, cipherText)
    }

    fun decrypt(base64Ciphertext: String, password: String): String? {
        val raw = Base64.getDecoder().decode(base64Ciphertext)
        return try {
            val version = raw[0].toInt() and 0xFF
            require(version == VERSION) { "Unsupported version: $version" }

            val iterations = raw[1].toInt() and 0xFF shl 24 or
                    (raw[2].toInt() and 0xFF shl 16) or
                    (raw[3].toInt() and 0xFF shl 8) or
                    (raw[4].toInt() and 0xFF)

            val saltStart = 5
            val nonceStart = 5 + SALT_LENGTH
            val cipherStart = 5 + SALT_LENGTH + NONCE_LENGTH
            val salt = raw.copyOfRange(saltStart, saltStart + SALT_LENGTH)
            val nonce = raw.copyOfRange(nonceStart, nonceStart + NONCE_LENGTH)
            val cipherText = raw.copyOfRange(cipherStart, raw.size)

            val key = deriveKey(password, salt, iterations)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, nonce))
            val plainText = cipher.doFinal(cipherText)
            String(plainText, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    private fun deriveKey(password: String, salt: ByteArray, iterations: Int): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val encodedKey = (factory.generateSecret(spec) as javax.crypto.SecretKey).encoded
        return SecretKeySpec(encodedKey, "AES")
    }

    private fun encodeOutput(
        version: Int,
        iterations: Int,
        salt: ByteArray,
        nonce: ByteArray,
        cipherText: ByteArray
    ): String {
        val totalLen = 1 + 4 + salt.size + nonce.size + cipherText.size
        val output = ByteArray(totalLen)
        var offset = 0
        output[offset++] = version.toByte()
        output[offset++] = ((iterations shr 24) and 0xFF).toByte()
        output[offset++] = ((iterations shr 16) and 0xFF).toByte()
        output[offset++] = ((iterations shr 8) and 0xFF).toByte()
        output[offset++] = (iterations and 0xFF).toByte()
        System.arraycopy(salt, 0, output, offset, salt.size); offset += salt.size
        System.arraycopy(nonce, 0, output, offset, nonce.size); offset += nonce.size
        System.arraycopy(cipherText, 0, output, offset, cipherText.size)
        return Base64.getEncoder().encodeToString(output)
    }
}
