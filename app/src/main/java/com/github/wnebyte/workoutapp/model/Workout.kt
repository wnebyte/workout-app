package com.github.wnebyte.workoutapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Workout(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var date: Date? = null,
    var reminder: Long? = null,
    var completed: Boolean = false
) {
    companion object {
        fun newInstance(): Workout =
            Workout(name = Workout::class.java.simpleName)
    }
}