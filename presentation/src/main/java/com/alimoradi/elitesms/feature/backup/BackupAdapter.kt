package com.alimoradi.elitesms.feature.backup

import android.content.Context
import android.text.format.Formatter
import android.view.ViewGroup
import com.alimoradi.domain.model.BackupFile
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.FlowableAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.DateFormatter
import com.alimoradi.elitesms.databinding.BackupListItemBinding
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class BackupAdapter @Inject constructor(
    private val context: Context,
    private val dateFormatter: DateFormatter
) : FlowableAdapter<BackupFile, BackupListItemBinding>() {

    val backupSelected: Subject<BackupFile> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<BackupListItemBinding> {
        return QkViewHolder(parent, BackupListItemBinding::inflate).apply {
            binding.root.setOnClickListener { backupSelected.onNext(getItem(adapterPosition)) }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<BackupListItemBinding>, position: Int) {
        val backup = getItem(position)
        val count = backup.messages

        holder.binding.title.text = dateFormatter.getDetailedTimestamp(backup.date)
        holder.binding.messages.text = context.resources.getQuantityString(R.plurals.backup_message_count, count, count)
        holder.binding.size.text = Formatter.formatFileSize(context, backup.size)
    }

}