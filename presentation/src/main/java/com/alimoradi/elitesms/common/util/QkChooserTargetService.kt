package com.alimoradi.elitesms.common.util

import android.content.ComponentName
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.alimoradi.common.utils.tryOrNull
import com.alimoradi.data.util.GlideApp
import com.alimoradi.domain.model.Conversation
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.feature.compose.ComposeActivity
import com.alimoradi.elitesms.injection.appComponent
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
class QkChooserTargetService : ChooserTargetService() {

    @Inject lateinit var conversationRepo: ConversationRepository

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }

    override fun onGetChooserTargets(targetActivityName: ComponentName?, matchedFilter: IntentFilter?): List<ChooserTarget> {
        return conversationRepo.getTopConversations()
                .take(3)
                .map(this::createShortcutForConversation)
    }

    private fun createShortcutForConversation(conversation: Conversation): ChooserTarget {
        val icon = when (conversation.recipients.size) {
            1 -> {
                val photoUri = conversation.recipients.first()?.contact?.photoUri
                val request = GlideApp.with(this)
                        .asBitmap()
                        .circleCrop()
                        .load(photoUri)
                        .submit()
                val bitmap = tryOrNull(false) { request.get() }

                if (bitmap != null) Icon.createWithBitmap(bitmap)
                else Icon.createWithResource(this, R.mipmap.ic_shortcut_person)
            }

            else -> Icon.createWithResource(this, R.mipmap.ic_shortcut_people)
        }

        val componentName = ComponentName(this, ComposeActivity::class.java)

        return ChooserTarget(conversation.getTitle(), icon, 1f, componentName, bundleOf("threadId" to conversation.id))
    }

}