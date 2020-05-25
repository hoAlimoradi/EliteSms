package com.alimoradi.domain.mapper

import android.database.Cursor
import com.alimoradi.domain.model.ContactGroup
interface CursorToContactGroup : Mapper<Cursor, ContactGroup> {

    fun getContactGroupsCursor(): Cursor?

}
