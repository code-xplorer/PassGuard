package com.ismail.creatvt.passguard.manager.security

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface SecurityManager {

    fun showAuthenticationDialog(activity: FragmentActivity, onAuthEnrollRequired: ()->Unit)
    fun showAuthenticationDialog(fragment: Fragment, onAuthEnrollRequired: ()->Unit)

}