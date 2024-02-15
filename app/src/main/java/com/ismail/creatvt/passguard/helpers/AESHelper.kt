package com.ismail.creatvt.passguard.helpers

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.InvalidParameterSpecException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


object AESHelper {

    fun encrypt(message: String, secret: String):String? {
        return try {
            encryptMsg(message, generateKey(secret))
        } catch (e: Exception) {
            Log.d("AESHelperLogTag", e.classAndMessage)
            null
        }
    }

    fun decrypt(message: String, secret: String): String? {
        return try {
            decryptMsg(message, generateKey(secret))
        } catch (e: Exception) {
            Log.d("AESHelperLogTag", e.classAndMessage)
            null
        }
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidParameterSpecException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        UnsupportedEncodingException::class
    )
    private fun encryptMsg(message: String, secret: SecretKey?): String? {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secret)
        val cipherText = cipher.doFinal(message.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(cipherText, Base64.NO_WRAP)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidParameterSpecException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        UnsupportedEncodingException::class
    )
    private fun decryptMsg(cipherText: String?, secret: SecretKey?): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secret)
        val decode: ByteArray = Base64.decode(cipherText, Base64.NO_WRAP)
        return String(cipher.doFinal(decode), Charsets.UTF_8)
    }

    private fun generateScrambledKey(secret:String) = secret.scramble().scramble2().scramble().scramble2()

    private fun String.scramble():String {
        val scrambledString = StringBuilder()
        forEachIndexed { index, c ->
            scrambledString.append(c)
            scrambledString.append(get(length - 1 - index))
        }
        return scrambledString.toString()
    }

    private fun String.scramble2():String {
        val part1 = substring(0, length/2)
        val part2 = substring(length/2)
        val scrambledString = StringBuilder()
        for(i in 0 until part1.length) {
            scrambledString.append(part1[i])
            scrambledString.append(part2[i])
        }
        return scrambledString.toString()
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun generateKey(key: String): SecretKey {
        val scrambledKey = generateScrambledKey(key)
        Log.d("AESHelperTag", "ScrambledKey: $scrambledKey")
        return SecretKeySpec(scrambledKey.toByteArray(), "AES")
    }

}