package com.github.wnebyte.workoutapp.database.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(
    tableName = "Exercise",
    columnName = "timer"
)
class TableExerciseDelColumnTimerMigration : AutoMigrationSpec {

}