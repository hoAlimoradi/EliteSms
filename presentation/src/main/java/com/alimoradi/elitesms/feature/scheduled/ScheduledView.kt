package com.alimoradi.elitesms.feature.scheduled

import com.alimoradi.elitesms.common.base.QkView
import io.reactivex.Observable

interface ScheduledView : QkView<ScheduledState> {

    val messageClickIntent: Observable<Long>
    val messageMenuIntent: Observable<Int>
    val composeIntent: Observable<*>
    val upgradeIntent: Observable<*>

    fun showMessageOptions()

}
