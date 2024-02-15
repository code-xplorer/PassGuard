package com.ismail.creatvt.passguard.passwordlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ismail.creatvt.passguard.LockedActivity
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.helpers.SecureActivity
import com.ismail.creatvt.passguard.addpassword.AddUpdatePasswordActivity
import com.ismail.creatvt.passguard.model.Password
import com.ismail.creatvt.passguard.model.Password.Companion.PASSWORD
import com.ismail.creatvt.passguard.settings.SettingsFragment
import com.ismail.creatvt.passguard.helpers.app
import com.ismail.creatvt.passguard.helpers.passwordManager
import com.ismail.creatvt.passguard.viewpassword.ViewPasswordFragment

class PasswordListActivity : SecureActivity() {

    private var adapter: PasswordsAdapter? = null
    private var lockActivityLauncher: ActivityResultLauncher<Intent>? = null
    private var passwordActivityLauncher: ActivityResultLauncher<Intent>? = null
    private var isFirstLaunch: Boolean = false
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstLaunch = true
        isPaused = false
        setContentView(R.layout.activity_password_list)

        setUpPasswordsListRecyclerView()

        findViewById<ImageView>(R.id.settingsButton).setOnClickListener {
            SettingsFragment().show(supportFragmentManager, "SettingsFragment")
        }

        lockActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    isPaused = false
                }
            }

        passwordActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    isPaused = false
                }
            }
    }

    private fun setUpPasswordsListRecyclerView() {
        val passwordListRecyclerView = findViewById<RecyclerView>(R.id.passwordsList)
        adapter = PasswordsAdapter(passwordManager.passwords, this, this::onPasswordItemClick)
        passwordListRecyclerView.adapter = adapter
        passwordListRecyclerView.layoutManager = LinearLayoutManager(this)
        ItemTouchHelper(MoveItemCallback(passwordManager)).attachToRecyclerView(passwordListRecyclerView)
        val noPasswordGroup = findViewById<Group>(R.id.noPasswordGroup)

        passwordManager.passwords.observe(this) {
            noPasswordGroup.visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onPasswordItemClick(password: Password) {
        ViewPasswordFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PASSWORD, password)
            }
            show(supportFragmentManager, password.website.websiteName)
        }
    }

    override fun onResume() {
        super.onResume()
        if ((isFirstLaunch || isPaused) && !app.isDocumentAppLaunched && !app.isEditPasswordLaunched) {
            isFirstLaunch = false
            app.isEditPasswordLaunched = false
            lockActivityLauncher?.launch(Intent(this, LockedActivity::class.java))
        }
        isPaused = false
    }

    override fun onRestart() {
        super.onRestart()
        isFirstLaunch = false
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    fun onPasswordButtonClick(view: View) {
        passwordActivityLauncher?.launch(Intent(this, AddUpdatePasswordActivity::class.java))
    }

}