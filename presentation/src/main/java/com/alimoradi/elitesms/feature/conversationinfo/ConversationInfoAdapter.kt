package com.alimoradi.elitesms.feature.conversationinfo

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.alimoradi.data.extensions.isVideo
import com.alimoradi.data.util.GlideApp
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.databinding.ConversationInfoSettingsBinding
import com.alimoradi.elitesms.databinding.ConversationMediaListItemBinding
import com.alimoradi.elitesms.databinding.ConversationRecipientListItemBinding
import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoItem
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class ConversationInfoAdapter @Inject constructor(
    private val context: Context,
    private val colors: Colors
) : QkAdapter<ConversationInfoItem, ViewBinding>() {

    val recipientClicks: Subject<Long> = PublishSubject.create()
    val recipientLongClicks: Subject<Long> = PublishSubject.create()
    val themeClicks: Subject<Long> = PublishSubject.create()
    val nameClicks: Subject<Unit> = PublishSubject.create()
    val notificationClicks: Subject<Unit> = PublishSubject.create()
    val archiveClicks: Subject<Unit> = PublishSubject.create()
    val blockClicks: Subject<Unit> = PublishSubject.create()
    val deleteClicks: Subject<Unit> = PublishSubject.create()
    val mediaClicks: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ViewBinding> {
        val holder: QkViewHolder<ViewBinding> = when (viewType) {
            0 -> QkViewHolder(parent, ConversationRecipientListItemBinding::inflate)
            1 -> QkViewHolder(parent, ConversationInfoSettingsBinding::inflate)
            2 -> QkViewHolder(parent, ConversationMediaListItemBinding::inflate)
            else -> throw IllegalStateException()
        }

        return holder.apply {
            when (binding) {
                is ConversationRecipientListItemBinding -> {
                    itemView.setOnClickListener {
                        val item = getItem(adapterPosition) as? ConversationInfoItem.ConversationInfoRecipient
                        item?.value?.id?.run(recipientClicks::onNext)
                    }

                    itemView.setOnLongClickListener {
                        val item = getItem(adapterPosition) as? ConversationInfoItem.ConversationInfoRecipient
                        item?.value?.id?.run(recipientLongClicks::onNext)
                        true
                    }

                    binding.theme.setOnClickListener {
                        val item = getItem(adapterPosition) as? ConversationInfoItem.ConversationInfoRecipient
                        item?.value?.id?.run(themeClicks::onNext)
                    }
                }

                is ConversationInfoSettingsBinding -> {
                    binding.groupName.clicks().subscribe(nameClicks)
                    binding.notifications.clicks().subscribe(notificationClicks)
                    binding.archive.clicks().subscribe(archiveClicks)
                    binding.block.clicks().subscribe(blockClicks)
                    binding.delete.clicks().subscribe(deleteClicks)
                }

                is ConversationMediaListItemBinding -> {
                    itemView.setOnClickListener {
                        val item = getItem(adapterPosition) as? ConversationInfoItem.ConversationInfoMedia
                        item?.value?.id?.run(mediaClicks::onNext)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ViewBinding>, position: Int) {
        val item = getItem(position)
        when {
            item is ConversationInfoItem.ConversationInfoRecipient && holder.binding is ConversationRecipientListItemBinding -> {
                val recipient = item.value
                holder.binding.avatar.setRecipient(recipient)

                holder.binding.name.text = recipient.contact?.name ?: recipient.address

                holder.binding.address.text = recipient.address
                holder.binding.address.setVisible(recipient.contact != null)

                holder.binding.add.setVisible(recipient.contact == null)

                val theme = colors.theme(recipient)
                holder.binding.theme.setTint(theme.theme)
            }

            item is ConversationInfoItem.ConversationInfoSettings && holder.binding is ConversationInfoSettingsBinding -> {
                holder.binding.groupName.isVisible = item.recipients.size > 1
                holder.binding.groupName.summary = item.name

                holder.binding.notifications.isEnabled = !item.blocked

                holder.binding.archive.isEnabled = !item.blocked
                holder.binding.archive.title = context.getString(when (item.archived) {
                    true -> R.string.info_unarchive
                    false -> R.string.info_archive
                })

                holder.binding.block.title = context.getString(when (item.blocked) {
                    true -> R.string.info_unblock
                    false -> R.string.info_block
                })
            }

            item is ConversationInfoItem.ConversationInfoMedia && holder.binding is ConversationMediaListItemBinding -> {
                val part = item.value

                GlideApp.with(context)
                        .load(part.getUri())
                        .fitCenter()
                        .into(holder.binding.thumbnail)

                holder.binding.video.isVisible = part.isVideo()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ConversationInfoItem.ConversationInfoRecipient -> 0
            is ConversationInfoItem.ConversationInfoSettings -> 1
            is ConversationInfoItem.ConversationInfoMedia -> 2
        }
    }

    override fun areItemsTheSame(old: ConversationInfoItem, new: ConversationInfoItem): Boolean {
        return when {
            old is ConversationInfoItem.ConversationInfoRecipient && new is ConversationInfoItem.ConversationInfoRecipient -> {
               old.value.id == new.value.id
            }

            old is ConversationInfoItem.ConversationInfoSettings && new is ConversationInfoItem.ConversationInfoSettings -> {
                true
            }

            old is ConversationInfoItem.ConversationInfoMedia && new is ConversationInfoItem.ConversationInfoMedia -> {
                old.value.id == new.value.id
            }

            else -> false
        }
    }

}
