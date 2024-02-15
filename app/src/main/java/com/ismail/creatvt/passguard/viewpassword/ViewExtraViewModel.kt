package com.ismail.creatvt.passguard.viewpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ismail.creatvt.passguard.addpassword.ViewContract
import com.ismail.creatvt.passguard.manager.security.AuthenticationResultListener
import com.ismail.creatvt.passguard.model.Extra

class ViewExtraViewModel(val extra: Extra) : AuthenticationResultListener {

    var viewContract: ViewPasswordViewModel.ViewPasswordView? = null

    private var didRequestAuthentication: Boolean = false

    private var _isValueVisible = MutableLiveData(!extra.hidden)
    val isValueVisible: LiveData<Boolean>
        get() = _isValueVisible

    fun onVisibilityRequest(visibility: Boolean) {
        if (visibility) {
            didRequestAuthentication = true
            viewContract?.requestAuthentication()
        } else {
            _isValueVisible.postValue(false)
        }
    }

    fun hideValue() {
        if (extra.hidden) {
            _isValueVisible.postValue(false)
        }
    }

    override fun onAuthenticationSuccess() {
        if (didRequestAuthentication) {
            didRequestAuthentication = false
            if (extra.hidden) {
                _isValueVisible.postValue(true)
            }
        }
    }

    override fun onAuthenticationFailure() {
        if (didRequestAuthentication) {
            didRequestAuthentication = false
            if (extra.hidden) {
                _isValueVisible.postValue(false)
            }
        }
    }

}