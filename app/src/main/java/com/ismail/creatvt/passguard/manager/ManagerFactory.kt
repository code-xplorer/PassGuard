package com.ismail.creatvt.passguard.manager

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.ismail.creatvt.passguard.manager.password.PasswordManager
import com.ismail.creatvt.passguard.manager.password.PasswordManagerImpl
import com.ismail.creatvt.passguard.manager.security.AuthenticationResultListener
import com.ismail.creatvt.passguard.manager.security.SecurityManager
import com.ismail.creatvt.passguard.manager.security.SecurityManagerImpl

fun PasswordManager(context:Context):PasswordManager {
    return PasswordManagerImpl(context)
}

fun SecurityManager(listener: AuthenticationResultListener):SecurityManager {
    return SecurityManagerImpl(listener)
}