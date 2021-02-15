package com.alimoradi.elitesms.feature.blocking.messages

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.domain.model.Conversation
import com.alimoradi.domain.util.Preferences
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkRealmAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.DateFormatter
import com.alimoradi.elitesms.databinding.BlockedListItemBinding
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class BlockedMessagesAdapter @Inject constructor(
    private val context: Context,
    private val dateFormatter: DateFormatter
) : QkRealmAdapter<Conversation, BlockedListItemBinding>() {

    val clicks: PublishSubject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<BlockedListItemBinding> {
        return QkViewHolder(parent, BlockedListItemBinding::inflate).apply {
            if (viewType == 0) {
                binding.title.setTypeface(binding.title.typeface, Typeface.BOLD)
                binding.date.setTypeface(binding.date.typeface, Typeface.BOLD)
                binding.date.setTextColor(parent.context.resolveThemeColor(android.R.attr.textColorPrimary))
            }

            binding.root.setOnClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnClickListener
                when (toggleSelection(conversation.id, false)) {
                    true -> binding.root.isActivated = isSelected(conversation.id)
                    false -> clicks.onNext(conversation.id)
                }
            }

            binding.root.setOnLongClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnLongClickListener true
                toggleSelection(conversation.id)
                binding.root.isActivated = isSelected(conversation.id)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<BlockedListItemBinding>, position: Int) {
        val conversation = getItem(position) ?: return

        holder.binding.root.isActivated = isSelected(conversation.id)

        holder.binding.avatars.recipients = conversation.recipients
        holder.binding.title.collapseEnabled = conversation.recipients.size > 1
        holder.binding.title.text = conversation.getTitle()
        holder.binding.date.text = dateFormatter.getConversationTimestamp(conversation.date)

        holder.binding.blocker.text = when (conversation.blockingClient) {
            Preferences.BLOCKING_MANAGER_CC -> context.getString(R.string.blocking_manager_call_control_title)
            Preferences.BLOCKING_MANAGER_SIA -> context.getString(R.string.blocking_manager_sia_title)
            else -> null
        }

        holder.binding.reason.text = conversation.blockReason
        holder.binding.blocker.isVisible = holder.binding.blocker.text.isNotEmpty()
        holder.binding.reason.isVisible = holder.binding.blocker.text.isNotEmpty()
    }

    override fun getItemViewType(position: Int): Int {
        val conversation = getItem(position)
        return if (conversation?.unread == false) 1 else 0
    }

}
