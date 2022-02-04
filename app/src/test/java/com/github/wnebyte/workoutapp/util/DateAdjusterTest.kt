package com.github.wnebyte.workoutapp.util

import java.util.*
import org.junit.Test
import org.junit.Assert
import com.github.wnebyte.workoutapp.util.Extensions.Companion.date
import com.github.wnebyte.workoutapp.util.Extensions.Companion.hour
import com.github.wnebyte.workoutapp.util.Extensions.Companion.minute
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.second
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year

class DateAdjusterTest {

    @Test
    fun test00() {
        // 2022/01/01 00:00:00
        val ref = getRef()
        Assert.assertEquals(2022, ref.year())
        Assert.assertEquals(0, ref.month())
        Assert.assertEquals(1, ref.date())
        Assert.assertEquals(0, ref.hour())
        Assert.assertEquals(0, ref.minute())
        Assert.assertEquals(0, ref.second())
    }

    @Test
    fun test01() {
        val ref = getRef()
        val adjuster: ITemporalAdjuster<Date> = DateAdjuster(ref)
        var date: Date = adjuster
            .add(Calendar.DATE, 5)
            .adjust()
        Assert.assertEquals(6, date.date())
        date = DateAdjuster(ref)
            .add(Calendar.YEAR, 1)
            .adjust()
        Assert.assertEquals(2023, date.year())
        date = DateAdjuster(ref)
            .subtract(Calendar.YEAR, -2)
            .adjust()
        Assert.assertEquals(2020, date.year())
        date = DateAdjuster(ref)
            .add(Calendar.MONTH, 1)
            .setMaximum(Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND)
            .adjust()
        Assert.assertEquals(1, date.month())
        Assert.assertEquals(23, date.hour())
        Assert.assertEquals(59, date.minute())
        Assert.assertEquals(59, date.second())
    }

    @Test
    fun test02() {
        val ref = getRef()
        val adjuster: ITemporalAdjuster<Date> = DateAdjuster(ref)
        val date = adjuster
            .subtract(Calendar.YEAR, 1)
            .setMinimum(Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND)
            .adjust()
        Assert.assertEquals(2021, date.year())
        Assert.assertEquals(0, date.month())
        Assert.assertEquals(1, date.date())
        Assert.assertEquals(0, date.hour())
        Assert.assertEquals(0, date.minute())
        Assert.assertEquals(0, date.second())
    }

    @Test
    fun test03() {
        val ref = getRef()
        val adjuster: ITemporalAdjuster<Date> = DateAdjuster(ref)
        val date = adjuster
            .add(Calendar.YEAR, 1)
            .setMaximum(Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND)
            .adjust()
        Assert.assertEquals(2023, date.year())
        Assert.assertEquals(11, date.month())
        Assert.assertEquals(23, date.hour())
        Assert.assertEquals(59, date.minute())
        Assert.assertEquals(59, date.second())
    }

    private fun getRef(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(2022, 0, 1, 0, 0, 0)
        return calendar.time
    }
}