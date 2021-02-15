package com.alimoradi.elitesms.feature.plus.experiment

import android.content.Context
import androidx.annotation.StringRes
import com.alimoradi.domain.experiment.Experiment
import com.alimoradi.domain.experiment.Variant
import com.alimoradi.domain.manager.AnalyticsManager
import com.alimoradi.elitesms.R
import javax.inject.Inject

class UpgradeButtonExperiment @Inject constructor(
    context: Context,
    analytics: AnalyticsManager
) : Experiment<@StringRes Int>(context, analytics) {

    override val key: String = "Upgrade Button"

    override val variants: List<Variant<Int>> = listOf(
            Variant("variant_a", R.string.qksms_plus_upgrade),
            Variant("variant_b", R.string.qksms_plus_upgrade_b),
            Variant("variant_c", R.string.qksms_plus_upgrade_c),
            Variant("variant_d", R.string.qksms_plus_upgrade_d))

    override val default: Int = R.string.qksms_plus_upgrade

}