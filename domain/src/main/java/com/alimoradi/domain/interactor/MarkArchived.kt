package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.ConversationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkArchived @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val markRead: MarkRead
) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> conversationRepo.markArchived(*threadIds) }
                .flatMap { markRead.buildObservable(params) }
    }

}