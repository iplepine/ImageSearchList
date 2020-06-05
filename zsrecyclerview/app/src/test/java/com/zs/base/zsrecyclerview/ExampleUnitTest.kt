package com.zs.base.zsrecyclerview

import org.junit.Test
import java.lang.ref.WeakReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var text: TestClass? = TestClass()
        val weakReference = WeakReference(text)

        var weakref1 = weakReference.get()

        text = null
        System.gc()
        println(weakref1)
    }

    class TestClass() {
        var text: String = "aasdf"

        override fun toString(): String {
            return text
        }
    }
}
