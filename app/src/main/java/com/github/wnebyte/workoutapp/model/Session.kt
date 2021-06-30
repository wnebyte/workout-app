package com.github.wnebyte.workoutapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Session(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var date: Date? = null,
    var completed: Boolean = false
) {
    companion object {
        fun newInstance(): Session =
            Session()
    }
}