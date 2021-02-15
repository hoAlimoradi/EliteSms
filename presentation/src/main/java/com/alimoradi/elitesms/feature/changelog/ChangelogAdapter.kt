package com.alimoradi.elitesms.feature.changelog

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import com.alimoradi.domain.manager.ChangelogManager
import com.alimoradi.elitesms.R
import com.alimoradi.elitesms.common.base.QkAdapter
import com.alimoradi.elitesms.common.base.QkViewHolder
import com.alimoradi.elitesms.databinding.ChangelogListItemBinding

class ChangelogAdapter(
    private val context: Context
) : QkAdapter<ChangelogAdapter.ChangelogItem, ChangelogListItemBinding>() {

    data class ChangelogItem(val type: Int, val label: String)

    fun setChangelog(changelog: ChangelogManager.Changelog) {
        val changes = mutableListOf<ChangelogItem>()
        if (changelog.added.isNotEmpty()) {
            changes += ChangelogItem(0, context.getString(R.string.changelog_added))
            changes += changelog.added.map { change -> ChangelogItem(1, change) }
            changes += ChangelogItem(0, "")
        }
        if (changelog.improved.isNotEmpty()) {
            changes += ChangelogItem(0, context.getString(R.string.changelog_improved))
            changes += changelog.improved.map { change -> ChangelogItem(1, change) }
            changes += ChangelogItem(0, "")
        }
        if (changelog.fixed.isNotEmpty()) {
            changes += ChangelogItem(0, context.getString(R.string.changelog_fixed))
            changes += changelog.fixed.map { change -> ChangelogItem(1, change) }
            changes += ChangelogItem(0, "")
        }
        data = changes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder<ChangelogListItemBinding> {
        return QkViewHolder(parent, ChangelogListItemBinding::inflate).apply {
            if (viewType == 0) {
                binding.changelogItem.setTypeface(binding.changelogItem.typeface, Typeface.BOLD)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder<ChangelogListItemBinding>, position: Int) {
        val item = getItem(position)

        holder.binding.changelogItem.text = item.label
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

}
