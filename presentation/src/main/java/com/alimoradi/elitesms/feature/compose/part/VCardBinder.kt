package com.alimoradi.elitesms.feature.compose.part

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.data.extensions.isVCard
import com.alimoradi.domain.extensions.mapNotNull
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.databinding.MmsVcardListItemBinding
import com.alimoradi.elitesms.feature.compose.BubbleUtils
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.MmsPart
import ezvcard.Ezvcard
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VCardBinder @Inject constructor(
    colors: Colors,
    private val context: Context
) : PartBinder<MmsVcardListItemBinding>(MmsVcardListItemBinding::inflate) {

    override var theme = colors.theme()

    override fun canBindPart(part: MmsPart) = part.isVCard()

    override fun bindPartInternal(
        holder: QkViewHolder<MmsVcardListItemBinding>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ) {
        BubbleUtils.getBubble(false, canGroupWithPrevious, canGroupWithNext, message.isMe())
                .let(holder.binding.vCardBackground::setBackgroundResource)

        holder.binding.root.setOnClickListener { clicks.onNext(part.id) }

        Observable.just(part.getUri())
                .map(context.contentResolver::openInputStream)
                .mapNotNull { inputStream -> inputStream.use { Ezvcard.parse(it).first() } }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { vcard -> holder.binding.name?.text = vcard.formattedName.value }

        val params = holder.binding.vCardBackground.layoutParams as FrameLayout.LayoutParams
        if (!message.isMe()) {
            holder.binding.vCardBackground.layoutParams = params.apply { gravity = Gravity.START }
            holder.binding.vCardBackground.setBackgroundTint(theme.theme)
            holder.binding.vCardAvatar.setTint(theme.textPrimary)
            holder.binding.name.setTextColor(theme.textPrimary)
            holder.binding.label.setTextColor(theme.textTertiary)
        } else {
            holder.binding.vCardBackground.layoutParams = params.apply { gravity = Gravity.END }
            holder.binding.vCardBackground.setBackgroundTint(holder.binding.root.context.resolveThemeColor(R.attr.bubbleColor))
            holder.binding.vCardAvatar.setTint(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorSecondary))
            holder.binding.name.setTextColor(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorPrimary))
            holder.binding.label.setTextColor(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorTertiary))
        }
    }

}