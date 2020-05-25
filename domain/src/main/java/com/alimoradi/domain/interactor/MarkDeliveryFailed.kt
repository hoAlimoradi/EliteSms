package com.alimoradi.domain.interactor

import com.alimoradi.domain.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class MarkDeliveryFailed @Inject constructor(
    private val messageRepo: MessageRepository
) : Interactor<MarkDeliveryFailed.Params>() {

    data class Params(val id: Long, val resultCode: Int)

    override fun buildObservable(params: Params): Flowable<Unit> {
        return Flowable.just(Unit)
                .doOnNext { messageRepo.markDeliveryFailed(params.id, params.resultCode) }
    }

}