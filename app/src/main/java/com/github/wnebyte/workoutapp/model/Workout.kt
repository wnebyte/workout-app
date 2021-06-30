package com.github.wnebyte.workoutapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Session::class, parentColumns = arrayOf("id"), childColumns = arrayOf("session"),
        onDelete = ForeignKey.CASCADE)
    ])
data class Workout(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var completed: Boolean = false,
    @ColumnInfo(index = true)
    val session: UUID? = null
) {
    companion object {
        fun newInstance(): Workout =
            Workout(name = Workout::class.java.simpleName)
    }
}