package com.github.wnebyte.workoutapp.database

import androidx.room.*
import androidx.room.Database
import com.github.wnebyte.workoutapp.database.dao.*
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.model.Set

@Database(
    entities = [ Set::class, Exercise::class, Workout::class ],
    version = 1,
    autoMigrations = [
     //   AutoMigration(from = 1, to = 2),
     //   AutoMigration(from = 2, to = 3, spec = TableExerciseDelAttrTimerMigration::class),
     //   AutoMigration(from = 3, to = 4, spec = TableExerciseDelAttrMillisMigration::class)
    ],
    exportSchema = true
)
@TypeConverters(TypeConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun setDao() : SetDao

    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutDao(): WorkoutDao

    abstract fun exerciseWithSetsDao(): ExerciseWithSetsDao

    abstract fun workoutWithExercisesDao(): WorkoutWithExercisesDao
}