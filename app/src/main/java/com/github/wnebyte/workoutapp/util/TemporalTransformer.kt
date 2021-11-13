package com.github.wnebyte.workoutapp.util

interface TemporalTransformer<T> {

    fun getYear(t: T): Int

    fun getMonth(t: T): Int

    fun getDay(t: T): Int

    fun toLastDateOfThisMonth(t: T): T

    fun toLastDateOfLastMonth(t: T): T

    fun toLastDateOfNextMonth(t: T): T

    fun toFirstDateOfThisMonth(t: T): T

    fun toFirstDateOfLastMonth(t: T): T

    fun toFirstDateOfNextMonth(t: T): T
}