package com.alimoradi.elitesms.injection

import com.alimoradi.elitesms.common.QKApplication
import com.alimoradi.elitesms.common.QkDialog
import com.alimoradi.elitesms.common.util.QkChooserTargetService
import com.alimoradi.elitesms.common.widget.AvatarView
import com.alimoradi.elitesms.common.widget.PagerTitleView
import com.alimoradi.elitesms.common.widget.PreferenceView
import com.alimoradi.elitesms.common.widget.QkEditText
import com.alimoradi.elitesms.common.widget.QkSwitch
import com.alimoradi.elitesms.common.widget.QkTextView
import com.alimoradi.elitesms.common.widget.RadioPreferenceView
import com.alimoradi.elitesms.feature.backup.BackupController
import com.alimoradi.elitesms.feature.blocking.BlockingController
import com.alimoradi.elitesms.feature.blocking.manager.BlockingManagerController
import com.alimoradi.elitesms.feature.blocking.messages.BlockedMessagesController
import com.alimoradi.elitesms.feature.blocking.numbers.BlockedNumbersController
import com.alimoradi.elitesms.feature.compose.editing.DetailedChipView
import com.alimoradi.elitesms.feature.conversationinfo.injection.ConversationInfoComponent
import com.alimoradi.elitesms.feature.settings.SettingsController
import com.alimoradi.elitesms.feature.settings.about.AboutController
import com.alimoradi.elitesms.feature.settings.swipe.SwipeActionsController
import com.alimoradi.elitesms.feature.themepicker.injection.ThemePickerComponent
import com.alimoradi.elitesms.feature.widget.WidgetAdapter
import com.alimoradi.elitesms.injection.android.ActivityBuilderModule
import com.alimoradi.elitesms.injection.android.BroadcastReceiverBuilderModule
import com.alimoradi.elitesms.injection.android.ServiceBuilderModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    BroadcastReceiverBuilderModule::class,
    ServiceBuilderModule::class])
interface AppComponent {

    fun conversationInfoBuilder(): ConversationInfoComponent.Builder
    fun themePickerBuilder(): ThemePickerComponent.Builder

    fun inject(application: QKApplication)

    fun inject(controller: AboutController)
    fun inject(controller: BackupController)
    fun inject(controller: BlockedMessagesController)
    fun inject(controller: BlockedNumbersController)
    fun inject(controller: BlockingController)
    fun inject(controller: BlockingManagerController)
    fun inject(controller: SettingsController)
    fun inject(controller: SwipeActionsController)

    fun inject(dialog: QkDialog)

    fun inject(service: WidgetAdapter)

    /**
     * This can't use AndroidInjection, or else it will crash on pre-marshmallow devices
     */
    fun inject(service: QkChooserTargetService)

    fun inject(view: AvatarView)
    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: RadioPreferenceView)
    fun inject(view: QkEditText)
    fun inject(view: QkSwitch)
    fun inject(view: QkTextView)

}
