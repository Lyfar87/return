package com.solanasniper.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class SecureStorage @Inject constructor(
    private val context: Context
) {
    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    fun encryptAndSaveData(key: String, data: ByteArray) {
        val file = File(context.filesDir, key)
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().use { output ->
            output.write(data)
        }
    }

    fun decryptData(key: String): ByteArray? {
        val file = File(context.filesDir, key)
        if (!file.exists()) return null

        return try {
            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val inputStream = encryptedFile.openFileInput()
            val outputStream = ByteArrayOutputStream()
            var nextByte: Int
            while (inputStream.read().also { nextByte = it } != -1) {
                outputStream.write(nextByte)
            }
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    fun deleteSecureData(key: String) {
        val file = File(context.filesDir, key)
        if (file.exists()) file.delete()
    }
}