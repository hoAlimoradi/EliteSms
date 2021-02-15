package com.alimoradi.elitesms.feature.themepicker.injection

import com.alimoradi.elitesms.feature.themepicker.ThemePickerController
import com.alimoradi.elitesms.injection.scope.ControllerScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ThemePickerModule(private val controller: ThemePickerController) {

    @Provides
    @ControllerScope
    @Named("recipientId")
    fun provideThreadId(): Long = controller.recipientId

}