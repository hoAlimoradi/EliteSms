package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.ConversationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkUnblocked @Inject constructor(private val conversationRepo: ConversationRepository) : Interactor<List<Long>>() {

    override fun buildObservable(params: List<Long>): Flowable<*> {
        return Flowable.just(params.toLongArray())
                .doOnNext { threadIds -> conversationRepo.markUnblocked(*threadIds) }
    }

}