package com.alimoradi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.alimoradi.domain.interactor.SyncMessage
import dagger.android.AndroidInjection
import javax.inject.Inject

class MmsUpdatedReceiver : BroadcastReceiver() {

    companion object {
        const val URI = "uri"
    }

    @Inject lateinit var syncMessage: SyncMessage

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        intent.getStringExtra(URI)?.let { uriString ->
            val pendingResult = goAsync()
            syncMessage.execute(Uri.parse(uriString)) { pendingResult.finish() }
        }
    }

}