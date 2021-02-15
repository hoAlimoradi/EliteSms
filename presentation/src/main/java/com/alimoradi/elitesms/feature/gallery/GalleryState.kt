package com.alimoradi.elitesms.feature.gallery

import com.alimoradi.domain.model.MmsPart
import io.realm.RealmResults

data class GalleryState(
    val navigationVisible: Boolean = true,
    val title: String? = "",
    val parts: RealmResults<MmsPart>? = null
)
