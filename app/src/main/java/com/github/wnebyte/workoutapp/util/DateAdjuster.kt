package com.github.wnebyte.workoutapp.util

import java.util.*
import kotlin.math.abs

class DateAdjuster(val date: Date) : ITemporalAdjuster<Date> {

    private val calendar: Calendar = Calendar.getInstance()

    init {
        calendar.time = date
    }

    override fun setMaximum(vararg fields: Int): ITemporalAdjuster<Date> {
        for (field in fields) {
            val max = calendar.getActualMaximum(field)
            calendar.set(field, max)
        }
        return this
    }

    override fun setMinimum(vararg fields: Int): ITemporalAdjuster<Date> {
        for (field in fields) {
            val min = calendar.getActualMinimum(field)
            calendar.set(field, min)
        }
        return this
    }

    override fun add(field: Int, amount: Int): ITemporalAdjuster<Date> {
        calendar.add(field, abs(amount))
        return this
    }

    override fun subtract(field: Int, amount: Int): ITemporalAdjuster<Date> {
        calendar.add(field, -1 * abs(amount))
        return this
    }

    override fun adjust(): Date {
        return calendar.time
    }
}