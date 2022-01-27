package com.github.wnebyte.workoutapp.ui.workoutlist

enum class Filter {
    ALL,
    COMPLETED,
    UNCOMPLETED;

    fun toInt(): Int {
        return ordinal
    }

    companion object {

        fun parse(int: Int): Filter {
            return values()[int]
        }
    }
}