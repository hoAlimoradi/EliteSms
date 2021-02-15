package com.alimoradi.elitesms.feature.contacts

import com.alimoradi.domain.extensions.Optional
import com.alimoradi.elitesms.common.base.QkView
import com.alimoradi.elitesms.feature.compose.editing.ComposeItem
import com.alimoradi.elitesms.feature.compose.editing.PhoneNumberAction
import io.reactivex.Observable
import io.reactivex.subjects.Subject

interface ContactsContract : QkView<ContactsState> {

    val queryChangedIntent: Observable<CharSequence>
    val queryClearedIntent: Observable<*>
    val queryEditorActionIntent: Observable<Int>
    val composeItemPressedIntent: Subject<ComposeItem>
    val composeItemLongPressedIntent: Subject<ComposeItem>
    val phoneNumberSelectedIntent: Subject<Optional<Long>>
    val phoneNumberActionIntent: Subject<PhoneNumberAction>

    fun clearQuery()
    fun openKeyboard()
    fun finish(result: HashMap<String, String?>)

}
