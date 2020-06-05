package com.zs.base.zsrecyclerview.model

import androidx.lifecycle.LiveData
import com.zs.base.zsrecyclerview.ui.common.LiveItemViewModel

class SelectionItemViewModel(private val selection: Selection) : LiveItemViewModel() {

    fun getTitle(): LiveData<String> {
        return selection.title
    }

    fun getText(): LiveData<String> {
        return selection.text
    }

    fun onClickItem() {
        SelectionManager.selectedItem.value = selection
    }
}
