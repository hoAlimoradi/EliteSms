package com.alimoradi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alimoradi.domain.interactor.UpdateScheduledMessageAlarms
import dagger.android.AndroidInjection
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var updateScheduledMessageAlarms: UpdateScheduledMessageAlarms

    override fun onReceive(context: Context, intent: Intent?) {
        AndroidInjection.inject(this, context)

        val result = goAsync()
        updateScheduledMessageAlarms.execute(Unit) { result.finish() }
    }

}