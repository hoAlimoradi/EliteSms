package com.alimoradi.elitesms.feature.settings.swipe

import android.content.Context
import androidx.annotation.DrawableRes
import com.alimoradi.domain.util.Preferences
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkPresenter
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class SwipeActionsPresenter @Inject constructor(
    context: Context,
    private val prefs: Preferences
) : QkPresenter<SwipeActionsView, SwipeActionsState>(SwipeActionsState()) {

    init {
        val actionLabels = context.resources.getStringArray(R.array.settings_swipe_actions)

        disposables += prefs.swipeRight.asObservable()
                .subscribe { action -> newState { copy(rightLabel = actionLabels[action], rightIcon = iconForAction(action)) } }

        disposables += prefs.swipeLeft.asObservable()
                .subscribe { action -> newState { copy(leftLabel = actionLabels[action], leftIcon = iconForAction(action)) } }
    }

    override fun bindIntents(view: SwipeActionsView) {
        super.bindIntents(view)

        view.actionClicks()
                .map { action ->
                    when (action) {
                        SwipeActionsView.Action.RIGHT -> prefs.swipeRight.get()
                        SwipeActionsView.Action.LEFT -> prefs.swipeLeft.get()
                    }
                }
                .autoDisposable(view.scope())
                .subscribe(view::showSwipeActions)

        view.actionSelected()
                .withLatestFrom(view.actionClicks()) { actionId, action ->
                    when (action) {
                        SwipeActionsView.Action.RIGHT -> prefs.swipeRight.set(actionId)
                        SwipeActionsView.Action.LEFT -> prefs.swipeLeft.set(actionId)
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()
    }

    @DrawableRes
    private fun iconForAction(action: Int) = when (action) {
        Preferences.SWIPE_ACTION_ARCHIVE -> R.drawable.ic_archive_white_24dp
        Preferences.SWIPE_ACTION_DELETE -> R.drawable.ic_delete_white_24dp
        Preferences.SWIPE_ACTION_BLOCK -> R.drawable.ic_block_white_24dp
        Preferences.SWIPE_ACTION_CALL -> R.drawable.ic_call_white_24dp
        Preferences.SWIPE_ACTION_READ -> R.drawable.ic_check_white_24dp
        Preferences.SWIPE_ACTION_UNREAD -> R.drawable.ic_markunread_black_24dp
        else -> 0
    }

}