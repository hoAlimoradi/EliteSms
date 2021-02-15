package com.alimoradi.elitesms.common.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.alimoradi.data.util.GlideApp
import com.alimoradi.domain.model.Recipient
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.Navigator
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.common.util.extensions.setTint
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.AvatarViewBinding
import com.alimoradi.elitesms.injection.appComponent
import javax.inject.Inject

class AvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    @Inject lateinit var colors: Colors
    @Inject lateinit var navigator: Navigator
    private lateinit var theme: Colors.Theme

    private val binding = viewBinding(AvatarViewBinding::inflate)

    private var lookupKey: String? = null
    private var name: String? = null
    private var photoUri: String? = null
    private var lastUpdated: Long? = null

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
            theme = colors.theme()
        }

        setBackgroundResource(R.drawable.circle)
        clipToOutline = true
    }

    /**
     * Use the [contact] information to display the avatar.
     */
    fun setRecipient(recipient: Recipient?) {
        lookupKey = recipient?.contact?.lookupKey
        name = recipient?.contact?.name
        photoUri = recipient?.contact?.photoUri
        lastUpdated = recipient?.contact?.lastUpdate
        theme = colors.theme(recipient)
        updateView()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!isInEditMode) {
            updateView()
        }
    }

    private fun updateView() {
        // Apply theme
        setBackgroundTint(theme.theme)
        binding.initial.setTextColor(theme.textPrimary)
        binding.icon.setTint(theme.textPrimary)

        if (name?.isNotEmpty() == true) {
            val initials = name
                    ?.substringBefore(',')
                    ?.split(" ").orEmpty()
                    .filter { subname -> subname.isNotEmpty() }
                    .map { subname -> subname[0].toString() }

            binding.initial.text = if (initials.size > 1) initials.first() + initials.last() else initials.first()
            binding.icon.visibility = GONE
        } else {
            binding.initial.text = null
            binding.icon.visibility = VISIBLE
        }

        binding.photo.setImageDrawable(null)
        photoUri?.let { photoUri ->
            GlideApp.with(binding.photo)
                    .load(photoUri)
                    .into(binding.photo)
        }
    }
}
