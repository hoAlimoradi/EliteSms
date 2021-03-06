package com.alimoradi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alimoradi.domain.interactor.MarkArchived
import dagger.android.AndroidInjection
import javax.inject.Inject

class MarkArchivedReceiver : BroadcastReceiver() {

    @Inject lateinit var markArchived: MarkArchived

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        val pendingResult = goAsync()
        val threadId = intent.getLongExtra("threadId", 0)
        markArchived.execute(listOf(threadId)) { pendingResult.finish() }
    }

}