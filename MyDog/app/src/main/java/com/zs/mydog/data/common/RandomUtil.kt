package com.zs.mydog.data.common

import kotlin.random.Random

object RandomUtil {
    fun rand(min: Double, max: Double): Double {
        return Math.random() * (max - min) + min
    }

    fun isOver(value: Double): Boolean {
        return Math.random() > value
    }

    fun isUnder(value: Double): Boolean {
        return Math.random() < value
    }

    fun weightedIndex(weights: Array<Int>): Int {
        var sum = 0L
        weights.forEach { sum += it }
        if (sum == 0L) {
            return Random.nextInt(weights.size)
        }

        var rand = Random.nextLong(sum)
        weights.forEachIndexed { index, i ->
            if (rand <= i) {
                return index
            }
            rand -= i
        }
        return Random.nextInt(weights.size)
    }
}