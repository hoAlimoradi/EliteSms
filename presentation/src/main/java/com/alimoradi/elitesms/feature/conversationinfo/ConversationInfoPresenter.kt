package com.alimoradi.elitesms.feature.conversationinfo

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.alimoradi.common.utils.makeToast
import com.alimoradi.data.extensions.asObservable
import com.alimoradi.domain.extensions.mapNotNull
import com.alimoradi.domain.interactor.DeleteConversations
import com.alimoradi.domain.interactor.MarkArchived
import com.alimoradi.domain.interactor.MarkUnarchived
import com.alimoradi.domain.manager.PermissionManager
import com.alimoradi.domain.model.Conversation
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.domain.repository.MessageRepository
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.Navigator
import com.alimoradi.elitesms.common.base.QkPresenter
import com.alimoradi.elitesms.common.util.ClipboardUtils
import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoItem.ConversationInfoMedia
import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoItem.ConversationInfoRecipient
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Named

class ConversationInfoPresenter @Inject constructor(
    @Named("threadId") threadId: Long,
    messageRepo: MessageRepository,
    private val context: Context,
    private val conversationRepo: ConversationRepository,
    private val deleteConversations: DeleteConversations,
    private val markArchived: MarkArchived,
    private val markUnarchived: MarkUnarchived,
    private val navigator: Navigator,
    private val permissionManager: PermissionManager
) : QkPresenter<ConversationInfoView, ConversationInfoState>(
        ConversationInfoState(threadId = threadId)
) {

    private val conversation: Subject<Conversation> = BehaviorSubject.create()

    init {
        disposables += conversationRepo.getConversationAsync(threadId)
                .asObservable()
                .filter { conversation -> conversation.isLoaded }
                .doOnNext { conversation ->
                    if (!conversation.isValid) {
                        newState { copy(hasError = true) }
                    }
                }
                .filter { conversation -> conversation.isValid }
                .filter { conversation -> conversation.id != 0L }
                .subscribe(conversation::onNext)

        disposables += markArchived
        disposables += markUnarchived
        disposables += deleteConversations

        disposables += Observables
                .combineLatest(
                        conversation,
                        messageRepo.getPartsForConversation(threadId).asObservable()
                ) { conversation, parts ->
                    val data = mutableListOf<ConversationInfoItem>()

                    // If some data was deleted, this isn't the place to handle it
                    if (!conversation.isLoaded || !conversation.isValid || !parts.isLoaded || !parts.isValid) {
                        return@combineLatest
                    }

                    data += conversation.recipients.map(::ConversationInfoRecipient)
                    data += ConversationInfoItem.ConversationInfoSettings(
                            name = conversation.name,
                            recipients = conversation.recipients,
                            archived = conversation.archived,
                            blocked = conversation.blocked)
                    data += parts.map(::ConversationInfoMedia)

                    newState { copy(data = data) }
                }
                .subscribe()
    }

    override fun bindIntents(view: ConversationInfoView) {
        super.bindIntents(view)

        // Add or display the contact
        view.recipientClicks()
                .mapNotNull(conversationRepo::getRecipient)
                .doOnNext { recipient ->
                    recipient.contact?.lookupKey?.let(navigator::showContact)
                            ?: navigator.addContact(recipient.address)
                }
                .autoDisposable(view.scope(Lifecycle.Event.ON_DESTROY)) // ... this should be the default
                .subscribe()

        // Copy phone number
        view.recipientLongClicks()
                .mapNotNull(conversationRepo::getRecipient)
                .map { recipient -> recipient.address }
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(view.scope())
                .subscribe { address ->
                    ClipboardUtils.copy(context, address)
                    context.makeToast(R.string.info_copied_address)
                }

        // Show the theme settings for the conversation
        view.themeClicks()
                .autoDisposable(view.scope())
                .subscribe(view::showThemePicker)

        // Show the conversation title dialog
        view.nameClicks()
                .withLatestFrom(conversation) { _, conversation -> conversation }
                .map { conversation -> conversation.name }
                .autoDisposable(view.scope())
                .subscribe(view::showNameDialog)

        // Set the conversation title
        view.nameChanges()
                .withLatestFrom(conversation) { name, conversation ->
                    conversationRepo.setConversationName(conversation.id, name)
                }
                .autoDisposable(view.scope())
                .subscribe()

        // Show the notifications settings for the conversation
        view.notificationClicks()
                .withLatestFrom(conversation) { _, conversation -> conversation }
                .autoDisposable(view.scope())
                .subscribe { conversation -> navigator.showNotificationSettings(conversation.id) }

        // Toggle the archived state of the conversation
        view.archiveClicks()
                .withLatestFrom(conversation) { _, conversation -> conversation }
                .autoDisposable(view.scope())
                .subscribe { conversation ->
                    when (conversation.archived) {
                        true -> markUnarchived.execute(listOf(conversation.id))
                        false -> markArchived.execute(listOf(conversation.id))
                    }
                }

        // Toggle the blocked state of the conversation
        view.blockClicks()
                .withLatestFrom(conversation) { _, conversation -> conversation }
                .autoDisposable(view.scope())
                .subscribe { conversation -> view.showBlockingDialog(listOf(conversation.id), !conversation.blocked) }

        // Show the delete confirmation dialog
        view.deleteClicks()
                .filter { permissionManager.isDefaultSms().also { if (!it) view.requestDefaultSms() } }
                .autoDisposable(view.scope())
                .subscribe { view.showDeleteDialog() }

        // Delete the conversation
        view.confirmDelete()
                .withLatestFrom(conversation) { _, conversation -> conversation }
                .autoDisposable(view.scope())
                .subscribe { conversation -> deleteConversations.execute(listOf(conversation.id)) }

        // Media
        view.mediaClicks()
                .autoDisposable(view.scope())
                .subscribe(navigator::showMedia)
    }

}