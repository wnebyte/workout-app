package com.github.wnebyte.workoutapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.model.Set
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

private const val DATABASE_NAME = "workout-database"

class Repository private constructor(context: Context) {

    private val database: Database = Room.databaseBuilder(
        context.applicationContext,
        Database::class.java,
        DATABASE_NAME
    ).build()

    private val executor = Executors.newSingleThreadExecutor()

    fun saveSet(set: Set) =
        executor.execute {
            database.setDao().save(set)
        }

    fun saveSet(vararg set: Set) =
        executor.execute {
            database.setDao().save(*set)
        }

    fun saveSet(sets: List<Set>) =
        executor.execute {
            database.setDao().save(*sets.toTypedArray())
        }

    fun updateSet(set: Set) =
        executor.execute {
            database.setDao().update(set)
        }

    fun updateSet(vararg set: Set) =
        executor.execute {
            database.setDao().update(*set)
        }

    fun updateSet(sets: List<Set>) =
        executor.execute {
            database.setDao().update(*sets.toTypedArray())
        }

    fun deleteSet(set: Set) =
        executor.execute {
            database.setDao().delete(set)
        }

    fun deleteSet(vararg set: Set) =
        executor.execute {
            database.setDao().delete(*set)
        }

    fun deleteSet(sets: List<Set>) =
        executor.execute {
            database.setDao().delete(*sets.toTypedArray())
        }

    fun getSet(id: UUID): LiveData<Set?> =
        database.setDao().get(id)

    fun getSets(): LiveData<List<Set>> =
        database.setDao().getAll()

    fun saveExercise(exercise: Exercise) =
        executor.execute {
            database.exerciseDao().save(exercise)
        }

    fun saveExercise(vararg exercise: Exercise) =
        executor.execute {
            database.exerciseDao().save(*exercise)
        }

    fun saveExercise(exercises: List<Exercise>) =
        executor.execute {
            database.exerciseDao().save(*exercises.toTypedArray())
        }

    fun updateExercise(exercise: Exercise) =
        executor.execute {
          database.exerciseDao().update(exercise)
        }

    fun updateExercise(vararg exercise: Exercise) =
        executor.execute {
            database.exerciseDao().update(*exercise)
        }

    fun updateExercise(exercises: List<Exercise>) =
        executor.execute {
            database.exerciseDao().update(*exercises.toTypedArray())
        }

    fun deleteExercise(exercise: Exercise) =
        executor.execute {
            database.exerciseDao().delete(exercise)
        }

    fun deleteExercise(vararg exercise: Exercise) =
        executor.execute {
            database.exerciseDao().delete(*exercise)
        }

    fun deleteExercise(exercises: List<Exercise>) =
        executor.execute {
            database.exerciseDao().delete(*exercises.toTypedArray())
        }

    fun getExercise(id: UUID): LiveData<Exercise?> =
        database.exerciseDao().get(id)

    fun getExercises(): LiveData<List<Exercise>> =
        database.exerciseDao().getAll()

    fun getTemplateExercises(): LiveData<List<Exercise>> =
        database.exerciseDao().getTemplates()

    fun getCompletedExercises(): LiveData<List<Exercise>> =
        database.exerciseDao().getCompleted()

    fun getNonCompletedExercises(): LiveData<List<Exercise>> =
        database.exerciseDao().getNonCompleted()

    fun saveWorkout(workout: Workout) =
        executor.execute {
            database.workoutDao().save(workout)
        }

    fun saveWorkout(vararg workout: Workout) =
        executor.execute {
            database.workoutDao().save(*workout)
        }

    fun saveWorkout(workouts: List<Workout>) =
        executor.execute {
            database.workoutDao().save(*workouts.toTypedArray())
        }

    fun saveWorkoutAndGetCount(workout: Workout): Future<Long> =
        executor.submit(Callable {
            database.workoutDao().saveAndGetCount(workout)
        })

    fun deleteWorkout(workout: Workout) =
        executor.execute {
            database.workoutDao().delete(workout)
        }

    fun deleteWorkout(vararg workout: Workout) =
        executor.execute {
            database.workoutDao().delete(*workout)
        }

    fun deleteWorkout(workouts: List<Workout>) =
        executor.execute {
            database.workoutDao().delete(*workouts.toTypedArray())
        }

    fun deleteWorkout(workout: WorkoutWithExercises) =
        executor.execute {
            database.workoutDao().delete(workout.workout)
            workout.exercises.forEach { exercise ->
                database.exerciseDao().delete(exercise.exercise)
                database.setDao().delete(*exercise.sets.toTypedArray())
            }
        }

    fun getWorkout(id: UUID): LiveData<Workout?> =
        database.workoutDao().get(id)

    fun getWorkouts(): LiveData<List<Workout>> =
        database.workoutDao().getAll()

    fun getWorkoutsOrderByDate(asc: Boolean = true): LiveData<List<Workout>> =
        database.workoutDao().getAllOrderByDate(asc)

    fun getCompletedWorkouts(): LiveData<List<Workout>> =
        database.workoutDao().getCompleted()

    fun getNonCompletedWorkoutsOrderByDate(asc: Boolean = true): LiveData<List<Workout>> =
        database.workoutDao().getNonCompletedOrderByDate(asc)

    fun getWorkoutCount(): Future<Long> =
        executor.submit(Callable {
            database.workoutDao().getCount()
        })

    fun getExerciseWithSets(id: UUID): LiveData<ExerciseWithSets?> =
        database.exerciseWithSetsDao().get(id)

    fun getExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getAll()

    fun getTemplateExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getTemplates()

    fun getOrderedTemplateExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getTemplatesOrderByName()

    fun getWorkoutWithExercises(id: UUID): LiveData<WorkoutWithExercises?> =
        database.workoutWithExercisesDao().get(id)

    fun getWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getAll()

    fun getWorkoutsWithExercisesOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getAllOrderByDate(asc)

    fun getNonCompletedWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getNonCompleted()

    fun getNonCompletedWorkoutsWithExercisesOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getNonCompletedOrderByDate(asc)

    fun getCompletedWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getCompleted()

    fun getCompletedWorkoutsWithExercisesOrderByDate(asc: Boolean = true): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getCompletedOrderByDate(asc)

    fun getMostRecentlyCompletedWorkoutWithExercises(): LiveData<WorkoutWithExercises?> =
        database.workoutWithExercisesDao().getMostRecentlyCompleted(Date().time)

    fun getWorkoutsWithExercisesCompletedBetween(from: Date, to: Date): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getCompletedBetween(from.time, to.time)

    fun getNextWorkoutWithExercises(): LiveData<WorkoutWithExercises?> =
        database.workoutWithExercisesDao().getNext(Date().time)

    fun getLastWorkoutWithExercises(): LiveData<WorkoutWithExercises?> =
        database.workoutWithExercisesDao().getLast(Date().time)

    fun getWorkoutsWithExercisesCompletedBetweenContainingExercise(
        exerciseName: String, from: Date, to: Date
    ): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao()
            .getCompletedBetweenContainingExercise(exerciseName, from.time, to.time)

    fun deleteAllSets() =
        executor.execute {
            database.setDao().deleteAll()
        }

    fun deleteAllExercises() =
        executor.execute {
            database.exerciseDao().deleteAll()
        }

    fun deleteAllWorkouts() =
        executor.execute {
            database.workoutDao().deleteAll()
        }

    companion object {
        private var INSTANCE: Repository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository {
            return INSTANCE
                ?: throw IllegalStateException("Repository must be initialized")
        }
    }
}