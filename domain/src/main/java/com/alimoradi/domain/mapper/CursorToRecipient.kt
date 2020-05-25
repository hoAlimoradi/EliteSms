package com.alimoradi.domain.mapper

import android.database.Cursor
import com.alimoradi.domain.model.Recipient

interface CursorToRecipient : Mapper<Cursor, Recipient> {

    fun getRecipientCursor(): Cursor?

    fun getRecipientCursor(id: Long): Cursor?

}