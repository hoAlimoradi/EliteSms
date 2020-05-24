package com.alimoradi.smsmms.com.android.mms.transaction;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Sms.Inbox;
import android.telephony.SmsMessage;

import com.alimoradi.smsmms.android.database.sqlite.SqliteWrapper;

import timber.log.Timber;

/**
 * Service that gets started by the MessageStatusReceiver when a message status report is
 * received.
 */
public class MessageStatusService extends IntentService {
    private static final String[] ID_PROJECTION = new String[] { Sms._ID };
    private static final Uri STATUS_URI = Uri.parse("content://sms/status");

    public MessageStatusService() {
        // Class name will be the thread name.
        super(MessageStatusService.class.getName());

        // Intent should be redelivered if the process gets killed before completing the job.
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This method is called on a worker thread.

        String messageUri = intent.getDataString();
        if (messageUri == null) {
            messageUri = intent.getStringExtra("message_uri");
            if (messageUri == null) {
                return;
            }
        }

        byte[] pdu = intent.getByteArrayExtra("pdu");
        String format = intent.getStringExtra("format");

        SmsMessage message = updateMessageStatus(this, Uri.parse(messageUri), pdu, format);
    }

    private SmsMessage updateMessageStatus(Context context, Uri messageUri, byte[] pdu,
            String format) {
        SmsMessage message = SmsMessage.createFromPdu(pdu);
        if (message == null) {
            return null;
        }
        // Create a "status/#" URL and use it to update the
        // message's status in the database.
        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
                            messageUri, ID_PROJECTION, null, null, null);
        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.moveToFirst()) {
                int messageId = cursor.getInt(0);

                Uri updateUri = ContentUris.withAppendedId(STATUS_URI, messageId);
                int status = message.getStatus();
                boolean isStatusReport = message.isStatusReportMessage();
                ContentValues contentValues = new ContentValues(2);

                log("updateMessageStatus: msgUrl=" + messageUri + ", status=" + status
                        + ", isStatusReport=" + isStatusReport);

                contentValues.put(Sms.STATUS, status);
                contentValues.put(Inbox.DATE_SENT, System.currentTimeMillis());
                SqliteWrapper.update(context, context.getContentResolver(),
                                    updateUri, contentValues, null, null);
            } else {
                error("Can't find message for status update: " + messageUri);
            }
        } finally {
            cursor.close();
        }
        return message;
    }

    private void error(String message) {
        Timber.e("[MessageStatusReceiver] " + message);
    }

    private void log(String message) {
        Timber.d("[MessageStatusReceiver] " + message);
    }
}
