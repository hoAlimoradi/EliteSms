package com.alimoradi.elitesms.feature.plus

import androidx.lifecycle.ViewModel
import com.alimoradi.elitesms.injection.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class PlusActivityModule {

    @Provides
    @IntoMap
    @ViewModelKey(PlusViewModel::class)
    fun providePlusViewModel(viewModel: PlusViewModel): ViewModel = viewModel

}