package com.alimoradi.elitesms.feature.settings.swipe

import com.alimoradi.elitesms.common.base.QkViewContract
import io.reactivex.Observable

interface SwipeActionsView : QkViewContract<SwipeActionsState> {

    enum class Action { LEFT, RIGHT }

    fun actionClicks(): Observable<Action>
    fun actionSelected(): Observable<Int>

    fun showSwipeActions(selected: Int)

}