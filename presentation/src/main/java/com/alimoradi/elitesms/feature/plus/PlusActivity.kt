package com.alimoradi.elitesms.feature.plus

import android.graphics.Typeface
import android.os.Bundle
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alimoradi.common.utils.resolveThemeColor
import com.jakewharton.rxbinding2.view.clicks
import com.alimoradi.elitesms.BuildConfig
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkThemedActivity
import com.alimoradi.elitesms.common.util.BillingManager
import com.alimoradi.elitesms.common.util.FontProvider
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.common.widget.PreferenceView
import com.alimoradi.elitesms.databinding.QksmsPlusActivityBinding
import com.alimoradi.elitesms.feature.plus.experiment.UpgradeButtonExperiment
import dagger.android.AndroidInjection
import javax.inject.Inject

class PlusActivity : QkThemedActivity(), PlusView {

    @Inject lateinit var fontProvider: FontProvider
    @Inject lateinit var upgradeButtonExperiment: UpgradeButtonExperiment
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val binding by viewBinding(QksmsPlusActivityBinding::inflate)
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[PlusViewModel::class.java] }

    override val upgradeIntent by lazy { binding.upgrade.clicks() }
    override val upgradeDonateIntent by lazy { binding.upgradeDonate.clicks() }
    override val donateIntent by lazy { binding.donate.clicks() }
    override val themeClicks by lazy { binding.themes.clicks() }
    override val scheduleClicks by lazy { binding.schedule.clicks() }
    override val backupClicks by lazy { binding.backup.clicks() }
    override val delayedClicks by lazy { binding.delayed.clicks() }
    override val nightClicks by lazy { binding.night.clicks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTitle(R.string.title_qksms_plus)
        showBackButton(true)
        viewModel.bindView(this)

        binding.free.setVisible(false)

        if (!prefs.systemFont.get()) {
            fontProvider.getLato { lato ->
                val typeface = Typeface.create(lato, Typeface.BOLD)
                binding.appBarLayout.collapsingToolbar.setCollapsedTitleTypeface(typeface)
                binding.appBarLayout.collapsingToolbar.setExpandedTitleTypeface(typeface)
            }
        }

        // Make the list titles bold
        binding.linearLayout.children
                .mapNotNull { view -> view as? PreferenceView }
                .map { preferenceView -> preferenceView.binding.titleView }
                .forEach { it.setTypeface(it.typeface, Typeface.BOLD) }

        val textPrimary = resolveThemeColor(android.R.attr.textColorPrimary)
        binding.appBarLayout.collapsingToolbar.setCollapsedTitleTextColor(textPrimary)
        binding.appBarLayout.collapsingToolbar.setExpandedTitleColor(textPrimary)

        val theme = colors.theme().theme
        binding.donate.setBackgroundTint(theme)
        binding.upgrade.setBackgroundTint(theme)
        binding.thanksIcon.setTint(theme)
    }

    override fun render(state: PlusState) {
        binding.description.text = getString(R.string.qksms_plus_description_summary, state.upgradePrice)
        binding.upgrade.text = getString(upgradeButtonExperiment.variant, state.upgradePrice, state.currency)
        binding.upgradeDonate.text = getString(R.string.qksms_plus_upgrade_donate, state.upgradeDonatePrice, state.currency)

        val fdroid = BuildConfig.FLAVOR == "noAnalytics"

        binding.free.setVisible(fdroid)
        binding.toUpgrade.setVisible(!fdroid && !state.upgraded)
        binding.upgraded.setVisible(!fdroid && state.upgraded)

        binding.themes.isEnabled = state.upgraded
        binding.schedule.isEnabled = state.upgraded
        binding.backup.isEnabled = state.upgraded
        binding.delayed.isEnabled = state.upgraded
        binding.night.isEnabled = state.upgraded
    }

    override fun initiatePurchaseFlow(billingManager: BillingManager, sku: String) {
        billingManager.initiatePurchaseFlow(this, sku)
    }

}