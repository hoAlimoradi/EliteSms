package com.alimoradi.elitesms.injection.android

import com.alimoradi.elitesms.feature.backup.RestoreBackupService
import com.alimoradi.elitesms.injection.scope.ActivityScope
import com.alimoradi.data.service.HeadlessSmsSendService
import com.alimoradi.data.receiver.SendSmsReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindHeadlessSmsSendService(): HeadlessSmsSendService

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindRestoreBackupService(): RestoreBackupService

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindSendSmsReceiver(): SendSmsReceiver

}