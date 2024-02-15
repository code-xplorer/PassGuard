package com.ismail.creatvt.passguard.manager.password

import androidx.lifecycle.LiveData
import com.ismail.creatvt.passguard.model.Password

interface PasswordManager {

    val passwords:LiveData<List<Password>>

    fun addPassword(password: Password)

    fun updatePassword(password: Password)

    fun deletePassword(password: Password)

    fun clearPasswords()

    fun getPassword(id: Long): Password?

    fun getAllPasswords(): List<Password>

    fun close()
    fun movePasswords(from: Int, to: Int)

    fun export():String

    fun import(data: String)
}