package com.zs.base.zsrecyclerview.ui.main

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.zs.base.zsrecyclerview.model.Selection
import com.zs.base.zsrecyclerview.model.SelectionItemViewModel
import com.zs.base.zsrecyclerview.ui.common.LiveViewModel

class MainViewModel : LiveViewModel() {
    val items = MutableLiveData<List<SelectionItemViewModel>>()
    val selections = arrayOf("일번", "이번", "삼번", "사범", "5번")

    init {
        refresh()
    }

    fun refresh() {
        items.value = selections.asList().shuffled().map {
            SelectionItemViewModel(Selection("타이틀", it))
        }
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<SelectionItemViewModel>?) {
    data?.also {
        (recyclerView.adapter as? SelectionAdapter)?.setData(it)
    }
}
