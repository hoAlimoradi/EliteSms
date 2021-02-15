package com.alimoradi.elitesms.feature.blocking.messages

import com.alimoradi.domain.model.Conversation
import io.realm.RealmResults

data class BlockedMessagesState(
    val data: RealmResults<Conversation>? = null,
    val selected: Int = 0
)
