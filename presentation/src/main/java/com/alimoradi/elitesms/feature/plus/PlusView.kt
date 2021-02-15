package com.alimoradi.elitesms.feature.plus

import com.alimoradi.elitesms.common.base.QkView
import com.alimoradi.elitesms.common.util.BillingManager
import io.reactivex.Observable

interface PlusView : QkView<PlusState> {

    val upgradeIntent: Observable<Unit>
    val upgradeDonateIntent: Observable<Unit>
    val donateIntent: Observable<*>
    val themeClicks: Observable<*>
    val scheduleClicks: Observable<*>
    val backupClicks: Observable<*>
    val delayedClicks: Observable<*>
    val nightClicks: Observable<*>

    fun initiatePurchaseFlow(billingManager: BillingManager, sku: String)

}