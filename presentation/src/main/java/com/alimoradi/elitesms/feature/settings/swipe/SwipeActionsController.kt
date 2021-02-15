package com.alimoradi.elitesms.feature.settings.swipe

import android.view.View
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.QkDialog
import com.alimoradi.elitesms.common.base.QkController
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.animateLayoutChanges
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.databinding.SwipeActionsControllerBinding
import com.alimoradi.elitesms.injection.appComponent
import com.uber.autodispose.android.autoDisposable
import com.uber.autodispose.android.lifecycle.autoDisposable
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class SwipeActionsController :
    QkController<SwipeActionsView, SwipeActionsState, SwipeActionsPresenter, SwipeActionsControllerBinding>(
            SwipeActionsControllerBinding::inflate), SwipeActionsView {

    @Inject override lateinit var presenter: SwipeActionsPresenter
    @Inject lateinit var actionsDialog: QkDialog
    @Inject lateinit var colors: Colors

    /**
     * Allows us to subscribe to [actionClicks] more than once
     */
    private val actionClicks: Subject<SwipeActionsView.Action> = PublishSubject.create()

    init {
        appComponent.inject(this)

        actionsDialog.adapter.setData(R.array.settings_swipe_actions)
    }

    override fun onViewCreated() {
        colors.theme().let { theme ->
            binding.rightIcon.setBackgroundTint(theme.theme)
            binding.rightIcon.setTint(theme.textPrimary)
            binding.leftIcon.setBackgroundTint(theme.theme)
            binding.leftIcon.setTint(theme.textPrimary)
        }

        binding.right.postDelayed({ binding.right.animateLayoutChanges = true }, 100)
        binding.left.postDelayed({ binding.left.animateLayoutChanges = true }, 100)

        Observable.merge(
                binding.right.clicks().map { SwipeActionsView.Action.RIGHT },
                binding.left.clicks().map { SwipeActionsView.Action.LEFT })
                .autoDisposable(scope())
                .subscribe(actionClicks)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.settings_swipe_actions)
        showBackButton(true)
    }

    override fun actionClicks(): Observable<SwipeActionsView.Action> = actionClicks

    override fun actionSelected(): Observable<Int> = actionsDialog.adapter.menuItemClicks

    override fun showSwipeActions(selected: Int) {
        actionsDialog.adapter.selectedItem = selected
        activity?.let(actionsDialog::show)
    }

    override fun render(state: SwipeActionsState) {
        binding.rightIcon.isVisible = state.rightIcon != 0
        binding.rightIcon.setImageResource(state.rightIcon)
        binding.rightLabel.text = state.rightLabel

        binding.leftIcon.isVisible = state.leftIcon != 0
        binding.leftIcon.setImageResource(state.leftIcon)
        binding.leftLabel.text = state.leftLabel
    }

}