package com.alimoradi.elitesms.injection.android

import com.alimoradi.elitesms.feature.backup.BackupActivity
import com.alimoradi.elitesms.feature.blocking.BlockingActivity
import com.alimoradi.elitesms.feature.compose.ComposeActivity
import com.alimoradi.elitesms.feature.compose.ComposeActivityModule
import com.alimoradi.elitesms.feature.contacts.ContactsActivity
import com.alimoradi.elitesms.feature.contacts.ContactsActivityModule
import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoActivity
import com.alimoradi.elitesms.feature.gallery.GalleryActivity
import com.alimoradi.elitesms.feature.gallery.GalleryActivityModule
import com.alimoradi.elitesms.feature.main.MainActivity
import com.alimoradi.elitesms.feature.main.MainActivityModule
import com.alimoradi.elitesms.feature.notificationprefs.NotificationPrefsActivity
import com.alimoradi.elitesms.feature.notificationprefs.NotificationPrefsActivityModule
import com.alimoradi.elitesms.feature.plus.PlusActivity
import com.alimoradi.elitesms.feature.plus.PlusActivityModule
import com.alimoradi.elitesms.feature.qkreply.QkReplyActivity
import com.alimoradi.elitesms.feature.qkreply.QkReplyActivityModule
import com.alimoradi.elitesms.feature.scheduled.ScheduledActivity
import com.alimoradi.elitesms.feature.scheduled.ScheduledActivityModule
import com.alimoradi.elitesms.feature.settings.SettingsActivity
import com.alimoradi.elitesms.injection.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PlusActivityModule::class])
    abstract fun bindPlusActivity(): PlusActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBackupActivity(): BackupActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ComposeActivityModule::class])
    abstract fun bindComposeActivity(): ComposeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ContactsActivityModule::class])
    abstract fun bindContactsActivity(): ContactsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindConversationInfoActivity(): ConversationInfoActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [GalleryActivityModule::class])
    abstract fun bindGalleryActivity(): GalleryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [NotificationPrefsActivityModule::class])
    abstract fun bindNotificationPrefsActivity(): NotificationPrefsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [QkReplyActivityModule::class])
    abstract fun bindQkReplyActivity(): QkReplyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ScheduledActivityModule::class])
    abstract fun bindScheduledActivity(): ScheduledActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindSettingsActivity(): SettingsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    abstract fun bindBlockingActivity(): BlockingActivity

}
