package com.github.wnebyte.workoutapp.model

import java.util.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Workout::class, parentColumns = arrayOf("id"), childColumns = arrayOf("workout"),
        onDelete = ForeignKey.CASCADE)
    ])
data class Exercise(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var completed: Boolean = false,
    @ColumnInfo(index = true)
    val workout: UUID? = null
) {
    companion object {

        /**
         * Returns a new [Exercise] instance with default values and the specified fk.
         * @param workout the fk to associate with the new instance
         * @return a new instance
         */
        fun newInstance(workout: UUID? = null): Exercise =
            Exercise(
                name = Exercise::class.java.simpleName, workout = workout
            )

        /**
         * Returns a copy of the specified [Exercise] but with a new id and with the specified
         * fk.
         * @param exercise the Exercise to be copied
         * @param workout the fk to associate with the copy
         * @return a copy of the specified Exercise
         */
        fun copyOf(exercise: Exercise, workout: UUID): Exercise =
            Exercise(
                name = exercise.name, workout = workout
            )
    }
}