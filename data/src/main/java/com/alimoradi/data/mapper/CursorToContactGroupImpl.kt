package com.alimoradi.data.mapper

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.alimoradi.domain.mapper.CursorToContactGroup
import com.alimoradi.domain.model.ContactGroup
import javax.inject.Inject

class CursorToContactGroupImpl @Inject constructor(
    private val context: Context
) : CursorToContactGroup {

    companion object {
        private val URI = ContactsContract.Groups.CONTENT_URI
        private val PROJECTION = arrayOf(
                ContactsContract.Groups._ID,
                ContactsContract.Groups.TITLE)
        private const val SELECTION = "${ContactsContract.Groups.AUTO_ADD}=0 " +
                "AND ${ContactsContract.Groups.DELETED}=0 " +
                "AND ${ContactsContract.Groups.FAVORITES}=0 " +
                "AND ${ContactsContract.Groups.TITLE} IS NOT NULL"

        private const val ID = 0
        private const val TITLE = 1
    }

    override fun map(from: Cursor): ContactGroup {
        return ContactGroup(from.getLong(ID), from.getString(TITLE))
    }

    override fun getContactGroupsCursor(): Cursor? {
        return context.contentResolver.query(URI, PROJECTION, SELECTION, null, null)
    }

}
