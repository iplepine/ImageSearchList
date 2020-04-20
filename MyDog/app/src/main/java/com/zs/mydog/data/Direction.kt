package com.zs.mydog.data

import kotlin.math.max

class Direction(val type: Type) {
    enum class Type { SIT, COME, DOWN, BANG, ROLL }

    private val actionTypes = arrayOf(
        DogAction.Type.SIT,
        DogAction.Type.COME,
        DogAction.Type.DOWN,
        DogAction.Type.BANG,
        DogAction.Type.ROLL
    )

    val actionWeight = HashMap<DogAction.Type, Int>().apply {
        actionTypes.forEach {
            put(it, Dog.MIN_WEIGHT)
        }
    }

    fun weightUp(actionType: DogAction.Type, weight: Int) {
        actionWeight[actionType] = (actionWeight[actionType] ?: Dog.MIN_WEIGHT) + weight
    }

    fun weightDown(actionType: DogAction.Type, weight: Int) {
        actionWeight[actionType] = max((actionWeight[actionType] ?: Dog.MIN_WEIGHT) - weight, Dog.MIN_WEIGHT)
    }
}