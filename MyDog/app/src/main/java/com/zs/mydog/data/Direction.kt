package com.zs.mydog.data

import kotlin.math.max

class Direction(val type: Type) {
    enum class Type { SIT, GO, COME, DOWN, BANG, ROLL }

    private val actionTypes = arrayOf(
        DogAction.Type.COME,
        DogAction.Type.GO_AWAY,
        DogAction.Type.SIT,
        DogAction.Type.DOWN,
        DogAction.Type.ROLL,
        DogAction.Type.BANG
    )

    val correctAction = let {
        when (type) {
            Type.SIT -> DogAction.Type.SIT
            Type.COME -> DogAction.Type.COME
            Type.GO -> DogAction.Type.GO_AWAY
            Type.DOWN -> DogAction.Type.DOWN
            Type.BANG -> DogAction.Type.BANG
            Type.ROLL -> DogAction.Type.ROLL
        }
    }

    fun isCorrectAction(actionType: DogAction.Type?): Boolean {
        return correctAction == actionType
    }

    val actionWeights = HashMap<DogAction.Type, Int>().apply {
        actionTypes.forEach {
            put(it, Dog.MIN_WEIGHT)
        }
    }

    fun weightUp(actionType: DogAction.Type, weight: Int) {
        if (actionWeights.containsKey(actionType)) {
            actionWeights[actionType] = (actionWeights[actionType] ?: Dog.MIN_WEIGHT) + weight + 100
        }
    }

    fun weightDown(actionType: DogAction.Type, weight: Int) {
        actionWeights[actionType] =
            max((actionWeights[actionType] ?: Dog.MIN_WEIGHT) - weight, Dog.MIN_WEIGHT)
    }

    fun weightDownAll(weight: Int) {
        actionWeights.forEach { weightDown(it.key, weight) }
    }
}
