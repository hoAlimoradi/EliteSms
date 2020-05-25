package com.alimoradi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alimoradi.domain.interactor.SendScheduledMessage
import com.alimoradi.domain.repository.MessageRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class SendScheduledMessageReceiver : BroadcastReceiver() {

    @Inject lateinit var messageRepo: MessageRepository
    @Inject lateinit var sendScheduledMessage: SendScheduledMessage

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        intent.getLongExtra("id", -1L).takeIf { it >= 0 }?.let { id ->
            val result = goAsync()
            sendScheduledMessage.execute(id) { result.finish() }
        }
    }

}