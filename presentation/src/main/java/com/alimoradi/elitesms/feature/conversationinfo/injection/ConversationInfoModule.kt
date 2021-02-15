package com.alimoradi.elitesms.feature.conversationinfo.injection

import com.alimoradi.elitesms.feature.conversationinfo.ConversationInfoController
import com.alimoradi.elitesms.injection.scope.ControllerScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ConversationInfoModule(private val controller: ConversationInfoController) {

    @Provides
    @ControllerScope
    @Named("threadId")
    fun provideThreadId(): Long = controller.threadId

}