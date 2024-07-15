package com.ismail.creatvt.passguard.passwordlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ismail.creatvt.passguard.PassGuardApplication
import com.ismail.creatvt.passguard.helpers.merge
import com.ismail.creatvt.passguard.helpers.passwordManager

class PasswordListViewModel(application: Application) : AndroidViewModel(application) {
    val searchQuery = MutableLiveData("")
    val isSearchModeEnabled = MutableLiveData(false)

    private val passwordManager = (application as PassGuardApplication).passwordManager

    var filteredPasswords = searchQuery.merge(passwordManager.passwords) { query, passwords ->
        if(query.isNullOrEmpty()) {
            return@merge passwords
        }
        var websiteQuery:String = query
        var usernameQuery:String = query
        var isSplit = false
        if(query.contains("::")) {
            isSplit = true

            query.split("::").let {
                websiteQuery = it[0]
                usernameQuery = it[1]
            }
        }
        return@merge passwords?.filter {
            val websiteNameFilter = it.website.websiteName.contains(websiteQuery, true)
            val usernameFilter = it.username.contains(usernameQuery, true)

            return@filter if(isSplit) {
                websiteNameFilter && usernameFilter
            } else {
                websiteNameFilter || usernameFilter
            }
        }
    }


    fun onSearchImageClick() {
        isSearchModeEnabled.value = isSearchModeEnabled.value == false // Toggle Boolean
    }
}