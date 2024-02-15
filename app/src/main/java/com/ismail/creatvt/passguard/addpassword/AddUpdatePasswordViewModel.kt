package com.ismail.creatvt.passguard.addpassword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.ismail.creatvt.passguard.helpers.ColorFactory
import com.ismail.creatvt.passguard.PassGuardApplication
import com.ismail.creatvt.passguard.manager.password.PasswordManager
import com.ismail.creatvt.passguard.model.Extra
import com.ismail.creatvt.passguard.model.Password
import com.ismail.creatvt.passguard.helpers.WebsiteFactory

class AddUpdatePasswordViewModel(application: Application) : AndroidViewModel(application) {

    private var passwordManager: PasswordManager = (application as PassGuardApplication).passwordManager

    var viewContract: ViewContract? = null
    val websiteList = WebsiteFactory.getAllWebsites()

    val selectedWebsite = MutableLiveData<String>()

    val shouldShowOtherName: LiveData<Boolean> = selectedWebsite.map {
        it == WebsiteFactory.OTHER
    }

    val username = MutableLiveData("")
    val password = MutableLiveData("")
    val usernameError = MutableLiveData<String?>(null)
    val passwordError = MutableLiveData<String?>(null)
    val extraInfo = MutableLiveData<List<ExtraViewModel>>(arrayListOf())
    val websiteError = MutableLiveData<String?>(null)
    val otherWebsiteName = MutableLiveData("")
    val otherWebsiteError = MutableLiveData<String?>(null)

    var originalPassword: Password? = null
        set(value) {
            field = value
            username.value = value?.username
            password.value = value?.password
            selectedWebsite.value = value?.website?.name?:""
            otherWebsiteName.value = value?.website?.otherName?:""
            extraInfo.value = value?.extras?.map {
                ExtraViewModel(it, this::onDeleteExtraClick)
            }
        }

    fun onDoneClick() {
        val usernameText = username.value?.trim()
        val passwordText = password.value?.trim()

        val selectedWebsite = selectedWebsite.value ?: ""
        val website = websiteList.firstOrNull { it.name == selectedWebsite }

        val otherWebsiteText = otherWebsiteName.value?.trim()

        if (website == null) {
            websiteError.postValue("Please select website")
            return
        }
        websiteError.postValue(null)
        if(selectedWebsite == WebsiteFactory.OTHER) {
            if(otherWebsiteText.isNullOrEmpty()) {
                otherWebsiteError.postValue("Please enter other website name")
                return
            }
            website.otherName = otherWebsiteText
            otherWebsiteError.postValue(null)
        }

        if (usernameText.isNullOrEmpty()) {
            usernameError.postValue("Please enter username")
            return
        }
        usernameError.postValue(null)

        if (passwordText.isNullOrEmpty()) {
            passwordError.postValue("Please enter password")
            return
        }
        passwordError.postValue(null)

        val extras = extraInfo.value ?: arrayListOf()
        for (extra in extras) {
            if (!extra.verify()) {
                return
            }
            extra.save()
        }
        if(website.icon == "" && website.color == "other") {
            website.color = ColorFactory.getRandomColor().name
        }
        val original = originalPassword
        if (original == null) {
            passwordManager.addPassword(
                Password(
                    System.currentTimeMillis(),
                    website,
                    usernameText,
                    passwordText,
                    extras.map { it.extra }
                )
            )
        } else {
            original.username = usernameText
            original.password = passwordText
            original.website = website
            original.extras = extras.map { it.extra }
            passwordManager.updatePassword(original)
        }
        viewContract?.finishWithSuccess()
    }

    fun onAddExtraClick() {
        val extras = ArrayList(extraInfo.value ?: arrayListOf())
        extras.add(ExtraViewModel(Extra(), this::onDeleteExtraClick))
        extraInfo.value = extras
    }

    private fun onDeleteExtraClick(extra: Extra) {
        val extras = ArrayList(extraInfo.value ?: arrayListOf())
        extraInfo.value = extras.filter { it.extra.id != extra.id }
    }
}