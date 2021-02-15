package com.alimoradi.elitesms.feature.compose

import com.alimoradi.data.compat.SubscriptionInfoCompat
import com.alimoradi.domain.model.Attachment
import com.alimoradi.domain.model.Conversation
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.Recipient
import io.realm.RealmResults

data class ComposeState(
    val hasError: Boolean = false,
    val editingMode: Boolean = false,
    val threadId: Long = 0,
    val selectedChips: List<Recipient> = ArrayList(),
    val sendAsGroup: Boolean = true,
    val conversationtitle: String = "",
    val loading: Boolean = false,
    val query: String = "",
    val searchSelectionId: Long = -1,
    val searchSelectionPosition: Int = 0,
    val searchResults: Int = 0,
    val messages: Pair<Conversation, RealmResults<Message>>? = null,
    val selectedMessages: Int = 0,
    val scheduled: Long = 0,
    val attachments: List<Attachment> = ArrayList(),
    val attaching: Boolean = false,
    val remaining: String = "",
    val subscription: SubscriptionInfoCompat? = null,
    val canSend: Boolean = false
)