package com.alimoradi.data.receiver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.alimoradi.smsmms.com.klinker.android.send_message.MmsReceivedReceiver
import com.alimoradi.domain.interactor.ReceiveMms
import dagger.android.AndroidInjection
import javax.inject.Inject

class MmsReceivedReceiver : MmsReceivedReceiver() {

    @Inject lateinit var receiveMms: ReceiveMms

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)
    }

    override fun onMessageReceived(messageUri: Uri?) {
        messageUri?.let { uri ->
            val pendingResult = goAsync()
            receiveMms.execute(uri) { pendingResult.finish() }
        }
    }

}
