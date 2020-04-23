package com.zs.mydog.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zs.mydog.data.common.RandomUtil
import kotlin.math.max
import kotlin.random.Random

class Dog {

    companion object {
        const val MIN_WEIGHT = 10
        const val DIRECTION_TIME = 3000L
        const val DIRECTION_ACTION_DOWN_WEIGHT = 3

        const val ACTION_DOWN_WEIGHT = 10
    }

    val actionHistory = ArrayList<DogAction>()
    val currentDirection = MutableLiveData<Direction?>()

    private var directTime: Long = 0L
    private var isActionFromDirection = false
    private var isDirectionRewarded = false

    val weightedActionTypes = arrayOf(
        DogAction.Type.SIT,
        DogAction.Type.GO_AWAY,
        DogAction.Type.COME,
        DogAction.Type.DOWN,
        DogAction.Type.ROLL,
        DogAction.Type.BANG
    )

    var directions = ArrayList<Direction>()
    var actions = ArrayList<DogAction>()

    val currentAction = MutableLiveData<DogAction>()

    var directionDelay = MutableLiveData<Long>().apply { value = 0L }
    var actionDelay = MutableLiveData<Long>().apply { value = 0L }

    init {
        Direction.Type.values().forEach {
            directions.add(Direction(it))
        }

        weightedActionTypes.forEach {
            actions.add(DogAction(it))
        }
    }

    fun findDogAction(type: DogAction.Type): DogAction? {
        return actions.find { type == it.type }
    }

    private fun getNextAction(): DogAction? {
        val weightArray = actions.map { it.weight }.toTypedArray()

        if (isDirectionExpired()) {
            // 명령어 시간 초과
            isActionFromDirection = false
        } else {
            // 명령어가 효과 있을 때
            currentDirection.value?.actionWeights?.also { directionWeights ->
                actions.forEachIndexed { index, dogAction ->
                    weightArray[index] =
                        weightArray[index] + (directionWeights[dogAction.type] ?: 0) * 10
                }
            }
            isActionFromDirection = true
        }

        val actionIndex = RandomUtil.weightedIndex(weightArray)
        return actions.getOrNull(actionIndex)
    }

    private fun changeAction(action: DogAction) {
        actionHistory.add(0, action)
        while (actionHistory.size > 1) {
            actionHistory.removeAt(actionHistory.size - 1)
        }

        currentAction.value = action
        val randomSecond = Random.nextInt(5, 10)
        actionDelay.value = randomSecond * 1000L
    }

    fun onDirection(type: Direction.Type) {
        currentDirection.value = directions.find { it.type == type }
        directionDelay.value = DIRECTION_TIME
        directTime = System.currentTimeMillis()
        isDirectionRewarded = false
        isActionFromDirection = false
    }

    fun updateTime(time: Long) {
        directionDelay.value = max((directionDelay.value ?: 0) - time, 0L)
        if (directionDelay.value == 0L) {
            onFinishDirection()
        }

        actionDelay.value = max((actionDelay.value ?: 0) - time, 0L)

        if (actionDelay.value == 0L) {
            onFinishAction()
        }
    }

    fun onFinishDirection() {
        if (!isDirectionRewarded) {
            onNoRewardFromDirection()
        }
    }

    fun onReceiveReward() {
        if (currentAction.value?.type == DogAction.Type.EAT) {
            return
        }

        if (isActionFromDirection) {
            if (!isDirectionRewarded) {
                rememberCurrentDirection()
                isDirectionRewarded = true
            }
        } else {
            likeCurrentAction()
        }

        currentAction.value = DogAction(DogAction.Type.EAT)
    }

    private fun onFinishAction() {
        Log.d("dog", "onFinishActionWithoutReward")
        if (isActionFromDirection) {
            if (!isDirectionRewarded) {
                onNoRewardFromDirection()
            }
        } else {
            currentAction.value?.weightDown(ACTION_DOWN_WEIGHT)
            actions.forEach {
                it.weightDown(ACTION_DOWN_WEIGHT)
            }
        }

        getNextAction()?.also { changeAction(it) }
    }


    private fun onNoRewardFromDirection() {
        currentDirection?.value?.also { direction ->
            currentAction?.value?.also { action ->
                direction.weightDown(action.type, DIRECTION_ACTION_DOWN_WEIGHT)
                Log.d("dog", "onNoRewardFromDirection")
            }
        }
    }

    private fun likeCurrentAction() {
        val weightBonus = arrayOf(3, 3, 1, 0, 0)
        currentAction.value?.apply {
            weightUp((actionDelay.value ?: 0).toInt() / 2000)
        }
        actionHistory.forEachIndexed { index, dogAction ->
            actions.find {
                it.type == dogAction.type
            }?.apply {
                weightUp(weightBonus[index])
            }
        }

        Log.d("dog", "likeCurrentAction")
    }

    private fun rememberCurrentDirection() {
        currentDirection.value?.also {
            weightUpOnDirection(it, Random.nextInt(10))
        }

        Log.d("dog", "rememberCurrentDirection")
    }

    private fun weightUpOnDirection(direction: Direction, weight: Int) {
        currentAction.value?.also { action ->
            direction.weightUp(action.type, weight)
        }
    }

    private fun isDirectionExpired(): Boolean {
        return System.currentTimeMillis() - directTime > DIRECTION_TIME
    }
}