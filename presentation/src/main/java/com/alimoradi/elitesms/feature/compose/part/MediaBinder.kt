package com.alimoradi.elitesms.feature.compose.part

import android.content.Context
import com.alimoradi.data.extensions.isImage
import com.alimoradi.data.extensions.isVideo
import com.alimoradi.data.util.GlideApp
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.widget.BubbleImageView
import com.alimoradi.elitesms.databinding.MmsPreviewListItemBinding
import com.alimoradi.domain.model.Message
import com.alimoradi.domain.model.MmsPart
import javax.inject.Inject

class MediaBinder @Inject constructor(
    colors: Colors,
    private val context: Context
) : PartBinder<MmsPreviewListItemBinding>(MmsPreviewListItemBinding::inflate) {

    override var theme = colors.theme()

    override fun canBindPart(part: MmsPart) = part.isImage() || part.isVideo()

    override fun bindPartInternal(
        holder: QkViewHolder<MmsPreviewListItemBinding>,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ) {
        holder.binding.video.setVisible(part.isVideo())
        holder.binding.root.setOnClickListener { clicks.onNext(part.id) }

        holder.binding.thumbnail.bubbleStyle = when {
            !canGroupWithPrevious && canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_FIRST else BubbleImageView.Style.IN_FIRST
            canGroupWithPrevious && canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_MIDDLE else BubbleImageView.Style.IN_MIDDLE
            canGroupWithPrevious && !canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_LAST else BubbleImageView.Style.IN_LAST
            else -> BubbleImageView.Style.ONLY
        }

        GlideApp.with(context).load(part.getUri()).fitCenter().into(holder.binding.thumbnail)
    }

}