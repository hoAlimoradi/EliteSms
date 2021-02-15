package com.alimoradi.elitesms.feature.compose.part

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.databinding.MmsFileListItemBinding
import com.alimoradi.elitesms.feature.compose.BubbleUtils
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.MmsPart
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FileBinder @Inject constructor(
    colors: Colors,
    private val context: Context
) : PartBinder<MmsFileListItemBinding>(MmsFileListItemBinding::inflate) {

    override var theme = colors.theme()

    // This is the last binder we check. If we're here, we can bind the part
    override fun canBindPart(part: MmsPart) = true

    @SuppressLint("CheckResult")
    override fun bindPartInternal(
        holder: QkViewHolder<MmsFileListItemBinding>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ) {
        BubbleUtils.getBubble(false, canGroupWithPrevious, canGroupWithNext, message.isMe())
                .let(holder.binding.fileBackground::setBackgroundResource)

        holder.binding.root.setOnClickListener { clicks.onNext(part.id) }

        Observable.just(part.getUri())
                .map(context.contentResolver::openInputStream)
                .map { inputStream -> inputStream.use { it.available() } }
                .map { bytes ->
                    when (bytes) {
                        in 0..999 -> "$bytes B"
                        in 1000..999999 -> "${"%.1f".format(bytes / 1000f)} KB"
                        in 1000000..9999999 -> "${"%.1f".format(bytes / 1000000f)} MB"
                        else -> "${"%.1f".format(bytes / 1000000000f)} GB"
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { size -> holder.binding.size.text = size }

        holder.binding.filename.text = part.name

        val params = holder.binding.fileBackground.layoutParams as FrameLayout.LayoutParams
        if (!message.isMe()) {
            holder.binding.fileBackground.layoutParams = params.apply { gravity = Gravity.START }
            holder.binding.fileBackground.setBackgroundTint(theme.theme)
            holder.binding.icon.setTint(theme.textPrimary)
            holder.binding.filename.setTextColor(theme.textPrimary)
            holder.binding.size.setTextColor(theme.textTertiary)
        } else {
            holder.binding.fileBackground.layoutParams = params.apply { gravity = Gravity.END }
            holder.binding.fileBackground.setBackgroundTint(holder.binding.root.context.resolveThemeColor(R.attr.bubbleColor))
            holder.binding.icon.setTint(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorSecondary))
            holder.binding.filename.setTextColor(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorPrimary))
            holder.binding.size.setTextColor(holder.binding.root.context.resolveThemeColor(android.R.attr.textColorTertiary))
        }
    }

}