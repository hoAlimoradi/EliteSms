package com.alimoradi.elitesms.feature.blocking.numbers

import com.alimoradi.domain.model.BlockedNumber
import io.realm.RealmResults

data class BlockedNumbersState(
    val numbers: RealmResults<BlockedNumber>? = null
)
