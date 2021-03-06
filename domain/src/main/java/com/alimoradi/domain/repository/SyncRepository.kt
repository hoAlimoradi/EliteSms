package com.alimoradi.domain.repository

import android.net.Uri
import com.alimoradi.domain.model.Message
import io.reactivex.Observable

interface SyncRepository {

    sealed class SyncProgress {
        object Idle : SyncProgress()
        data class Running(val max: Int, val progress: Int, val indeterminate: Boolean) : SyncProgress()
    }

    val syncProgress: Observable<SyncProgress>

    fun syncMessages()

    fun syncMessage(uri: Uri): Message?

    fun syncContacts()

}
