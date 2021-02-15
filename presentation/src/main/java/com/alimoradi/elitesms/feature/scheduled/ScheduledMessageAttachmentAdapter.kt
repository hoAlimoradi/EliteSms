package com.alimoradi.elitesms.feature.scheduled

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import com.alimoradi.data.util.GlideApp
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.databinding.ScheduledMessageImageListItemBinding
import javax.inject.Inject

class ScheduledMessageAttachmentAdapter @Inject constructor(
    private val context: Context
) : QkAdapter<Uri, ScheduledMessageImageListItemBinding>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ScheduledMessageImageListItemBinding> {
        return QkViewHolder(parent, ScheduledMessageImageListItemBinding::inflate).apply {
            binding.thumbnail.clipToOutline = true
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ScheduledMessageImageListItemBinding>, position: Int) {
        val attachment = getItem(position)

        GlideApp.with(context).load(attachment).into(holder.binding.thumbnail)
    }

}
