package com.alimoradi.elitesms.injection

import com.alimoradi.elitesms.common.QKApplication

internal lateinit var appComponent: AppComponent
    private set

internal object AppComponentManager {

    fun init(application: QKApplication) {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .build()
    }

}