package com.ismail.creatvt.passguard.addpassword

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ismail.creatvt.passguard.model.Extra

class ExtraViewModel(var extra: Extra, val onDeleteClick:(extra:Extra)->Unit) {

    val labelError = MutableLiveData<String?>(null)
    val valueError = MutableLiveData<String?>(null)

    val label = MutableLiveData(extra.key)
    val value = MutableLiveData(extra.value)
    val hidden = MutableLiveData(extra.hidden)

    fun save() {
        extra.key = label.value?:""
        extra.value = value.value?:""
        extra.hidden = hidden.value?:false
    }

    fun verify(): Boolean {
        val labelText = label.value?:""
        val valueText = value.value?:""

        if(labelText.isEmpty()) {
            labelError.postValue("Please enter label")
            return false
        }
        labelError.postValue(null)

        if(valueText.isEmpty()) {
            valueError.postValue("Please enter value")
            return false
        }
        valueError.postValue(null)
        return true
    }

    fun onDeleteClick(view: View) {
        onDeleteClick(extra)
    }
}