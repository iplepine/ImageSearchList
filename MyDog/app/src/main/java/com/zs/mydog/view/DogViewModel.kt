package com.zs.mydog.view

import androidx.lifecycle.ViewModel
import com.zs.mydog.data.Direction
import com.zs.mydog.data.Dog

class DogViewModel : ViewModel() {
    val dog = Dog()

    fun updateTime(time:Long) {
        dog.updateTime(time)
    }

    fun onClickDirection(type : Direction.Type) {
        dog.onDirect(type)
    }

    fun onClickReward() {
        dog.onReceiveReward()
    }
}