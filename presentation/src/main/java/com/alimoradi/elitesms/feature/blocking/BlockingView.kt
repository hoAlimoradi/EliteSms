package com.alimoradi.elitesms.feature.blocking

import com.alimoradi.elitesms.common.base.QkViewContract
import io.reactivex.Observable

interface BlockingView : QkViewContract<BlockingState> {

    val blockingManagerIntent: Observable<*>
    val blockedNumbersIntent: Observable<*>
    val blockedMessagesIntent: Observable<*>
    val dropClickedIntent: Observable<*>

    fun openBlockingManager()
    fun openBlockedNumbers()
    fun openBlockedMessages()
}
