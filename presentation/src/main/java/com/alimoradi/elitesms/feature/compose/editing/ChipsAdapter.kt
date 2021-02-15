package com.alimoradi.elitesms.feature.compose.editing
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.domain.model.Recipient
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.extensions.dpToPx
import com.alimoradi.elitesms.common.util.extensions.setBackgroundTint
import com.alimoradi.elitesms.databinding.ContactChipBinding
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ChipsAdapter @Inject constructor() : QkAdapter<Recipient, ContactChipBinding>() {

    var view: RecyclerView? = null
    val chipDeleted: PublishSubject<Recipient> = PublishSubject.create<Recipient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ContactChipBinding> {
        return QkViewHolder(parent, ContactChipBinding::inflate).apply {
            // These theme attributes don't apply themselves on API 21
            if (Build.VERSION.SDK_INT <= 22) {
                binding.content.setBackgroundTint(parent.context.resolveThemeColor(R.attr.bubbleColor))
            }

            binding.root.setOnClickListener {
                val chip = getItem(adapterPosition)
                showDetailedChip(parent.context, chip)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ContactChipBinding>, position: Int) {
        val recipient = getItem(position)

        holder.binding.avatar.setRecipient(recipient)
        holder.binding.name.text = recipient.contact?.name?.takeIf { it.isNotBlank() } ?: recipient.address
    }

    /**
     * The [context] has to come from a view, because we're inflating a view that used themed attrs
     */
    private fun showDetailedChip(context: Context, recipient: Recipient) {
        val detailedChipView = DetailedChipView(context)
        detailedChipView.setRecipient(recipient)

        val rootView = view?.rootView as ViewGroup

        val layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        layoutParams.topMargin = 32.dpToPx(context)
        layoutParams.marginStart = 56.dpToPx(context)
        layoutParams.marginEnd = 8.dpToPx(context)

        rootView.addView(detailedChipView, layoutParams)
        detailedChipView.show()

        detailedChipView.setOnDeleteListener {
            chipDeleted.onNext(recipient)
            detailedChipView.hide()
        }
    }
}
