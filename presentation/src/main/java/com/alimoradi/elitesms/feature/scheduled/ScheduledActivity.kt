package com.alimoradi.elitesms.feature.scheduled

import android.graphics.Typeface
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.QkDialog
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.FontProvider
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.ScheduledActivityBinding
import dagger.android.AndroidInjection
import javax.inject.Inject


class ScheduledActivity : QkThemedActivity(), ScheduledView {

    @Inject lateinit var dialog: QkDialog
    @Inject lateinit var fontProvider: FontProvider
    @Inject lateinit var messageAdapter: ScheduledMessageAdapter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val messageClickIntent by lazy { messageAdapter.clicks }
    override val messageMenuIntent by lazy { dialog.adapter.menuItemClicks }
    override val composeIntent by lazy { binding.compose.clicks() }
    override val upgradeIntent by lazy { binding.upgrade.clicks() }

    private val binding by viewBinding(ScheduledActivityBinding::inflate)
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[ScheduledViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTitle(R.string.scheduled_title)
        showBackButton(true)
        viewModel.bindView(this)

        if (!prefs.systemFont.get()) {
            fontProvider.getLato { lato ->
                val typeface = Typeface.create(lato, Typeface.BOLD)
                binding.appBarLayout.collapsingToolbar.setCollapsedTitleTypeface(typeface)
                binding.appBarLayout.collapsingToolbar.setExpandedTitleTypeface(typeface)
            }
        }

        dialog.title = getString(R.string.scheduled_options_title)
        dialog.adapter.setData(R.array.scheduled_options)

        messageAdapter.emptyView = binding.empty
        binding.messages.adapter = messageAdapter

        colors.theme().let { theme ->
            binding.sampleMessage.setBackgroundTint(theme.theme)
            binding.sampleMessage.setTextColor(theme.textPrimary)
            binding.compose.setTint(theme.textPrimary)
            binding.compose.setBackgroundTint(theme.theme)
            binding.upgrade.setBackgroundTint(theme.theme)
            binding.upgradeIcon.setTint(theme.textPrimary)
            binding.upgradeLabel.setTextColor(theme.textPrimary)
        }
    }

    override fun render(state: ScheduledState) {
        messageAdapter.updateData(state.scheduledMessages)

        binding.compose.isVisible = state.upgraded
        binding.upgrade.isVisible = !state.upgraded
    }

    override fun showMessageOptions() {
        dialog.show(this)
    }

}