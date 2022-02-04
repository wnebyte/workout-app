package com.github.wnebyte.workoutapp.util

interface ITemporalAdjuster<T> {

    fun setMaximum(vararg fields: Int): ITemporalAdjuster<T>

    fun setMinimum(vararg fields: Int): ITemporalAdjuster<T>

    fun add(field: Int, amount: Int): ITemporalAdjuster<T>

    fun subtract(field: Int, amount: Int): ITemporalAdjuster<T>

    fun adjust(): T

}