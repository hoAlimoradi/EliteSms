package com.alimoradi.elitesms.common.util

import android.content.Context
import com.alimoradi.common.utils.tryOrNull
import com.alimoradi.domain.model.Message
import com.alimoradi.elitesms.R
import com.alimoradi.smsmms.com.google.android.mms.pdu_alt.EncodedStringValue
import com.alimoradi.smsmms.com.google.android.mms.pdu_alt.MultimediaMessagePdu
import com.alimoradi.smsmms.com.google.android.mms.pdu_alt.PduPersister
import javax.inject.Inject

class MessageDetailsFormatter @Inject constructor(
    private val context: Context,
    private val dateFormatter: DateFormatter
) {

    fun format(message: Message): String {
        val builder = StringBuilder()

        message.type
                .takeIf { it.isNotBlank() }
                ?.toUpperCase()
                ?.let { context.getString(R.string.compose_details_type, it) }
                ?.let(builder::appendln)

        if (message.isSms()) {
            message.address
                    .takeIf { it.isNotBlank() && !message.isMe() }
                    ?.let { context.getString(R.string.compose_details_from, it) }
                    ?.let(builder::appendln)

            message.address
                    .takeIf { it.isNotBlank() && message.isMe() }
                    ?.let { context.getString(R.string.compose_details_to, it) }
                    ?.let(builder::appendln)
        } else {
            val pdu = tryOrNull {
                PduPersister.getPduPersister(context)
                        .load(message.getUri())
                        as MultimediaMessagePdu
            }

            pdu?.from?.string
                    ?.takeIf { it.isNotBlank() }
                    ?.let { context.getString(R.string.compose_details_from, it) }
                    ?.let(builder::appendln)

            pdu?.to
                    ?.let(EncodedStringValue::concat)
                    ?.takeIf { it.isNotBlank() }
                    ?.let { context.getString(R.string.compose_details_to, it) }
                    ?.let(builder::appendln)
        }

        message.date
                .takeIf { it > 0 && message.isMe() }
                ?.let(dateFormatter::getDetailedTimestamp)
                ?.let { context.getString(R.string.compose_details_sent, it) }
                ?.let(builder::appendln)

        message.dateSent
                .takeIf { it > 0 && !message.isMe() }
                ?.let(dateFormatter::getDetailedTimestamp)
                ?.let { context.getString(R.string.compose_details_sent, it) }
                ?.let(builder::appendln)

        message.date
                .takeIf { it > 0 && !message.isMe() }
                ?.let(dateFormatter::getDetailedTimestamp)
                ?.let { context.getString(R.string.compose_details_received, it) }
                ?.let(builder::appendln)

        message.dateSent
                .takeIf { it > 0 && message.isMe() }
                ?.let(dateFormatter::getDetailedTimestamp)
                ?.let { context.getString(R.string.compose_details_delivered, it) }
                ?.let(builder::appendln)

        message.errorCode
                .takeIf { it != 0 && message.isSms() }
                ?.let { context.getString(R.string.compose_details_error_code, it) }
                ?.let(builder::appendln)

        return builder.toString().trim()
    }

}