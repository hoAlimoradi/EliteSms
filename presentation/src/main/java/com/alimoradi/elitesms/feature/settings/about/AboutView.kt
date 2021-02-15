package com.alimoradi.elitesms.feature.settings.about

import com.alimoradi.elitesms.common.base.QkViewContract
import com.alimoradi.elitesms.common.widget.PreferenceView
import io.reactivex.Observable

interface AboutView : QkViewContract<Unit> {

    fun preferenceClicks(): Observable<PreferenceView>

}