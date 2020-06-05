package com.zs.base.zsrecyclerview.ui.common

abstract class LiveRecyclerViewModel : LiveViewModel() {
    abstract fun getItems(): List<LiveItemViewModel>
}
