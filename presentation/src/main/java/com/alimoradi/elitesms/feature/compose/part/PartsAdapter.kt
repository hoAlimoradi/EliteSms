package com.alimoradi.elitesms.feature.compose.part

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alimoradi.data.extensions.isSmil
import com.alimoradi.data.extensions.isText
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.forwardTouches
import com.alimoradi.elitesms.databinding.MessageListItemInBinding
import com.alimoradi.elitesms.feature.compose.BubbleUtils.canGroup
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.MmsPart
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import io.reactivex.Observable
import javax.inject.Inject

class PartsAdapter @Inject constructor(
    colors: Colors,
    fileBinder: FileBinder,
    mediaBinder: MediaBinder,
    vCardBinder: VCardBinder
) : QkAdapter<MmsPart, ViewBinding>() {

    private val partBinders = listOf(mediaBinder, vCardBinder, fileBinder)

    var theme: Colors.Theme = colors.theme()
        set(value) {
            field = value
            partBinders.forEach { binder -> binder.theme = value }
        }

    val clicks: Observable<Long> = Observable.merge(partBinders.map { it.clicks })

    private lateinit var message: Message
    private var previous: Message? = null
    private var next: Message? = null
    private var holder: QkViewHolder<MessageListItemInBinding>? = null
    private var bodyVisible: Boolean = true

    fun setData(message: Message, previous: Message?, next: Message?, holder: QkViewHolder<MessageListItemInBinding>) {
        this.message = message
        this.previous = previous
        this.next = next
        this.holder = holder
        this.bodyVisible = holder.binding.body.visibility == View.VISIBLE
        this.data = message.parts.filter { !it.isSmil() && !it.isText() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ViewBinding> {
        return QkViewHolder(parent, partBinders[viewType].bindingInflater).apply {
            holder?.binding?.root?.let(binding.root::forwardTouches)
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ViewBinding>, position: Int) {
        val part = data[position]

        val canGroupWithPrevious = canGroup(message, previous) || position > 0
        val canGroupWithNext = canGroup(message, next) || position < itemCount - 1 || bodyVisible

        partBinders.find { binder -> binder.bindPart(holder, part, message, canGroupWithPrevious, canGroupWithNext) }
    }

    override fun getItemViewType(position: Int): Int {
        val part = data[position]
        return partBinders.indexOfFirst { it.canBindPart(part) }
    }

}