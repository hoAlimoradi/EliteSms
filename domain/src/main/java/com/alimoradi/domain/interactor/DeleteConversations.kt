package com.alimoradi.domain.interactor

import com.alimoradi.domain.manager.NotificationManager
import com.alimoradi.domain.repository.ConversationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class DeleteConversations @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val notificationManager: NotificationManager,
    private val updateBadge: UpdateBadge
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> conversationRepo.deleteConversations(*threadIds) }
                .doOnNext { threadIds -> threadIds.forEach(notificationManager::update) }
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge
    }

}