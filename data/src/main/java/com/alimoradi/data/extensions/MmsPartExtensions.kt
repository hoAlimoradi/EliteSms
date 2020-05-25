package com.alimoradi.data.extensions

import com.alimoradi.domain.model.MmsPart
import com.alimoradi.smsmms.com.google.android.mms.ContentType


fun MmsPart.isSmil() = ContentType.APP_SMIL == type

fun MmsPart.isImage() = ContentType.isImageType(type)

fun MmsPart.isVideo() = ContentType.isVideoType(type)

fun MmsPart.isText() = ContentType.TEXT_PLAIN == type

fun MmsPart.isVCard() = ContentType.TEXT_VCARD == type
