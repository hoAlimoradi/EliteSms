package com.alimoradi.elitesms.feature.conversationinfo.injection

import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoController
import com.alimoradi.elitesms.injection.scope.ControllerScope
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ConversationInfoModule::class])
interface ConversationInfoComponent {

    fun inject(controller: ConversationInfoController)

    @Subcomponent.Builder
    interface Builder {
        fun conversationInfoModule(module: ConversationInfoModule): Builder
        fun build(): ConversationInfoComponent
    }

}