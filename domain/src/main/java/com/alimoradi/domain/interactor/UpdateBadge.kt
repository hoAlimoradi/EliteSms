package com.alimoradi.domain.interactor

import com.alimoradi.domain.manager.ShortcutManager
import com.alimoradi.domain.manager.WidgetManager
import io.reactivex.Flowable
import javax.inject.Inject

class UpdateBadge @Inject constructor(
    private val shortcutManager: ShortcutManager,
    private val widgetManager: WidgetManager
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { shortcutManager.updateBadge() }
                .doOnNext { widgetManager.updateUnreadCount() }
    }

}