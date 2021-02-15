package com.alimoradi.elitesms.common.widget

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.databinding.QkDialogBinding

class QkDialog(private val context: Activity) : AlertDialog(context) {

    private val binding = QkDialogBinding.inflate(context.layoutInflater)

    @StringRes
    var titleRes: Int? = null
        set(value) {
            field = value
            title = value?.let(context::getString)
        }

    var title: String? = null
        set(value) {
            field = value
            binding.title.text = value
            binding.title.isVisible = !value.isNullOrBlank()
        }

    @StringRes
    var subtitleRes: Int? = null
        set(value) {
            field = value
            subtitle = value?.let(context::getString)
        }

    var subtitle: String? = null
        set(value) {
            field = value
            binding.subtitle.text = value
            binding.subtitle.isVisible = !value.isNullOrBlank()
        }

    var adapter: QkAdapter<*, *>? = null
        set(value) {
            field = value
            binding.list.isVisible = value != null
            binding.list.adapter = value
        }

    var positiveButtonListener: (() -> Unit)? = null

    @StringRes
    var positiveButton: Int? = null
        set(value) {
            field = value
            value?.run(binding.positiveButton::setText)
            binding.positiveButton.isVisible = value != null
            binding.positiveButton.setOnClickListener {
                positiveButtonListener?.invoke() ?: dismiss()
            }
        }

    var negativeButtonListener: (() -> Unit)? = null

    @StringRes
    var negativeButton: Int? = null
        set(value) {
            field = value
            value?.run(binding.negativeButton::setText)
            binding.negativeButton.isVisible = value != null
            binding.negativeButton.setOnClickListener {
                negativeButtonListener?.invoke() ?: dismiss()
            }
        }

    var cancelListener: (() -> Unit)? = null
        set(value) {
            field = value
            setOnCancelListener { value?.invoke() }
        }

    init {
        setView(binding.root)
    }

}
