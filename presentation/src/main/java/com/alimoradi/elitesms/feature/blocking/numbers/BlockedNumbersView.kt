package com.alimoradi.elitesms.feature.blocking.numbers

import com.alimoradi.elitesms.common.base.QkViewContract
import io.reactivex.Observable

interface BlockedNumbersView : QkViewContract<BlockedNumbersState> {

    fun unblockAddress(): Observable<Long>
    fun addAddress(): Observable<*>
    fun saveAddress(): Observable<String>

    fun showAddDialog()

}
