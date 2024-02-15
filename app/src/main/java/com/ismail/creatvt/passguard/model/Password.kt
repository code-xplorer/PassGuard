package com.ismail.creatvt.passguard.model

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.ismail.creatvt.passguard.model.Extra
import com.ismail.creatvt.passguard.model.Website
import kotlinx.parcelize.Parcelize

@Parcelize
data class Password(
    var id: Long,
    var website: Website,
    var username: String,
    var password: String,
    var extras: List<Extra> = arrayListOf()
):Parcelable {
    companion object{
        const val PASSWORD = "PASSWORD"
    }
}