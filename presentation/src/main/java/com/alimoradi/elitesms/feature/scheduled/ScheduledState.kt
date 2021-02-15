package com.alimoradi.elitesms.feature.scheduled

import com.alimoradi.domain.model.ScheduledMessage
import io.realm.RealmResults

data class ScheduledState(
    val scheduledMessages: RealmResults<ScheduledMessage>? = null,
    val upgraded: Boolean = false
)
