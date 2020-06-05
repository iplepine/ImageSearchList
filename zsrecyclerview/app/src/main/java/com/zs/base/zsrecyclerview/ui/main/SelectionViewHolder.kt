package com.zs.base.zsrecyclerview.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zs.base.zsrecyclerview.BR
import com.zs.base.zsrecyclerview.databinding.ItemViewBinding
import com.zs.base.zsrecyclerview.ui.common.LiveViewHolder

class SelectionViewHolder(parent: ViewGroup) : LiveViewHolder(
    ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    override fun getVariableId(): Int {
        return BR.item
    }
}
