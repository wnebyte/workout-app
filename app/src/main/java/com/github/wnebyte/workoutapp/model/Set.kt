package com.github.wnebyte.workoutapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Exercise::class, parentColumns = arrayOf("id"), childColumns = arrayOf("exercise"),
        onDelete = ForeignKey.CASCADE)
    ])
data class Set(
        @PrimaryKey
        val id: UUID = UUID.randomUUID(),
        var reps: Int,
        var weights: Double,
        var completed: Boolean = false,
        @ColumnInfo(index = true)
        val exercise: UUID
) {
    companion object {

        /**
         * Returns a new instance with 'default' values.
         * @param exercise the fk
         * @return a new set
         */
        fun newInstance(exercise: UUID): Set =
            Set(reps = 0, weights = 0.0, exercise = exercise)

        /**
         * Returns a copy of the specified Set but with a new id, and with the specified
         * fk.
         * @param set the set to be copied
         * @param exercise the fk to associate with the copy
         * @return a special copy of the specified set
         */
        fun copyOf(set: Set, exercise: UUID): Set =
            Set(reps = set.reps, weights = set.weights, exercise = exercise)

        /**
         * Returns a list of copies from the specified Sets, but with new id's and fk's.
         * @param sets the list of Sets to be copied
         * @param exercise the fk to associate with the copy's
         * @return a list of special copy's of the specified sets
         */
        fun copyOf(sets: MutableList<Set>, exercise: UUID): MutableList<Set> =
            sets.map { t -> copyOf(t, exercise) }
                .toMutableList()
    }
}