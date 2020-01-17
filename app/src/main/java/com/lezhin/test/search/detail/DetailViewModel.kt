package com.lezhin.test.search.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class DetailViewModel : ViewModel() {
    val imageUrl = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    var imageWidth = 0
    var imageHeight = 0
}