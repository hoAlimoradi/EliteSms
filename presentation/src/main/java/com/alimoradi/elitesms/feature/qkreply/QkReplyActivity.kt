package com.alimoradi.elitesms.feature.qkreply

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alimoradi.common.utils.resolveThemeColor
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.extensions.autoScrollToStart
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.QkreplyActivityBinding
import com.alimoradi.elitesms.feature.compose.MessagesAdapter
import dagger.android.AndroidInjection
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class QkReplyActivity : QkThemedActivity(), QkReplyView {

    @Inject lateinit var adapter: MessagesAdapter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val menuItemIntent: Subject<Int> = PublishSubject.create()
    override val textChangedIntent by lazy { binding.message.textChanges() }
    override val changeSimIntent by lazy { binding.sim.clicks() }
    override val sendIntent by lazy { binding.send.clicks() }

    private val binding by viewBinding(QkreplyActivityBinding::inflate)
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[QkReplyViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setFinishOnTouchOutside(prefs.qkreplyTapDismiss.get())
        setContentView(binding.root)
        window.setBackgroundDrawable(null)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        viewModel.bindView(this)

        binding.toolbar.clipToOutline = true

        binding.messages.adapter = adapter
        binding.messages.adapter?.autoScrollToStart(binding.messages)
        binding.messages.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = binding.messages.scrollToPosition(adapter.itemCount - 1)
        })

        // These theme attributes don't apply themselves on API 21
        if (Build.VERSION.SDK_INT <= 22) {
            binding.toolbar.setBackgroundTint(resolveThemeColor(R.attr.colorPrimary))
            binding.background.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            binding.messageBackground.setBackgroundTint(resolveThemeColor(R.attr.bubbleColor))
            binding.composeBackgroundGradient.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            binding.composeBackgroundSolid.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
        }
    }

    override fun render(state: QkReplyState) {
        if (state.hasError) {
            finish()
        }

        threadId.onNext(state.threadId)

        title = state.title

        binding.toolbar.menu.findItem(R.id.expand)?.isVisible = !state.expanded
        binding.toolbar.menu.findItem(R.id.collapse)?.isVisible = state.expanded

        adapter.data = state.data

        binding.counter.text = state.remaining
        binding.counter.setVisible(binding.counter.text.isNotBlank())

        binding.sim.setVisible(state.subscription != null)
        binding.sim.contentDescription = getString(R.string.compose_sim_cd, state.subscription?.displayName)
        binding.simIndex.text = "${state.subscription?.simSlotIndex?.plus(1)}"

        binding.send.isEnabled = state.canSend
        binding.send.imageAlpha = if (state.canSend) 255 else 128
    }

    override fun setDraft(draft: String) {
        binding.message.setText(draft)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qkreply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuItemIntent.onNext(item.itemId)
        return true
    }

    override fun getActivityThemeRes(black: Boolean) = when {
        black -> R.style.AppThemeDialog_Black
        else -> R.style.AppThemeDialog
    }

}