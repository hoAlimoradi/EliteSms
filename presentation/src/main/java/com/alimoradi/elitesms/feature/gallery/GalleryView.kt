package com.alimoradi.elitesms.feature.gallery

import com.alimoradi.domain.model.MmsPart
import com.alimoradi.elitesms.common.base.QkView
import io.reactivex.Observable

interface GalleryView : QkView<GalleryState> {

    fun optionsItemSelected(): Observable<Int>
    fun screenTouched(): Observable<*>
    fun pageChanged(): Observable<MmsPart>

    fun requestStoragePermission()

}