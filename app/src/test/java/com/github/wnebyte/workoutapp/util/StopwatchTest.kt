package com.github.wnebyte.workoutapp.util

import org.junit.Assert
import org.junit.Test

class StopwatchTest {

    @Test
    fun test00() {
        Assert.assertEquals("00:00.1", Stopwatch.formatSeconds(1L))
        Assert.assertEquals("00:02.5", Stopwatch.formatSeconds(125))
        Assert.assertEquals("00:00.1", Stopwatch.formatMillis(1000))
        Assert.assertEquals("00:02.5", Stopwatch.formatMillis(125000))
    }

    @Test
    fun test01() {
        Assert.assertEquals("00:01.2", Stopwatch.formatMMSSMS(1200))
        Assert.assertEquals("00:03.4", Stopwatch.formatMMSSMS(3420))
    }
}