package com.alimoradi.elitesms.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class QkViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {

    constructor(
        parent: ViewGroup,
        bindingInflator: (LayoutInflater, ViewGroup, Boolean) -> T
    ) : this(bindingInflator(LayoutInflater.from(parent.context), parent, false))

}
