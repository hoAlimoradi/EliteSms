package com.alimoradi.elitesms.feature.conversations

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.data.util.PhoneNumberUtils
import com.alimoradi.domain.model.Conversation
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.Navigator
import com.alimoradi.elitesms.common.base.QkRealmAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.DateFormatter
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.databinding.ConversationListItemBinding

import javax.inject.Inject

class ConversationsAdapter @Inject constructor(
    private val colors: Colors,
    private val context: Context,
    private val dateFormatter: DateFormatter,
    private val navigator: Navigator,
    private val phoneNumberUtils: PhoneNumberUtils
) : QkRealmAdapter<Conversation, ConversationListItemBinding>() {

    init {
        // This is how we access the threadId for the swipe actions
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ConversationListItemBinding> {
        return QkViewHolder(parent, ConversationListItemBinding::inflate).apply {
            if (viewType == 1) {
                val textColorPrimary = parent.context.resolveThemeColor(android.R.attr.textColorPrimary)

                binding.title.setTypeface(binding.title.typeface, Typeface.BOLD)

                binding.snippet.setTypeface(binding.snippet.typeface, Typeface.BOLD)
                binding.snippet.setTextColor(textColorPrimary)
                binding.snippet.maxLines = 5

                binding.unread.isVisible = true

                binding.date.setTypeface(binding.date.typeface, Typeface.BOLD)
                binding.date.setTextColor(textColorPrimary)
            }

            binding.root.setOnClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnClickListener
                when (toggleSelection(conversation.id, false)) {
                    true -> binding.root.isActivated = isSelected(conversation.id)
                    false -> navigator.showConversation(conversation.id)
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

    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: QkViewHolder<ConversationListItemBinding>, position: Int) {
        val conversation = getItem(position) ?: return

        // If the last message wasn't incoming, then the colour doesn't really matter anyway
        val lastMessage = conversation.lastMessage
        val recipient = when {
            conversation.recipients.size == 1 || lastMessage == null -> conversation.recipients.firstOrNull()
            else -> conversation.recipients.find { recipient ->
                phoneNumberUtils.compare(recipient.address, lastMessage.address)
            }
        }
        val theme = colors.theme(recipient).theme

        holder.binding.root.isActivated = isSelected(conversation.id)

        holder.binding.avatars.recipients = conversation.recipients
        holder.binding.title.collapseEnabled = conversation.recipients.size > 1
        holder.binding.title.text = buildSpannedString {
            append(conversation.getTitle())
            if (conversation.draft.isNotEmpty()) {
                color(theme) { append(" " + context.getString(R.string.main_draft)) }
            }
        }
        holder.binding.date.text = conversation.date.takeIf { it > 0 }?.let(dateFormatter::getConversationTimestamp)
        holder.binding.snippet.text = when {
            conversation.draft.isNotEmpty() -> conversation.draft
            conversation.me -> context.getString(R.string.main_sender_you, conversation.snippet)
            else -> conversation.snippet
        }
        holder.binding.pinned.isVisible = conversation.pinned
        holder.binding.unread.setTint(theme)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: -1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.unread == false) 0 else 1
    }
}
