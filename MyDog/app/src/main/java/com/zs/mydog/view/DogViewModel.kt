package com.zs.mydog.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zs.mydog.data.Direction
import com.zs.mydog.data.Dog

class DogViewModel : ViewModel() {
    val debugViewVisible = MutableLiveData<Boolean>().apply {
        value = false
    }

    val dog = Dog()

    fun updateTime(time: Long) {
        dog.updateTime(time)
    }

    fun onClickDirection(type: Direction.Type) {
        dog.onDirection(type)
    }

    fun onClickReward() {
        dog.onReceiveReward()
    }

    fun toggleDebugMode() {
        debugViewVisible.value = debugViewVisible.value != true
    }
}