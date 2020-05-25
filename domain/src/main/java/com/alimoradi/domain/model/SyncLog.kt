package com.alimoradi.domain.model

import io.realm.RealmObject

open class SyncLog : RealmObject() {

    var date: Long = System.currentTimeMillis()

}