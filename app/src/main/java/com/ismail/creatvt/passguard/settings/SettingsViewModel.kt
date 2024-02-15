package com.ismail.creatvt.passguard.settings

import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    var viewContract: ViewContract? = null

    fun onImport() {
        viewContract?.showBackupPin(BackupOperation.Import)
    }

    fun onExport() {
        viewContract?.showBackupPin(BackupOperation.Export)
    }

    fun onDeleteAll() {
        viewContract?.showDeleteConfirmation()
    }

    interface ViewContract {
        fun showBackupPin(operation: BackupOperation)
        fun showDeleteConfirmation()
    }

    enum class BackupOperation {
        Import,
        Export
    }
}