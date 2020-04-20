package com.zs.mydog.data

import androidx.lifecycle.MutableLiveData
import com.zs.mydog.data.common.RandomUtil
import kotlin.math.max
import kotlin.random.Random

class Dog {

    companion object {
        const val MIN_WEIGHT = 10
        const val DIRECTION_TIME = 2000L
    }

    var actionHistoryLimit = 5
    val actionHistory = ArrayList<DogAction>()
    val currentDirection = MutableLiveData<Direction?>()

    var directions = ArrayList<Direction>()
    var actions = ArrayList<DogAction>()

    val currentAction = MutableLiveData<DogAction>()

    var directionDelay = MutableLiveData<Long>().apply { value = 0L }
    var actionDelay = MutableLiveData<Long>().apply { value = 0L }

    init {
        Direction.Type.values().forEach {
            directions.add(Direction(it))
        }
        DogAction.Type.values().forEach {
            actions.add(DogAction(it))
        }
    }

    fun getNextAction(): DogAction? {
        val weightArray = actions.map { it.weight }.toTypedArray()
        val actionIndex = RandomUtil.weightedIndex(weightArray)

        return actions.getOrNull(actionIndex)
    }

    fun doAction(action: DogAction) {
        actionHistory.add(0, action)
        while (actionHistory.size > actionHistoryLimit) {
            actionHistory.removeAt(actionHistoryLimit)
        }

        currentAction.postValue(action)
        val randomSecond = Random.nextInt(5, 10)
        actionDelay.postValue(randomSecond * 1000L)
    }

    fun onDirect(type: Direction.Type) {
        currentDirection.value = directions.find { it.type == type }
        directionDelay.value = DIRECTION_TIME
    }

    fun updateTime(time: Long) {
        directionDelay.value = max((directionDelay.value ?: 0) - time, 0L)
        if (directionDelay.value == 0L) {
            onFinishDirection()
        }

        actionDelay.value = max((actionDelay.value ?: 0) - time, 0L)

        if (actionDelay.value == 0L) {
            onFinishActionWithoutReward()
        }
    }

    fun onFinishDirection() {
        currentDirection.value?.also { direction ->
            currentAction.value?.also { action ->
                direction.weightDown(action.type, 1)
            }
        }
        currentDirection.value = null
    }

    fun onReceiveReward() {
        currentDirection.value?.also { direction ->
            currentAction.value?.also { action ->
                direction.weightUp(action.type, Random.nextInt(3))
            }
        }

        val weightBonus = arrayOf(3, 1, 1, 0, 0)
        actionHistory.forEachIndexed { index, dogAction ->
            actions.find {
                it.type == dogAction.type
            }?.apply {
                weightUp(weightBonus[index])
            }
        }

        currentAction.value = DogAction(DogAction.Type.EAT)
    }

    fun onFinishActionWithoutReward() {
        currentDirection.value?.also {
            currentAction.value?.apply {
                it.weightDown(type, 1)
                weightDown(1)
            }
        }

        getNextAction()?.also { doAction(it) }
    }
}