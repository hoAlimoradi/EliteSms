package com.alimoradi.elitesms.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.alimoradi.common.utils.resolveThemeAttribute
import com.alimoradi.common.utils.resolveThemeColorStateList
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.common.util.extensions.viewBinding
import com.alimoradi.elitesms.databinding.PreferenceViewBinding
import com.alimoradi.elitesms.injection.appComponent


class PreferenceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

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

    val binding: PreferenceViewBinding = viewBinding(PreferenceViewBinding::inflate)

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
        }

        setBackgroundResource(context.resolveThemeAttribute(R.attr.selectableItemBackground))
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        binding.icon.imageTintList = context.resolveThemeColorStateList(android.R.attr.textColorSecondary)

        context.obtainStyledAttributes(attrs, R.styleable.PreferenceView).run {
            title = getString(R.styleable.PreferenceView_title)
            summary = getString(R.styleable.PreferenceView_summary)

            // If there's a custom view used for the preference's widget, inflate it
            getResourceId(R.styleable.PreferenceView_widget, -1).takeIf { it != -1 }?.let { id ->
                View.inflate(context, id, binding.widgetFrame)
            }

            // If an icon is being used, set up the icon view
            getResourceId(R.styleable.PreferenceView_icon, -1).takeIf { it != -1 }?.let { id ->
                binding.icon.setVisible(true)
                binding.icon.setImageResource(id)
            }

            recycle()
        }
    }

    fun <T : View> widget(): T {
        return binding.widgetFrame.getChildAt(0) as T
    }

}
