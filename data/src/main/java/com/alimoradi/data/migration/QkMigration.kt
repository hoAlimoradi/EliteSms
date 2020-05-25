package com.alimoradi.data.migration
import android.content.Context
import com.alimoradi.common.utils.versionCode
import com.alimoradi.data.blocking.QksmsBlockingClient
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.domain.util.Preferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class QkMigration @Inject constructor(
    private val context: Context,
    private val conversationRepo: ConversationRepository,
    private val prefs: Preferences,
    private val qksmsBlockingClient: QksmsBlockingClient
) {

    fun performMigration() {
        GlobalScope.launch {
            val oldVersion = prefs.version.get()

            if (oldVersion < 2199) {
                upgradeTo370()
            }

            prefs.version.set(context.versionCode)
        }
    }

    private fun upgradeTo370() {
        // Migrate changelog version
        prefs.changelogVersion.set(prefs.version.get())

        // Migrate from old SIA preference to blocking manager preference
        if (prefs.sia.get()) {
            prefs.blockingManager.set(Preferences.BLOCKING_MANAGER_SIA)
            prefs.sia.delete()
        }

        // Migrate blocked conversations into QK blocking client
        val addresses = conversationRepo.getBlockedConversations()
                .flatMap { conversation -> conversation.recipients }
                .map { recipient -> recipient.address }
                .distinct()

        qksmsBlockingClient.block(addresses).blockingAwait()
    }

}
