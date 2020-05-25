package com.alimoradi.domain.interactor

import android.net.Uri
import com.alimoradi.domain.extensions.mapNotNull
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.domain.repository.SyncRepository
import io.reactivex.Flowable
import javax.inject.Inject

class SyncMessage @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val syncManager: SyncRepository,
    private val updateBadge: UpdateBadge
) : Interactor<Uri>() {

    override fun buildObservable(params: Uri): Flowable<*> {
        return Flowable.just(params)
                .mapNotNull { uri -> syncManager.syncMessage(uri) }
                .doOnNext { message -> conversationRepo.updateConversations(message.threadId) }
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge
    }

}