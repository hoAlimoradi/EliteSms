package com.alimoradi.domain.mapper

import android.database.Cursor
import com.alimoradi.domain.model.Conversation

interface CursorToConversation : Mapper<Cursor, Conversation> {

    fun getConversationsCursor(): Cursor?

}