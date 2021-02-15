package com.alimoradi.elitesms.feature.scheduled

import android.content.Context
import com.alimoradi.common.utils.makeToast
import com.alimoradi.domain.interactor.SendScheduledMessage
import com.alimoradi.domain.repository.MessageRepository
import com.alimoradi.domain.repository.ScheduledMessageRepository
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.Navigator
import com.alimoradi.elitesms.common.base.QkViewModel
import com.alimoradi.elitesms.common.util.BillingManager
import com.alimoradi.elitesms.common.util.ClipboardUtils
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class ScheduledViewModel @Inject constructor(
    billingManager: BillingManager,
    private val context: Context,
    private val messageRepo: MessageRepository,
    private val navigator: Navigator,
    private val scheduledMessageRepo: ScheduledMessageRepository,
    private val sendScheduledMessage: SendScheduledMessage
) : QkViewModel<ScheduledView, ScheduledState>(ScheduledState(
        scheduledMessages = scheduledMessageRepo.getScheduledMessages()
)) {

    init {
        disposables += billingManager.upgradeStatus
                .subscribe { upgraded -> newState { copy(upgraded = upgraded) } }
    }

    override fun bindView(view: ScheduledView) {
        super.bindView(view)

        view.messageClickIntent
                .autoDisposable(view.scope())
                .subscribe { view.showMessageOptions() }

        view.messageMenuIntent
                .withLatestFrom(view.messageClickIntent) { itemId, messageId ->
                    when (itemId) {
                        0 -> sendScheduledMessage.execute(messageId)
                        1 -> scheduledMessageRepo.getScheduledMessage(messageId)?.let { message ->
                            ClipboardUtils.copy(context, message.body)
                            context.makeToast(R.string.toast_copied)
                        }
                        2 -> scheduledMessageRepo.deleteScheduledMessage(messageId)
                    }
                    Unit
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.composeIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showCompose() }

        view.upgradeIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showQksmsPlusActivity("schedule_fab") }
    }

}