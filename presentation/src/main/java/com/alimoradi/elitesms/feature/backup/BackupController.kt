package com.alimoradi.elitesms.feature.backup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.alimoradi.domain.model.BackupFile
import com.alimoradi.domain.repository.BackupRepository
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkController
import com.alimoradi.elitesms.common.util.DateFormatter
import com.alimoradi.elitesms.common.util.extensions.getLabel
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.widget.PreferenceView
import com.alimoradi.elitesms.databinding.BackupControllerBinding
import com.alimoradi.elitesms.databinding.BackupListDialogBinding
import com.alimoradi.elitesms.injection.appComponent
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.common.util.extensions.setPositiveButton
/*import com.moez.QKSMS.R
import com.moez.QKSMS.common.base.QkController
import com.moez.QKSMS.common.util.DateFormatter
import com.moez.QKSMS.common.util.extensions.getLabel
import com.moez.QKSMS.common.util.extensions.setBackgroundTint
import com.moez.QKSMS.common.util.extensions.setPositiveButton
import com.moez.QKSMS.common.util.extensions.setTint
import com.moez.QKSMS.common.widget.PreferenceView
import com.moez.QKSMS.databinding.BackupControllerBinding
import com.moez.QKSMS.databinding.BackupListDialogBinding
import com.moez.QKSMS.injection.appComponent
import com.moez.QKSMS.model.BackupFile
import com.moez.QKSMS.repository.BackupRepository*/
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class BackupController : QkController<BackupView, BackupState, BackupPresenter, BackupControllerBinding>(
        BackupControllerBinding::inflate
), BackupView {

    @Inject lateinit var adapter: BackupAdapter
    @Inject lateinit var dateFormatter: DateFormatter
    @Inject override lateinit var presenter: BackupPresenter

    private val activityVisibleSubject: Subject<Unit> = PublishSubject.create()
    private val confirmRestoreSubject: Subject<Unit> = PublishSubject.create()
    private val stopRestoreSubject: Subject<Unit> = PublishSubject.create()

    private val backupFilesDialog by lazy {
        val binding = BackupListDialogBinding.inflate(LayoutInflater.from(activity))
                .apply { files.adapter = adapter.apply { emptyView = empty } }

        AlertDialog.Builder(activity!!)
                .setView(binding.root)
                .setCancelable(true)
                .create()
    }
    private val confirmRestoreDialog by lazy {
        androidx.appcompat.app.AlertDialog.Builder(activity!!)
            .setTitle(R.string.backup_restore_confirm_title)
            .setMessage(R.string.backup_restore_confirm_message)
            .setPositiveButton(R.string.backup_restore_title, confirmRestoreSubject)
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }

    private val stopRestoreDialog by lazy {
        androidx.appcompat.app.AlertDialog.Builder(activity!!)
            .setTitle(R.string.backup_restore_stop_title)
            .setMessage(R.string.backup_restore_stop_message)
            .setPositiveButton(R.string.button_stop, stopRestoreSubject)
            .setNegativeButton(R.string.button_cancel, null)
            .create()
    }


    init {
        appComponent.inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.backup_title)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()

        themedActivity?.colors?.theme()?.let { theme ->
            binding.progressBar.indeterminateTintList = ColorStateList.valueOf(theme.theme)
            binding.progressBar.progressTintList = ColorStateList.valueOf(theme.theme)
            binding.fab.setBackgroundTint(theme.theme)
            binding.fabIcon.setTint(theme.textPrimary)
            binding.fabLabel.setTextColor(theme.textPrimary)
        }

        // Make the list titles bold
        binding.linearLayout.children
                .mapNotNull { view -> view as? PreferenceView }
                .map { preferenceView -> preferenceView.binding.titleView }
                .forEach { it.setTypeface(it.typeface, Typeface.BOLD) }
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        activityVisibleSubject.onNext(Unit)
    }

    override fun render(state: BackupState) {
        when {
            state.backupProgress.running -> {
                binding.progressIcon.setImageResource(R.drawable.ic_file_upload_black_24dp)
                binding.progressTitle.setText(R.string.backup_backing_up)
                binding.progressSummary.text = state.backupProgress.getLabel(activity!!)
                binding.progressSummary.isVisible = binding.progressSummary.text.isNotEmpty()
                binding.progressCancel.isVisible = false
                val running = (state.backupProgress as? BackupRepository.Progress.Running)
                binding.progressBar.isVisible = state.backupProgress.indeterminate || running?.max ?: 0 > 0
                binding.progressBar.isIndeterminate = state.backupProgress.indeterminate
                binding.progressBar.max = running?.max ?: 0
                binding.progressBar.progress = running?.count ?: 0
                binding.progress.isVisible = true
                binding.fab.isVisible = false
            }

            state.restoreProgress.running -> {
                binding.progressIcon.setImageResource(R.drawable.ic_file_download_black_24dp)
                binding.progressTitle.setText(R.string.backup_restoring)
                binding.progressSummary.text = state.restoreProgress.getLabel(activity!!)
                binding.progressSummary.isVisible = binding.progressSummary.text.isNotEmpty()
                binding.progressCancel.isVisible = true
                val running = (state.restoreProgress as? BackupRepository.Progress.Running)
                binding.progressBar.isVisible = state.restoreProgress.indeterminate || running?.max ?: 0 > 0
                binding.progressBar.isIndeterminate = state.restoreProgress.indeterminate
                binding.progressBar.max = running?.max ?: 0
                binding.progressBar.progress = running?.count ?: 0
                binding.progress.isVisible = true
                binding.fab.isVisible = false
            }

            else -> {
                binding.progress.isVisible = false
                binding.fab.isVisible = true
            }
        }

        binding.backup.summary = state.lastBackup

        adapter.data = state.backups

        binding.fabIcon.setImageResource(when (state.upgraded) {
            true -> R.drawable.ic_file_upload_black_24dp
            false -> R.drawable.ic_star_black_24dp
        })

        binding.fabLabel.setText(when (state.upgraded) {
            true -> R.string.backup_now
            false -> R.string.title_qksms_plus
        })
    }

    override fun activityVisible(): Observable<*> = activityVisibleSubject

    override fun restoreClicks(): Observable<*> = binding.restore.clicks()

    override fun restoreFileSelected(): Observable<BackupFile> = adapter.backupSelected
            .doOnNext { backupFilesDialog.dismiss() }

    override fun restoreConfirmed(): Observable<*> = confirmRestoreSubject

    override fun stopRestoreClicks(): Observable<*> = binding.progressCancel.clicks()

    override fun stopRestoreConfirmed(): Observable<*> = stopRestoreSubject

    override fun fabClicks(): Observable<*> = binding.fab.clicks()

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
    }

    override fun selectFile() = backupFilesDialog.show()

    override fun confirmRestore() = confirmRestoreDialog.show()

    override fun stopRestore() = stopRestoreDialog.show()

}