package com.alimoradi.elitesms.feature.compose.editing

import android.view.ViewGroup
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.databinding.ContactNumberListItemBinding
import com.alimoradi.domain.model.PhoneNumber

class PhoneNumberAdapter : QkAdapter<PhoneNumber, ContactNumberListItemBinding>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ContactNumberListItemBinding> {
        return QkViewHolder(parent, ContactNumberListItemBinding::inflate)
    }

    override fun onBindViewHolder(holder: QkViewHolder<ContactNumberListItemBinding>, position: Int) {
        val number = getItem(position)

        holder.binding.address.text = number.address
        holder.binding.type.text = number.type
    }

    override fun areItemsTheSame(old: PhoneNumber, new: PhoneNumber): Boolean {
        return old.type == new.type && old.address == new.address
    }

    override fun areContentsTheSame(old: PhoneNumber, new: PhoneNumber): Boolean {
        return old.type == new.type && old.address == new.address
    }

}
