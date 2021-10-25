package com.github.wnebyte.workoutapp

import com.github.wnebyte.workoutapp.util.Clock
import org.junit.Assert
import org.junit.Test

class ClockUnitTest {

    @Test
    fun test00() {
        Assert.assertEquals("00:00.1", Clock.formatSeconds(1L))
        Assert.assertEquals("00:02.5", Clock.formatSeconds(125))
        Assert.assertEquals("00:00.1", Clock.formatMillis(1000))
        Assert.assertEquals("00:02.5", Clock.formatMillis(125000))
    }
}