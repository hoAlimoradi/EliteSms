package com.alimoradi.domain.mapper

import android.database.Cursor
import com.alimoradi.domain.model.MmsPart

interface CursorToPart : Mapper<Cursor, MmsPart> {

    fun getPartsCursor(messageId: Long): Cursor?

}