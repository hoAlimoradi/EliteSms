package com.alimoradi.elitesms.common.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.alimoradi.common.utils.getColorCompat
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.domain.model.Recipient
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.GroupAvatarViewBinding

/*import com.moez.QKSMS.common.util.extensions.getColorCompat
import com.moez.QKSMS.common.util.extensions.resolveThemeColor
import com.moez.QKSMS.common.util.extensions.setBackgroundTint
import com.moez.QKSMS.common.util.extensions.viewBinding
import com.moez.QKSMS.databinding.GroupAvatarViewBinding
import com.moez.QKSMS.model.Recipient*/

class GroupAvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var recipients: List<Recipient> = ArrayList()
        set(value) {
            field = value.sortedWith(compareByDescending { contact -> contact.contact?.lookupKey })
            updateView()
        }

    private val binding = viewBinding(GroupAvatarViewBinding::inflate)

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!isInEditMode) {
            updateView()
        }
    }

    private fun updateView() {
        binding.avatar1Frame.setBackgroundTint(when (recipients.size > 1) {
            true -> context.resolveThemeColor(android.R.attr.windowBackground)
            false -> context.getColorCompat(android.R.color.transparent)
        })
        binding.avatar1Frame.updateLayoutParams<LayoutParams> {
            matchConstraintPercentWidth = if (recipients.size > 1) 0.75f else 1.0f
        }
        binding.avatar2.isVisible = recipients.size > 1


        recipients.getOrNull(0).run(binding.avatar1::setRecipient)
        recipients.getOrNull(1).run(binding.avatar2::setRecipient)
    }

}
