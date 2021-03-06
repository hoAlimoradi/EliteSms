package com.alimoradi.elitesms.feature.backup

import android.content.Context
import com.alimoradi.common.utils.makeToast
import com.alimoradi.domain.interactor.PerformBackup
import com.alimoradi.domain.manager.PermissionManager
import com.alimoradi.domain.repository.BackupRepository
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.Navigator
import com.alimoradi.elitesms.common.base.QkPresenter
import com.alimoradi.elitesms.common.util.BillingManager
import com.alimoradi.elitesms.common.util.DateFormatter
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BackupPresenter @Inject constructor(
    private val backupRepo: BackupRepository,
    private val billingManager: BillingManager,
    private val context: Context,
    private val dateFormatter: DateFormatter,
    private val navigator: Navigator,
    private val performBackup: PerformBackup,
    private val permissionManager: PermissionManager
) : QkPresenter<BackupView, BackupState>(BackupState()) {

    private val storagePermissionSubject: Subject<Boolean> = BehaviorSubject.createDefault(permissionManager.hasStorage())

    init {
        disposables += backupRepo.getBackupProgress()
                .sample(16, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe { progress -> newState { copy(backupProgress = progress) } }

        disposables += backupRepo.getRestoreProgress()
                .sample(16, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe { progress -> newState { copy(restoreProgress = progress) } }

        disposables += storagePermissionSubject
                .distinctUntilChanged()
                .switchMap { backupRepo.getBackups() }
                .doOnNext { backups -> newState { copy(backups = backups) } }
                .map { backups -> backups.map { it.date }.max() ?: 0L }
                .map { lastBackup ->
                    when (lastBackup) {
                        0L -> context.getString(R.string.backup_never)
                        else -> dateFormatter.getDetailedTimestamp(lastBackup)
                    }
                }
                .startWith(context.getString(R.string.backup_loading))
                .subscribe { lastBackup -> newState { copy(lastBackup = lastBackup) } }

        disposables += billingManager.upgradeStatus
                .subscribe { upgraded -> newState { copy(upgraded = upgraded) } }
    }

    override fun bindIntents(view: BackupView) {
        super.bindIntents(view)

        view.activityVisible()
                .map { permissionManager.hasStorage() }
                .autoDisposable(view.scope())
                .subscribe(storagePermissionSubject)

        view.restoreClicks()
                .withLatestFrom(
                        backupRepo.getBackupProgress(),
                        backupRepo.getRestoreProgress(),
                        billingManager.upgradeStatus)
                { _, backupProgress, restoreProgress, upgraded ->
                    when {
                        !upgraded -> context.makeToast(R.string.backup_restore_error_plus)
                        backupProgress.running -> context.makeToast(R.string.backup_restore_error_backup)
                        restoreProgress.running -> context.makeToast(R.string.backup_restore_error_restore)
                        !permissionManager.hasStorage() -> view.requestStoragePermission()
                        else -> view.selectFile()
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.restoreFileSelected()
                .autoDisposable(view.scope())
                .subscribe { view.confirmRestore() }

        view.restoreConfirmed()
                .withLatestFrom(view.restoreFileSelected()) { _, backup -> backup }
                .autoDisposable(view.scope())
                .subscribe { backup -> RestoreBackupService.start(context, backup.path) }

        view.stopRestoreClicks()
                .autoDisposable(view.scope())
                .subscribe { view.stopRestore() }

        view.stopRestoreConfirmed()
                .autoDisposable(view.scope())
                .subscribe { backupRepo.stopRestore() }

        view.fabClicks()
                .withLatestFrom(billingManager.upgradeStatus) { _, upgraded -> upgraded }
                .autoDisposable(view.scope())
                .subscribe { upgraded ->
                    when {
                        !upgraded -> navigator.showQksmsPlusActivity("backup_fab")
                        !permissionManager.hasStorage() -> view.requestStoragePermission()
                        upgraded -> performBackup.execute(Unit)
                    }
                }
    }

}