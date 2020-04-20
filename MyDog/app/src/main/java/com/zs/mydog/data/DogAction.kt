package com.zs.mydog.data

import kotlin.math.max

class DogAction(val type: Type) {
    enum class Type { SIT, GO_AWAY, COME, DOWN, BANG, ROLL, LIE, EAT }

    var weight = 0

    fun weightUp(value: Int) {
        weight += value
    }

    fun weightDown(value: Int) {
        weight = max(weight - value, 0)
    }
}