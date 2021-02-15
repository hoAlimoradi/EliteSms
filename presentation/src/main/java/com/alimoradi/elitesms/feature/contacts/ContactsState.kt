package com.alimoradi.elitesms.feature.contacts

import com.alimoradi.domain.model.Contact
import com.alimoradi.elitesms.feature.compose.editing.ComposeItem


data class ContactsState(
    val query: String = "",
    val composeItems: List<ComposeItem> = ArrayList(),
    val selectedContact: Contact? = null // For phone number picker
)
