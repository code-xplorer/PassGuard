package com.ismail.creatvt.passguard.viewpassword

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ismail.creatvt.passguard.PassGuardApplication
import com.ismail.creatvt.passguard.manager.security.AuthenticationResultListener
import com.ismail.creatvt.passguard.model.Password
import com.ismail.creatvt.passguard.helpers.WebsiteFactory

class ViewPasswordViewModel : ViewModel(), AuthenticationResultListener {

    private val _extraViewModels = MutableLiveData(listOf<ViewExtraViewModel>())
    val extraViewModels: LiveData<List<ViewExtraViewModel>>
        get() = _extraViewModels

    var password: Password? = null
        set(value) {
            field = value
            _extraViewModels.value = value?.extras?.map { ViewExtraViewModel(it) } ?: listOf()
        }

    val shouldShowOtherName: Boolean
        get() = password?.website?.name == WebsiteFactory.OTHER

    private var didRequestAuthentication = false

    private var _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean>
        get() = _passwordVisible

    var viewContract: ViewPasswordView? = null
        set(value) {
            field = value
            val extras = extraViewModels.value ?: listOf()
            extras.forEach {
                it.viewContract = field
            }
            Log.d("ViewPasswordVMTag", "View contract set to extras... ${extras.size}")
            _extraViewModels.value = extras
        }

    fun onDeleteClick(view: View) {
        val passwordToDelete = password ?: return
        viewContract?.showDeleteConfirmationDialog {
            (view.context.applicationContext as PassGuardApplication)
                .passwordManager.deletePassword(passwordToDelete)
            viewContract?.finishWithSuccess()
        }
    }

    private var isEditAuthRequested = false

    fun onEditClick() {
        isEditAuthRequested = true
        viewContract?.requestAuthentication()
    }

    fun hideConfidentialData() {
        _passwordVisible.postValue(false)
        extraViewModels.value?.forEach {
            it.hideValue()
        }
    }

    fun onViewPasswordRequest(visibility: Boolean) {
        if (visibility) {
            didRequestAuthentication = true
            viewContract?.requestAuthentication()
        } else {
            _passwordVisible.postValue(false)
        }
    }

    override fun onAuthenticationSuccess() {
        if(isEditAuthRequested) {
            isEditAuthRequested = false
            viewContract?.showEditPasswordActivity()
            return
        }
        if (didRequestAuthentication) {
            didRequestAuthentication = false
            _passwordVisible.postValue(true)
        }
        extraViewModels.value?.forEach {
            it.onAuthenticationSuccess()
        }
    }

    override fun onAuthenticationFailure() {
        if(isEditAuthRequested) {
            isEditAuthRequested = false
            return
        }
        if (didRequestAuthentication) {
            didRequestAuthentication = false
            _passwordVisible.postValue(false)
        }
        extraViewModels.value?.forEach {
            it.onAuthenticationFailure()
        }
    }

    interface ViewPasswordView {
        fun requestAuthentication()
        fun showDeleteConfirmationDialog(onDeleteConfirm: () -> Unit)
        fun showEditPasswordActivity()
        fun finishWithSuccess()

    }
}