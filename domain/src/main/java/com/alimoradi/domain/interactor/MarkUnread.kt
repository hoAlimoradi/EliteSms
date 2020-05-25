package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkUnread @Inject constructor(
    private val messageRepo: MessageRepository,
    private val updateBadge: UpdateBadge
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadId -> messageRepo.markUnread(*threadId) }
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge
    }

}