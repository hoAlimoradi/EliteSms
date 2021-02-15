package com.alimoradi.elitesms.feature.blocking

import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.QkChangeHandler
import com.alimoradi.elitesms.common.base.QkController
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.animateLayoutChanges
import com.alimoradi.elitesms.common.widget.QkSwitch
import com.alimoradi.elitesms.databinding.BlockingControllerBinding
import com.alimoradi.elitesms.feature.blocking.manager.BlockingManagerController
import com.alimoradi.elitesms.feature.blocking.messages.BlockedMessagesController
import com.alimoradi.elitesms.feature.blocking.numbers.BlockedNumbersController
import com.alimoradi.elitesms.injection.appComponent
import javax.inject.Inject

class BlockingController : QkController<BlockingView, BlockingState, BlockingPresenter, BlockingControllerBinding>(
        BlockingControllerBinding::inflate
), BlockingView {

    override val blockingManagerIntent by lazy { binding.blockingManager.clicks() }
    override val blockedNumbersIntent by lazy { binding.blockedNumbers.clicks() }
    override val blockedMessagesIntent by lazy { binding.blockedMessages.clicks() }
    override val dropClickedIntent by lazy { binding.drop.clicks() }

    @Inject lateinit var colors: Colors
    @Inject override lateinit var presenter: BlockingPresenter

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override fun onViewCreated() {
        super.onViewCreated()
        binding.parent.postDelayed({ binding.parent.animateLayoutChanges = true }, 100)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocking_title)
        showBackButton(true)
    }

    override fun render(state: BlockingState) {
        binding.blockingManager.summary = state.blockingManager
        binding.drop.widget<QkSwitch>().isChecked = state.dropEnabled
        binding.blockedMessages.isEnabled = !state.dropEnabled
    }

    override fun openBlockedNumbers() {
        router.pushController(RouterTransaction.with(BlockedNumbersController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

    override fun openBlockedMessages() {
        router.pushController(RouterTransaction.with(BlockedMessagesController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

    override fun openBlockingManager() {
        router.pushController(RouterTransaction.with(BlockingManagerController())
                .pushChangeHandler(QkChangeHandler())
                .popChangeHandler(QkChangeHandler()))
    }

}
