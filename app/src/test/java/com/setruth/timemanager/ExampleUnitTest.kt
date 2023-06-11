package com.setruth.timemanager

import org.junit.Test
import java.time.LocalDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        LocalDateTime.now().apply {
//            println(year)
//            println(monthValue)
//            println(dayOfMonth)
            println(dayOfWeek.value.toString())
            println(hour)
            println(minute)
            println(second)
        }
    }
}