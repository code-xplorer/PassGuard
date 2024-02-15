package com.ismail.creatvt.passguard.viewpassword

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.core.os.BundleCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.addpassword.AddUpdatePasswordActivity
import com.ismail.creatvt.passguard.databinding.FragmentViewPasswordBinding
import com.ismail.creatvt.passguard.helpers.app
import com.ismail.creatvt.passguard.helpers.dp
import com.ismail.creatvt.passguard.helpers.getEnrollIntent
import com.ismail.creatvt.passguard.manager.SecurityManager
import com.ismail.creatvt.passguard.manager.security.SecurityManager
import com.ismail.creatvt.passguard.model.Password

class ViewPasswordFragment : BottomSheetDialogFragment(), ViewPasswordViewModel.ViewPasswordView {

    private var securityManager: SecurityManager? = null
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var binding: FragmentViewPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPasswordBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val password = BundleCompat.getParcelable(requireArguments(), Password.PASSWORD, Password::class.java)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            showAuthenticationDialog()
        }
        val viewModel = ViewModelProvider(this)[ViewPasswordViewModel::class.java]
        viewModel.password = password
        viewModel.viewContract = this

        binding?.viewModel = viewModel
        securityManager = SecurityManager(viewModel)
    }

    private fun showAuthenticationDialog() {
        securityManager?.showAuthenticationDialog(this, this::onAuthEnrollRequired)
    }

    private fun onAuthEnrollRequired() {
        // Prompts the user to create credentials that your app accepts.
        launcher?.launch(getEnrollIntent())
    }

    override fun requestAuthentication() {
        showAuthenticationDialog()
    }

    override fun showDeleteConfirmationDialog(onDeleteConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext(), R.style.PassGuard_AlertDialog)
            .setBackgroundInsetStart(requireContext().dp(6f).toInt())
            .setBackgroundInsetEnd(requireContext().dp(6f).toInt())
            .setTitle(R.string.delete_password)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(R.string.yes) { dialog, action ->
                onDeleteConfirm()
            }.setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    override fun showEditPasswordActivity() {
        val password =
            BundleCompat.getParcelable(requireArguments(), Password.PASSWORD, Password::class.java)
        if (password == null) {
            dismiss()
            return
        }
        showEditActivity(password)
        dismiss()
    }

    private fun showEditActivity(password: Password) {
        app.isEditPasswordLaunched = true
        startActivity(Intent(
            requireContext(),
            AddUpdatePasswordActivity::class.java
        ).apply {
            putExtra(Password.PASSWORD, password)
        })
    }

    override fun finishWithSuccess() {
        dismiss()
    }

    override fun onPause() {
        super.onPause()
        binding?.viewModel?.hideConfidentialData()
    }
}