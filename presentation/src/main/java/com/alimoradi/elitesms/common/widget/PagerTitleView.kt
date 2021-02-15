package com.alimoradi.elitesms.common.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.domain.extensions.Optional
import com.alimoradi.domain.repository.ConversationRepository
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.forEach
import com.alimoradi.elitesms.databinding.TabViewBinding
import com.alimoradi.elitesms.injection.appComponent
import com.uber.autodispose.android.ViewScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class PagerTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    @Inject lateinit var colors: Colors
    @Inject lateinit var conversationRepo: ConversationRepository

    private val recipientId: Subject<Long> = BehaviorSubject.create()

    var pager: ViewPager? = null
        set(value) {
            if (field !== value) {
                field = value
                recreate()
            }
        }

    init {
        if (!isInEditMode) appComponent.inject(this)
    }

    fun setRecipientId(id: Long) {
        recipientId.onNext(id)
    }

    private fun recreate() {
        removeAllViews()

        pager?.adapter?.count?.forEach { position ->
            val binding = TabViewBinding.inflate(LayoutInflater.from(context), this, false)
            binding.label.text = pager?.adapter?.getPageTitle(position)
            binding.root.setOnClickListener { pager?.currentItem = position }

            addView(binding.root)
        }

        childCount.forEach { index ->
            getChildAt(index).isActivated = index == pager?.currentItem
        }

        pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                childCount.forEach { index ->
                    getChildAt(index).isActivated = index == position
                }
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val states = arrayOf(
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(-android.R.attr.state_activated))

        recipientId
                .distinctUntilChanged()
                .map { recipientId -> Optional(conversationRepo.getRecipient(recipientId)) }
                .switchMap { recipient -> colors.themeObservable(recipient.value) }
                .map { theme ->
                    val textSecondary = context.resolveThemeColor(android.R.attr.textColorSecondary)
                    ColorStateList(states, intArrayOf(theme.theme, textSecondary))
                }
                .autoDisposable(ViewScopeProvider.from(this))
                .subscribe { colorStateList ->
                    childCount.forEach { index ->
                        (getChildAt(index) as? TextView)?.setTextColor(colorStateList)
                    }
                }
    }

}
