package com.alimoradi.elitesms.feature.blocking.numbers

import android.view.ViewGroup
import com.alimoradi.domain.model.BlockedNumber
import com.alimoradi.elitesms.common.base.QkRealmAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.databinding.BlockedNumberListItemBinding
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class BlockedNumbersAdapter : QkRealmAdapter<BlockedNumber, BlockedNumberListItemBinding>() {

    val unblockAddress: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<BlockedNumberListItemBinding> {
        return QkViewHolder(parent, BlockedNumberListItemBinding::inflate).apply {
            binding.unblock.setOnClickListener {
                val number = getItem(adapterPosition) ?: return@setOnClickListener
                unblockAddress.onNext(number.id)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<BlockedNumberListItemBinding>, position: Int) {
        val item = getItem(position)!!

        holder.binding.number.text = item.address
    }

}
