package com.alimoradi.elitesms.common

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.recyclerview.widget.RecyclerView
import com.alimoradi.common.utils.resolveThemeColor
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.common.util.Colors
import com.alimoradi.elitesms.common.util.extensions.setVisible
import com.alimoradi.elitesms.databinding.MenuListItemBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

data class MenuItem(val title: String, val actionId: Int)

class MenuItemAdapter @Inject constructor(
    private val context: Context,
    private val colors: Colors
) : QkAdapter<MenuItem, MenuListItemBinding>() {

    val menuItemClicks: Subject<Int> = PublishSubject.create()

    private val disposables = CompositeDisposable()

    var selectedItem: Int? = null
        set(value) {
            val old = data.map { it.actionId }.indexOfFirst { it == field }.takeIf { it != -1 }
            val new = data.map { it.actionId }.indexOfFirst { it == value }.takeIf { it != -1 }

            field = value

            old?.let(::notifyItemChanged)
            new?.let(::notifyItemChanged)
        }

    fun setData(@ArrayRes titles: Int, @ArrayRes values: Int = -1) {
        val valueInts = if (values != -1) context.resources.getIntArray(values) else null

        data = context.resources.getStringArray(titles)
                .mapIndexed { index, title -> MenuItem(title, valueInts?.getOrNull(index) ?: index) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<MenuListItemBinding> {
        return QkViewHolder(parent, MenuListItemBinding::inflate).apply {
            val states = arrayOf(
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(-android.R.attr.state_activated))

            val text = parent.context.resolveThemeColor(android.R.attr.textColorTertiary)
            binding.check.imageTintList = ColorStateList(states, intArrayOf(colors.theme().theme, text))

            binding.root.setOnClickListener {
                val menuItem = getItem(adapterPosition)
                menuItemClicks.onNext(menuItem.actionId)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<MenuListItemBinding>, position: Int) {
        val menuItem = getItem(position)

        holder.binding.title.text = menuItem.title
        holder.binding.check.isActivated = (menuItem.actionId == selectedItem)
        holder.binding.check.setVisible(selectedItem != null)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }

}