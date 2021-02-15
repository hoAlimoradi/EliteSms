package com.alimoradi.elitesms.feature.qkreply

import com.alimoradi.data.compat.SubscriptionInfoCompat
import com.alimoradi.domain.model.Conversation
import com.alimoradi.domain.model.Message
import io.realm.RealmResults

data class QkReplyState(
    val hasError: Boolean = false,
    val threadId: Long = 0,
    val title: String = "",
    val expanded: Boolean = false,
    val data: Pair<Conversation, RealmResults<Message>>? = null,
    val remaining: String = "",
    val subscription: SubscriptionInfoCompat? = null,
    val canSend: Boolean = false
)