package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class CancelDelayedMessage @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<*> {
        return Flowable.just(params)
                .doOnNext { id -> messageRepo.cancelDelayedSms(id) }
                .doOnNext { id -> messageRepo.deleteMessages(id) }
    }

}