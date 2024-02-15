package com.ismail.creatvt.passguard.manager.security

interface AuthenticationResultListener {

    fun onAuthenticationSuccess()

    fun onAuthenticationFailure()

}