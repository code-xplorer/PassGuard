package com.ismail.creatvt.passguard.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.P
import android.os.Build.VERSION_CODES.R
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.fragment.app.Fragment
import com.ismail.creatvt.passguard.PassGuardApplication
import com.ismail.creatvt.passguard.manager.password.PasswordManager


fun getPasswordKey(index: Int): String {
    return "#P@Wd$index"
}

fun Context.showToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.dp(dip: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip,
        resources.displayMetrics
    )
}

val Exception.classAndMessage: String
    get() = "${this::class.simpleName}(\"$message\")"
val Activity.passwordManager: PasswordManager
    get() = app.passwordManager
val Fragment.passwordManager: PasswordManager
    get() = app.passwordManager

val Activity.app: PassGuardApplication
    get() = (application as PassGuardApplication)
val Fragment.app: PassGuardApplication
    get() = (requireActivity().application as PassGuardApplication)

fun Fragment.showToast(message: Int) {
    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
fun getEnrollIntent() = when {
    Build.VERSION.SDK_INT >= R -> Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
        putExtra(
            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }

    Build.VERSION.SDK_INT >= P -> Intent(Settings.ACTION_FINGERPRINT_ENROLL)
    else -> Intent(Settings.ACTION_SECURITY_SETTINGS)
}