package com.github.wnebyte.workoutapp.util

interface ITemporalRange {

    fun adjustUp(): ITemporalRange

    fun adjustDown(): ITemporalRange
}