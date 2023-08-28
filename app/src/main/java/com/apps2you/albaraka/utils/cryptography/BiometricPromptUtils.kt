package com.apps2you.albaraka.utils.cryptography

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.apps2you.albaraka.R

object BiometricPromptUtils {
    fun createBiometricPrompt(
            activity: AppCompatActivity,
            processSuccess: (BiometricPrompt.AuthenticationResult?, Boolean) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result, true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == 13 || errorCode == 10) {
                    processSuccess(null, false)
                }
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun createPromptInfo(activity: AppCompatActivity): BiometricPrompt.PromptInfo =
            BiometricPrompt.PromptInfo.Builder().apply {
                setTitle(activity.getString(R.string.prompt_info_title))
//                setSubtitle(activity.getString(R.string.prompt_info_subtitle))
//                setDescription(activity.getString(R.string.prompt_info_description))
                setNegativeButtonText(activity.getString(R.string.prompt_info_cancel))
                setAllowedAuthenticators(BIOMETRIC_STRONG)
            }.build()
}