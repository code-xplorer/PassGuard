package com.ismail.creatvt.passguard.manager.security

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED
import androidx.biometric.BiometricManager.BIOMETRIC_STATUS_UNKNOWN
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPrompt
import androidx.biometric.auth.CredentialAuthPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.helpers.classAndMessage
import com.ismail.creatvt.passguard.helpers.showToast

class SecurityManagerImpl(private val listener: AuthenticationResultListener) : SecurityManager,
    BiometricPrompt.AuthenticationCallback() {

    override fun showAuthenticationDialog(
        activity: FragmentActivity,
        onAuthEnrollRequired: () -> Unit
    ) {
        try {
            val biometricManager = BiometricManager.from(activity)

            checkAndShowBiometricPrompt(
                activity,
                null,
                onAuthEnrollRequired,
                biometricManager
            )
        } catch (e: Exception) {
            activity.showToast(e.classAndMessage)
        }
    }

    override fun showAuthenticationDialog(
        fragment: Fragment,
        onAuthEnrollRequired: () -> Unit
    ) {
        try {
            val biometricManager = BiometricManager.from(fragment.requireContext())
            checkAndShowBiometricPrompt(
                null,
                fragment,
                onAuthEnrollRequired,
                biometricManager
            )
        } catch (e: Exception) {
            fragment.requireContext().showToast(e.classAndMessage)
        }
    }

    private fun checkAndShowBiometricPrompt(
        activity: FragmentActivity? = null,
        fragment: Fragment? = null,
        onAuthEnrollRequired: () -> Unit,
        biometricManager: BiometricManager
    ) {
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BIOMETRIC_SUCCESS -> {
                Log.d("SecurityManagerTag", "Success")
                showBiometricPrompt(activity, fragment)
            }

            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d("SecurityManagerTag", "Hardware Unavailable")
                showDeviceCredentialsPrompt(activity, fragment)
            }

            BIOMETRIC_ERROR_NONE_ENROLLED -> onAuthEnrollRequired()
            BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("SecurityManagerTag", "No Hardware")
                showDeviceCredentialsPrompt(activity, fragment)
            }

            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> (activity?:fragment!!.requireActivity()).failure(R.string.fp_not_available)
            BIOMETRIC_ERROR_UNSUPPORTED -> {
                Log.d("SecurityManagerTag", "Unsupported Fingerprint")
                showDeviceCredentialsPrompt(activity, fragment)
            }

            BIOMETRIC_STATUS_UNKNOWN -> (activity?:fragment!!.requireActivity()).failure(R.string.something_went_wrong)
        }
    }

    private fun showDeviceCredentialsPrompt(
        activity: FragmentActivity? = null,
        fragment: Fragment? = null
    ) {
        if(activity == null) {
            BiometricPrompt(
                fragment!!,
                ContextCompat.getMainExecutor(fragment.requireContext()),
                this
            ).authenticate(fragment.requireContext().getPromptInfo(R.string.enter_device_credentials, DEVICE_CREDENTIAL))
        }
    }

    private fun Context.failure(message: Int) {
        showToast(message)
        listener.onAuthenticationFailure()
    }

    private fun Context.getPromptInfo(description: Int, allowedAuthenticators: Int) =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(description))
            .setAllowedAuthenticators(allowedAuthenticators)
            .build()

    private fun showBiometricPrompt(
        activity: FragmentActivity? = null,
        fragment: Fragment? = null
    ) {
        if (activity == null) {
            BiometricPrompt(
                fragment!!,
                ContextCompat.getMainExecutor(fragment.requireActivity()),
                this
            ).authenticate(
                fragment.requireContext().getPromptInfo(
                    R.string.fp_prompt_message,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            )
        } else {
            BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                this
            ).authenticate(
                activity.getPromptInfo(
                    R.string.fp_prompt_message,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            )
        }
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        listener.onAuthenticationSuccess()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        listener.onAuthenticationFailure()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        listener.onAuthenticationFailure()
    }

}