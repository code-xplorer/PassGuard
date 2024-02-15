package com.ismail.creatvt.passguard.model

import android.os.Parcelable
import com.ismail.creatvt.passguard.helpers.WebsiteFactory
import kotlinx.parcelize.Parcelize

@Parcelize
data class Website(
    val icon: String,
    val name: String,
    var color: String,
    var otherName: String? = "Unknown"
) : Parcelable {

    val websiteName: String
        get() = if (name == WebsiteFactory.OTHER) otherName ?: "Unknown" else name

    override fun toString(): String {
        return name
    }
}