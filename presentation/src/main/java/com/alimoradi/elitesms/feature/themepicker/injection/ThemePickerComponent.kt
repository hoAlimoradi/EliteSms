package com.alimoradi.elitesms.feature.themepicker.injection

import com.alimoradi.elitesms.feature.themepicker.ThemePickerController
import com.alimoradi.elitesms.injection.scope.ControllerScope
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ThemePickerModule::class])
interface ThemePickerComponent {

    fun inject(controller: ThemePickerController)

    @Subcomponent.Builder
    interface Builder {
        fun themePickerModule(module: ThemePickerModule): Builder
        fun build(): ThemePickerComponent
    }

}