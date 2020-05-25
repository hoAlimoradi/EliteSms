package com.alimoradi.data.repository

import com.alimoradi.domain.model.ScheduledMessage
import com.alimoradi.domain.repository.ScheduledMessageRepository
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import javax.inject.Inject

class ScheduledMessageRepositoryImpl @Inject constructor() : ScheduledMessageRepository {

    override fun saveScheduledMessage(
        date: Long,
        subId: Int,
        recipients: List<String>,
        sendAsGroup: Boolean,
        body: String,
        attachments: List<String>
    ) {
        Realm.getDefaultInstance().use { realm ->
            val id = (realm.where(ScheduledMessage::class.java).max("id")?.toLong() ?: -1) + 1
            val recipientsRealmList = RealmList(*recipients.toTypedArray())
            val attachmentsRealmList = RealmList(*attachments.toTypedArray())

            val message = ScheduledMessage(id, date, subId, recipientsRealmList, sendAsGroup, body,
                    attachmentsRealmList)

            realm.executeTransaction { realm.insertOrUpdate(message) }
        }
    }

    override fun getScheduledMessages(): RealmResults<ScheduledMessage> {
        return Realm.getDefaultInstance()
                .where(ScheduledMessage::class.java)
                .sort("date")
                .findAll()
    }

    override fun getScheduledMessage(id: Long): ScheduledMessage? {
        return Realm.getDefaultInstance()
                .apply { refresh() }
                .where(ScheduledMessage::class.java)
                .equalTo("id", id)
                .findFirst()
    }

    override fun deleteScheduledMessage(id: Long) {
        Realm.getDefaultInstance()
                .apply { refresh() }
                .use { realm ->
                    val message = realm.where(ScheduledMessage::class.java)
                            .equalTo("id", id)
                            .findFirst()

                    realm.executeTransaction { message?.deleteFromRealm() }
                }
    }

}