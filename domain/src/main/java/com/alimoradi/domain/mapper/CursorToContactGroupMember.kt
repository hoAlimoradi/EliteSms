package com.alimoradi.domain.mapper

import android.database.Cursor

interface CursorToContactGroupMember : Mapper<Cursor, CursorToContactGroupMember.GroupMember> {

    data class GroupMember(val lookupKey: String, val groupId: Long)

    fun getGroupMembersCursor(): Cursor?

}
