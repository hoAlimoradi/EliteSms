
package com.alimoradi.elitesms.feature.compose.editing

import android.content.Context
import android.view.ViewGroup
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.extensions.forwardTouches
import com.alimoradi.elitesms.databinding.PhoneNumberListItemBinding
import com.alimoradi.domain.extensions.Optional
import com.alimoradi.domain.model.PhoneNumber
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class PhoneNumberPickerAdapter @Inject constructor(
    private val context: Context
) : QkAdapter<PhoneNumber, PhoneNumberListItemBinding>() {

    val selectedItemChanges: Subject<Optional<Long>> = BehaviorSubject.create()

    private var selectedItem: Long? = null
        set(value) {
            data.indexOfFirst { number -> number.id == field }.takeIf { it != -1 }?.run(::notifyItemChanged)
            field = value
            data.indexOfFirst { number -> number.id == field }.takeIf { it != -1 }?.run(::notifyItemChanged)
            selectedItemChanges.onNext(Optional(value))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<PhoneNumberListItemBinding> {
        return QkViewHolder(parent, PhoneNumberListItemBinding::inflate).apply {
            binding.number.binding.radioButton.forwardTouches(itemView)

            binding.root.setOnClickListener {
                val phoneNumber = getItem(adapterPosition)
                selectedItem = phoneNumber.id
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<PhoneNumberListItemBinding>, position: Int) {
        val phoneNumber = getItem(position)

        holder.binding.number.binding.radioButton.isChecked = phoneNumber.id == selectedItem
        holder.binding.number.binding.titleView.text = phoneNumber.address
        holder.binding.number.binding.summaryView.text = when (phoneNumber.isDefault) {
            true -> context.getString(R.string.compose_number_picker_default, phoneNumber.type)
            false -> phoneNumber.type
        }
    }

    override fun onDatasetChanged() {
        super.onDatasetChanged()
        selectedItem = data.find { number -> number.isDefault }?.id ?: data.firstOrNull()?.id
    }

}
