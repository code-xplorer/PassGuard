package com.ismail.creatvt.passguard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ismail.creatvt.passguard.helpers.SecureActivity
import com.ismail.creatvt.passguard.helpers.getEnrollIntent
import com.ismail.creatvt.passguard.manager.SecurityManager
import com.ismail.creatvt.passguard.manager.security.AuthenticationResultListener
import com.ismail.creatvt.passguard.manager.security.SecurityManager

class LockedActivity : SecureActivity(), AuthenticationResultListener {

    private var launcher: ActivityResultLauncher<Intent>? = null
    private var securityManager:SecurityManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locked)
        securityManager = SecurityManager(this)

        Log.d("SecurityManagerTag", "Lock Screen Launched")
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            showAuthenticationDialog()
        }
        findViewById<Button>(R.id.unlockButton).setOnClickListener {
            showAuthenticationDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        showAuthenticationDialog()
    }

    private fun showAuthenticationDialog() {
        securityManager?.showAuthenticationDialog(this, this::onAuthEnrollRequired)
    }

    private fun onAuthEnrollRequired() {
        // Prompts the user to create credentials that your app accepts.
        launcher?.launch(getEnrollIntent())
    }

    override fun onBackPressed() {

    }

    override fun onAuthenticationSuccess() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onAuthenticationFailure() {
        showAuthenticationDialog()
    }

}