package com.alimoradi.elitesms.feature.scheduled

import androidx.lifecycle.ViewModel
import com.alimoradi.elitesms.injection.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ScheduledActivityModule {

    @Provides
    @IntoMap
    @ViewModelKey(ScheduledViewModel::class)
    fun provideScheduledViewModel(viewModel: ScheduledViewModel): ViewModel = viewModel

}