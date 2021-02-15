package com.alimoradi.elitesms.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.alimoradi.common.utils.resolveThemeAttribute
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.forwardTouches
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.RadioPreferenceViewBinding
import com.alimoradi.elitesms.injection.appComponent
import javax.inject.Inject

class RadioPreferenceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    @Inject lateinit var colors: Colors

    var title: String? = null
        set(value) {
            field = value

            if (isInEditMode) {
                findViewById<TextView>(R.id.titleView).text = value
            } else {
                binding.titleView.text = value
            }
        }

    var summary: String? = null
        set(value) {
            field = value


            if (isInEditMode) {
                findViewById<TextView>(R.id.summaryView).run {
                    text = value
                    setVisible(value?.isNotEmpty() == true)
                }
            } else {
                binding.summaryView.text = value
                binding.summaryView.setVisible(value?.isNotEmpty() == true)
            }
        }

    val binding: RadioPreferenceViewBinding = viewBinding(RadioPreferenceViewBinding::inflate)

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
        }

        setBackgroundResource(context.resolveThemeAttribute(R.attr.selectableItemBackground))

        val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked))

        val themeColor = when (isInEditMode) {
            true -> context.resources.getColor(R.color.tools_theme)
            false -> colors.theme().theme
        }
        val textSecondary = context.resolveThemeColor(android.R.attr.textColorTertiary)
        binding.radioButton.buttonTintList = ColorStateList(states, intArrayOf(themeColor, textSecondary))
        binding.radioButton.forwardTouches(this)

        context.obtainStyledAttributes(attrs, R.styleable.RadioPreferenceView)?.run {
            title = getString(R.styleable.RadioPreferenceView_title)
            summary = getString(R.styleable.RadioPreferenceView_summary)

            // If there's a custom view used for the preference's widget, inflate it
            getResourceId(R.styleable.RadioPreferenceView_widget, -1).takeIf { it != -1 }?.let { id ->
                View.inflate(context, id, binding.widgetFrame)
            }

            recycle()
        }
    }

}
