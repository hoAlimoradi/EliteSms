package com.alimoradi.elitesms.feature.backup

import com.alimoradi.domain.model.BackupFile
import com.alimoradi.domain.repository.BackupRepository


data class BackupState(
    val backupProgress: BackupRepository.Progress = BackupRepository.Progress.Idle(),
    val restoreProgress: BackupRepository.Progress = BackupRepository.Progress.Idle(),
    val lastBackup: String = "",
    val backups: List<BackupFile> = listOf(),
    val upgraded: Boolean = false
)