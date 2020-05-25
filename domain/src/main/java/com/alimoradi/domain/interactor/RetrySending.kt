package com.alimoradi.domain.interactor

import com.alimoradi.domain.extensions.mapNotNull
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.repository.MessageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class RetrySending @Inject constructor(private val messageRepo: MessageRepository) : Interactor<Long>() {

    override fun buildObservable(params: Long): Flowable<Message> {
        return Flowable.just(params)
                .doOnNext(messageRepo::markSending)
                .mapNotNull(messageRepo::getMessage)
                .doOnNext { message ->
                    when (message.isSms()) {
                        true -> messageRepo.sendSms(message)
                        false -> messageRepo.resendMms(message)
                    }
                }
    }

}
