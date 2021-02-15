package com.alimoradi.elitesms.feature.conversationinfo

data class ConversationInfoState(
    val threadId: Long = 0,
    val data: List<ConversationInfoItem> = listOf(),
    val hasError: Boolean = false
)
