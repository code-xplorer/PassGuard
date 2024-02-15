package com.ismail.creatvt.passguard.settings

import android.app.Activity.CLIPBOARD_SERVICE
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ismail.creatvt.passguard.customviews.PinView
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.databinding.FragmentSettingsBinding
import com.ismail.creatvt.passguard.manager.SecurityManager
import com.ismail.creatvt.passguard.manager.security.AuthenticationResultListener
import com.ismail.creatvt.passguard.manager.security.SecurityManager
import com.ismail.creatvt.passguard.helpers.AESHelper
import com.ismail.creatvt.passguard.helpers.app
import com.ismail.creatvt.passguard.helpers.getEnrollIntent
import com.ismail.creatvt.passguard.helpers.passwordManager
import com.ismail.creatvt.passguard.helpers.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class SettingsFragment : BottomSheetDialogFragment(), SettingsViewModel.ViewContract,
    AuthenticationResultListener {

    private var authRegisterLauncher: ActivityResultLauncher<Intent>? = null
    private var exportLauncher: ActivityResultLauncher<Intent>? = null
    private var importLauncher: ActivityResultLauncher<Intent>? = null
    private var binding: FragmentSettingsBinding? = null
    private var securityManager: SecurityManager? = null

    private sealed class SettingsOperations {
        object Import : SettingsOperations()
        class Export(val uri: Uri) : SettingsOperations()

        object DeleteAll : SettingsOperations()
        object None : SettingsOperations()

    }

    private var operation: SettingsOperations = SettingsOperations.None

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UUID.randomUUID().toString()
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        securityManager = SecurityManager(this)

        authRegisterLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                operation = SettingsOperations.DeleteAll
                showAuthenticationDialog()
            }

        importLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    Log.d("ContentChooserLogTag", "URI: ${it.data?.data}")
                    val uri = it.data?.data
                    if (uri != null) {
                        showKeyInputDialogAndImportPasswords(uri)
                    }
                }
            }

        exportLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    Log.d("ContentChooserLogTag", "URI: ${it.data?.data}")
                    val uri = it.data?.data
                    if (uri != null) {
                        operation = SettingsOperations.Export(uri)
                        showAuthenticationDialog()
                    }
                }
            }

        val viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        viewModel.viewContract = this
        binding?.viewModel = viewModel
    }

    private fun showKeyInputDialogAndImportPasswords(uri: Uri) {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(R.layout.decryption_password_dialog)
            window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            window?.setLayout(MATCH_PARENT, WRAP_CONTENT)

            val pinView = findViewById<PinView>(R.id.pinView)
            findViewById<ImageView>(R.id.closeButton).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.importButton).setOnClickListener {
                val text = pinView.text.toString()
                if(text.length != 8) {
                    showToast(R.string.enter_complete_key)
                    return@setOnClickListener
                }
                importPasswords(uri, text, this::dismiss)
            }
            show()
        }
    }

    private fun importPasswords(uri: Uri, passwordKey: String, dismiss: ()->Unit) {
        requireActivity().contentResolver.openInputStream(uri)?.use {ips ->
            val data = StringBuilder()
            while(true) {
                val byte = ips.read()
                if(byte == -1) {
                    break
                }
                data.append(byte.toChar())
            }
            val passwords = AESHelper.decrypt(data.toString(), passwordKey)
            if(passwords == null) {
                showToast(R.string.unable_to_import)
                return@use
            }
            try {
                passwordManager.import(passwords)
            } catch (e: Exception) {
                showToast(R.string.unable_to_import)
                return@use
            }
            showToast(R.string.imported)
            dismiss()
        }
    }

    private fun writeDataToEncryptedFileAndShowPasswordDialog(uri:Uri) {
        Log.d("ContentChooserLogTag", "Writing encrypted data to file...")
        val passwordKey = UUID.randomUUID().toString().substring(0, 8)
        val encryptedPasswords =
            AESHelper.encrypt(passwordManager.export(), passwordKey)
        requireActivity().contentResolver.openOutputStream(uri).use { ops ->
            ops?.write(encryptedPasswords?.toByteArray())
            ops?.flush()
        }
        showPasswordKeyDialog(passwordKey)
    }

    private fun showPasswordKeyDialog(passwordKey: String) {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(R.layout.encrypted_password_dialog)
            window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            window?.setLayout(MATCH_PARENT, WRAP_CONTENT)

            findViewById<PinView>(R.id.pinView).setText(passwordKey)
            findViewById<ImageView>(R.id.closeButton).setOnClickListener { dismiss() }
            findViewById<Button>(R.id.copyButton).setOnClickListener {
                (requireActivity().getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                    it.setPrimaryClip(ClipData.newPlainText("PassGuard Key", passwordKey))
                    showToast(R.string.copied)
                }
            }
            show()
        }
    }

    private fun showAuthenticationDialog() {
        securityManager?.showAuthenticationDialog(this, this::onAuthEnrollRequired)
    }

    override fun showBackupPin(operation: SettingsViewModel.BackupOperation) {
        if (operation == SettingsViewModel.BackupOperation.Import) {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                app.isDocumentAppLaunched = true
                importLauncher?.launch(this)
            }
        } else {
            val passwords = passwordManager.getAllPasswords()
            if(passwords.isEmpty()) {
                showToast(R.string.no_passwords_to_export)
                return
            }
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "passguard_backup_${System.currentTimeMillis()}")
                app.isDocumentAppLaunched = true
                exportLauncher?.launch(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        app.isDocumentAppLaunched = false
    }
    override fun showDeleteConfirmation() {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.delete_confirmation_dialog)
            window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
            var enableDeleteJob: Job? = null
            val countdownText = findViewById<TextView>(R.id.countdown_text)
            val deleteButton = findViewById<Button>(R.id.delete_button)
            deleteButton.isEnabled = false
            deleteButton.alpha = 0.3f
            val deleteJobTimeout: suspend CoroutineScope.() -> Unit = {
                var i = 10
                countdownText.text = "10"
                countdownText.visibility = View.VISIBLE
                while (isActive && i >= 1) {
                    delay(1000)
                    i--
                    countdownText.text = i.toString()
                }
                countdownText.visibility = View.GONE
                deleteButton.isEnabled = true
                deleteButton.alpha = 1f
            }
            findViewById<CheckBox>(R.id.password_checkbox).setOnCheckedChangeListener { buttonView, isChecked ->
                enableDeleteJob = if (isChecked) {
                    lifecycleScope.launch(block = deleteJobTimeout)
                } else {
                    deleteButton.isEnabled = false
                    deleteButton.alpha = 0.3f
                    countdownText.visibility = View.GONE
                    enableDeleteJob?.cancel()
                    null
                }
            }
            deleteButton.setOnClickListener {
                operation = SettingsOperations.DeleteAll
                showAuthenticationDialog()
                dismiss()
            }

            findViewById<Button>(R.id.cancel_button).setOnClickListener {
                enableDeleteJob?.cancel()
                dismiss()

            }
            show()
        }

    }

    private fun onAuthEnrollRequired() {
        // Prompts the user to create credentials that your app accepts.
        authRegisterLauncher?.launch(getEnrollIntent())
    }

    override fun onAuthenticationSuccess() {
        val op = operation
        Log.d("SettingsFragmentTag", "Operation: ${op::class.java.simpleName}")
        if (op == SettingsOperations.DeleteAll) {
            Log.d("SettingsFragmentTag", "Deleting All...")
            passwordManager.clearPasswords()
        } else if (op is SettingsOperations.Export) {
            writeDataToEncryptedFileAndShowPasswordDialog(op.uri)
        }
        operation = SettingsOperations.None
    }

    override fun onAuthenticationFailure() {
        operation = SettingsOperations.None
    }

}
