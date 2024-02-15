package com.ismail.creatvt.passguard.helpers

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ismail.creatvt.passguard.BuildConfig

open class SecureActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if(!BuildConfig.DEBUG) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}