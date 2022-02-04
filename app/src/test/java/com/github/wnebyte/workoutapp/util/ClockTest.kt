package com.github.wnebyte.workoutapp.util

import com.github.wnebyte.workoutapp.util.Clock
import org.junit.Assert
import org.junit.Test

class ClockTest {

    @Test
    fun test00() {
        Assert.assertEquals("00:00.1", Clock.formatSeconds(1L))
        Assert.assertEquals("00:02.5", Clock.formatSeconds(125))
        Assert.assertEquals("00:00.1", Clock.formatMillis(1000))
        Assert.assertEquals("00:02.5", Clock.formatMillis(125000))
    }

    @Test
    fun test01() {
        Assert.assertEquals("00:01.2", Clock.formatMMSSMS(1200))
        Assert.assertEquals("00:03.4", Clock.formatMMSSMS(3420))
    }
}