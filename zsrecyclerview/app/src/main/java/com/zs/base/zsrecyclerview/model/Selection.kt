package com.zs.base.zsrecyclerview.model

import androidx.lifecycle.MutableLiveData

class Selection(title: String, text: String) {
    val title = MutableLiveData(title)
    val text = MutableLiveData(text)
}