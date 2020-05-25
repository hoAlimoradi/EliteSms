package com.alimoradi.domain.interactor

import com.alimoradi.domain.manager.ShortcutManager
import com.alimoradi.domain.repository.ConversationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkUnpinned @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val updateBadge: UpdateBadge,
    private val shortcutManager: ShortcutManager
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> conversationRepo.markUnpinned(*threadIds) }
                .doOnNext { shortcutManager.updateShortcuts() } // Update shortcuts
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge and widget
    }

}
