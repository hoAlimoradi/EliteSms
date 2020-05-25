package com.alimoradi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.alimoradi.data.compat.SubscriptionManagerCompat
import com.alimoradi.domain.interactor.MarkRead
import com.alimoradi.domain.interactor.SendMessage
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.domain.repository.MessageRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class RemoteMessagingReceiver : BroadcastReceiver() {

    @Inject lateinit var conversationRepo: ConversationRepository
    @Inject lateinit var markRead: MarkRead
    @Inject lateinit var messageRepo: MessageRepository
    @Inject lateinit var sendMessage: SendMessage
    @Inject lateinit var subscriptionManager: SubscriptionManagerCompat

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return
        val bundle = intent.extras ?: return

        val threadId = bundle.getLong("threadId")
        val body = remoteInput.getCharSequence("body").toString()
        markRead.execute(listOf(threadId))

        val lastMessage = messageRepo.getMessages(threadId).lastOrNull()
        val subId = subscriptionManager.activeSubscriptionInfoList
                .firstOrNull { it.subscriptionId == lastMessage?.subId }
                ?.subscriptionId ?: -1
        val addresses = conversationRepo.getConversation(threadId)?.recipients?.map { it.address } ?: return

        val pendingRepository = goAsync()
        sendMessage.execute(SendMessage.Params(subId, threadId, addresses, body)) { pendingRepository.finish() }
    }
}
