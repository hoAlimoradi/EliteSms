package com.alimoradi.elitesms.feature.notificationprefs

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.QkDialog
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.extensions.animateLayoutChanges
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.common.widget.PreferenceView
import com.alimoradi.elitesms.common.widget.QkSwitch
import com.alimoradi.elitesms.databinding.NotificationPrefsActivityBinding
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class NotificationPrefsActivity : QkThemedActivity(), NotificationPrefsView {

    @Inject lateinit var previewModeDialog: QkDialog
    @Inject lateinit var actionsDialog: QkDialog
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val preferenceClickIntent: Subject<PreferenceView> = PublishSubject.create()
    override val previewModeSelectedIntent by lazy { previewModeDialog.adapter.menuItemClicks }
    override val ringtoneSelectedIntent: Subject<String> = PublishSubject.create()
    override val actionsSelectedIntent by lazy { actionsDialog.adapter.menuItemClicks }

    private val binding by viewBinding(NotificationPrefsActivityBinding::inflate)
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[NotificationPrefsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTitle(R.string.title_notification_prefs)
        showBackButton(true)
        viewModel.bindView(this)

        binding.preferences.postDelayed({ binding.preferences.animateLayoutChanges = true }, 100)

        val hasOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        binding.notificationsO.setVisible(hasOreo)
        binding.notifications.setVisible(!hasOreo)
        binding.vibration.setVisible(!hasOreo)
        binding.ringtone.setVisible(!hasOreo)

        previewModeDialog.setTitle(R.string.settings_notification_previews_title)
        previewModeDialog.adapter.setData(R.array.notification_preview_options)
        actionsDialog.adapter.setData(R.array.notification_actions)

        // Listen to clicks for all of the preferences
        (0 until binding.preferences.childCount)
                .map { index -> binding.preferences.getChildAt(index) }
                .mapNotNull { view -> view as? PreferenceView }
                .map { preference -> preference.clicks().map { preference } }
                .let { Observable.merge(it) }
                .autoDisposable(scope())
                .subscribe(preferenceClickIntent)
    }

    override fun render(state: NotificationPrefsState) {
        if (state.threadId != 0L) {
            title = state.conversationTitle
        }

        binding.notifications.widget<QkSwitch>().isChecked = state.notificationsEnabled
        binding.previews.summary = state.previewSummary
        previewModeDialog.adapter.selectedItem = state.previewId
        binding.wake.widget<QkSwitch>().isChecked = state.wakeEnabled
        binding.vibration.widget<QkSwitch>().isChecked = state.vibrationEnabled
        binding.ringtone.summary = state.ringtoneName

        binding.actionsDivider.isVisible = state.threadId == 0L
        binding.actionsTitle.isVisible = state.threadId == 0L
        binding.action1.isVisible = state.threadId == 0L
        binding.action1.summary = state.action1Summary
        binding.action2.isVisible = state.threadId == 0L
        binding.action2.summary = state.action2Summary
        binding.action3.isVisible = state.threadId == 0L
        binding.action3.summary = state.action3Summary

        binding.qkreplyDivider.isVisible = state.threadId == 0L
        binding.qkreplyTitle.isVisible = state.threadId == 0L
        binding.qkreply.widget<QkSwitch>().isChecked = state.qkReplyEnabled
        binding.qkreply.isVisible = state.threadId == 0L
        binding.qkreplyTapDismiss.isVisible = state.threadId == 0L
        binding.qkreplyTapDismiss.isEnabled = state.qkReplyEnabled
        binding.qkreplyTapDismiss.widget<QkSwitch>().isChecked = state.qkReplyTapDismiss
    }

    override fun showPreviewModeDialog() = previewModeDialog.show(this)

    override fun showRingtonePicker(default: Uri?) {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, default)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        startActivityForResult(intent, 123)
    }

    override fun showActionDialog(selected: Int) {
        actionsDialog.adapter.selectedItem = selected
        actionsDialog.show(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            ringtoneSelectedIntent.onNext(uri?.toString() ?: "")
        }
    }

}