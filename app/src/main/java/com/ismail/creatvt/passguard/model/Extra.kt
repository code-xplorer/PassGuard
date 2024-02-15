package com.ismail.creatvt.passguard.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Extra(
    var id: Long = System.currentTimeMillis(),
    var key: String = "",
    var value: String = "",
    var hidden: Boolean = false
):Parcelable