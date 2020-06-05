package com.zs.base.zsrecyclerview.ui.common

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class LiveViewHolder(val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun getVariableId(): Int

    fun bind(item: LiveItemViewModel) {
        binding.setVariable(getVariableId(), item)
    }
}