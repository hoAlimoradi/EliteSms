package com.alimoradi.elitesms.feature.changelog

import androidx.appcompat.app.AlertDialog
import com.alimoradi.domain.manager.ChangelogManager
import com.alimoradi.elitesms.BuildConfig
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.databinding.ChangelogDialogBinding
import com.alimoradi.elitesms.feature.main.MainActivity
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class ChangelogDialog(activity: MainActivity) {

    val moreClicks: Subject<Unit> = PublishSubject.create()

    private val dialog: AlertDialog
    private val adapter = ChangelogAdapter(activity)

    init {
        val binding = ChangelogDialogBinding.inflate(activity.layoutInflater)

        dialog = AlertDialog.Builder(activity)
                .setCancelable(true)
                .setView(binding.root)
                .create()

        binding.version.text = activity.getString(R.string.changelog_version, BuildConfig.VERSION_NAME)
        binding.changelog.adapter = adapter
        binding.more.setOnClickListener { dialog.dismiss(); moreClicks.onNext(Unit) }
        binding.dismiss.setOnClickListener { dialog.dismiss() }
    }

    fun show(changelog: ChangelogManager.Changelog) {
        adapter.setChangelog(changelog)
        dialog.show()
    }

}
