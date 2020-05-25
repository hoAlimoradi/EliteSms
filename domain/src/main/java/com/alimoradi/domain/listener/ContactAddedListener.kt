package com.alimoradi.domain.listener

import io.reactivex.Observable

interface ContactAddedListener {

    fun listen(): Observable<*>

}
