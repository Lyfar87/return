package com.solanasniper.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthHelper(
    private val context: Context,
    private val activity: FragmentActivity
) {
    fun authenticate(
        title: String = "Authenticate",
        callback: BiometricPrompt.AuthenticationCallback
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setNegativeButtonText("Cancel")
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }

    fun isBiometricAvailable(): Boolean {
        return BiometricManager.from(context).canAuthenticate() ==
                BiometricManager.BIOMETRIC_SUCCESS
    }
}