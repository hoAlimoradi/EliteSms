package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkSeen @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<Unit> {
        return Flowable.just(Unit)
                .doOnNext { messageRepo.markSeen(params) }
    }

}