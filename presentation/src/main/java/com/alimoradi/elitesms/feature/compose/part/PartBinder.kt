package com.alimoradi.elitesms.feature.compose.part

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.MmsPart
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

abstract class PartBinder<Binding : ViewBinding>(
    val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> Binding
) {

    val clicks: Subject<Long> = PublishSubject.create()

    abstract var theme: Colors.Theme

    fun <T: ViewBinding> bindPart(
        holder: QkViewHolder<T>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ): Boolean {
        val castHolder = holder as? QkViewHolder<Binding>

        if (!canBindPart(part) || castHolder == null) {
            return false
        }

        bindPartInternal(castHolder, part, message, canGroupWithPrevious, canGroupWithNext)

        return true
    }

    abstract fun canBindPart(part: MmsPart): Boolean

    protected abstract fun bindPartInternal(
        holder: QkViewHolder<Binding>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    )

}
