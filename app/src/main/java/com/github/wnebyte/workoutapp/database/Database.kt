package com.github.wnebyte.workoutapp.database

import androidx.room.*
import androidx.room.Database
import androidx.room.migration.AutoMigrationSpec
import com.github.wnebyte.workoutapp.database.dao.*
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.model.Set

@Database(
    entities = [ Set::class, Exercise::class, Workout::class ],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun setDao() : SetDao

    abstract fun exerciseDao(): ExerciseDao

    abstract fun workoutDao(): WorkoutDao

    abstract fun exerciseWithSetsDao(): ExerciseWithSetsDao

    abstract fun workoutWithExercisesDao(): WorkoutWithExercisesDao
}