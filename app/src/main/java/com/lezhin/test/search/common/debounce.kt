package com.lezhin.test.search.common

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> LiveData<T>.debounce(delay: Long = 1000L) = MediatorLiveData<T>().also {
    val source = this
    val handler = Handler(Looper.getMainLooper())

    val runnable = Runnable {
        it.value = source.value
    }

    it.addSource(source) {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, delay)
    }
}