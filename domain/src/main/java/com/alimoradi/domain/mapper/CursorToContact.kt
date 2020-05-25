package com.alimoradi.domain.mapper

import android.database.Cursor
import com.alimoradi.domain.model.Contact

interface CursorToContact : Mapper<Cursor, Contact> {

    fun getContactsCursor(): Cursor?

}