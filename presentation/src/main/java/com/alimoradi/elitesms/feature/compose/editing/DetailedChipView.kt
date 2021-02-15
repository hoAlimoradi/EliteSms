
package com.alimoradi.elitesms.feature.compose.editing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.dpToPx
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.ContactChipDetailedBinding
import com.alimoradi.elitesms.injection.appComponent
import com.alimoradi.domain.model.Recipient
import javax.inject.Inject

class DetailedChipView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    @Inject lateinit var colors: Colors

    private val binding = viewBinding(ContactChipDetailedBinding::inflate)

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
            visibility = View.GONE
        }

        setOnClickListener { hide() }
        setBackgroundResource(R.drawable.rounded_rectangle_2dp)

        elevation = 8.dpToPx(context) .toFloat()
//        updateLayoutParams<LinearLayout.LayoutParams> { setMargins(8.dpToPx(context) ) }

        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun setRecipient(recipient: Recipient) {
        binding.avatar.setRecipient(recipient)
        binding.name.text = recipient.contact?.name?.takeIf { it.isNotBlank() } ?: recipient.address
        binding.info.text = recipient.address

        colors.theme(recipient).let { theme ->
            setBackgroundTint(theme.theme)
            binding.name.setTextColor(theme.textPrimary)
            binding.info.setTextColor(theme.textTertiary)
            binding.delete.setTint(theme.textPrimary)
        }
    }

    fun show() {
        startAnimation(AlphaAnimation(0f, 1f).apply { duration = 200 })

        visibility = View.VISIBLE
        requestFocus()
        isClickable = true
    }

    fun hide() {
        startAnimation(AlphaAnimation(1f, 0f).apply { duration = 200 })

        visibility = View.GONE
        clearFocus()
        isClickable = false
    }

    fun setOnDeleteListener(listener: (View) -> Unit) {
        binding.delete.setOnClickListener(listener)
    }

}
