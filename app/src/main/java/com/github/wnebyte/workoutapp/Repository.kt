package com.github.wnebyte.workoutapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.github.wnebyte.workoutapp.database.Database
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.model.Set
import java.util.*
import java.util.concurrent.Executors

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

    fun getExerciseWithSets(id: UUID): LiveData<ExerciseWithSets?> =
        database.exerciseWithSetsDao().get(id)

    fun getExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getAll()

    fun getTemplateExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getTemplates()

    fun getOrderedTemplateExercisesWithSets(): LiveData<List<ExerciseWithSets>> =
        database.exerciseWithSetsDao().getOrderedTemplates()

    fun getWorkoutWithExercises(id: UUID): LiveData<WorkoutWithExercises?> =
        database.workoutWithExercisesDao().get(id)

    fun getWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getAll()

    fun getTemplateWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getTemplates()

    fun getNonCompletedWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>> =
        database.workoutWithExercisesDao().getNonCompleted()

    fun saveSession(session: Session) =
        executor.execute {
            database.sessionDao().save(session)
        }

    fun saveSession(vararg session: Session) =
        executor.execute {
            database.sessionDao().save(*session)
        }

    fun saveSession(session: List<Session>) =
        executor.execute {
            database.sessionDao().save(*session.toTypedArray())
        }

    fun deleteSession(session: Session) =
        executor.execute {
            database.sessionDao().delete(session)
        }

    fun deleteSession(vararg session: Session) =
        executor.execute {
            database.sessionDao().delete(*session)
        }

    fun deleteSession(session: List<Session>) =
        executor.execute {
            database.sessionDao().delete(*session.toTypedArray())
        }

    fun getSession(id: UUID): LiveData<Session?> =
        database.sessionDao().get(id)

    fun getSessions(): LiveData<List<Session>> =
        database.sessionDao().getAll()

    fun getOrderedSessions(): LiveData<List<Session>> =
        database.sessionDao().getOrdered()

    fun getSessionWithWorkout(id: UUID): LiveData<SessionWithWorkout?> =
        database.sessionWithWorkoutDao().get(id)

    fun getSessionsWithWorkout(): LiveData<List<SessionWithWorkout>> =
        database.sessionWithWorkoutDao().getAll()

    fun getCompletedSessionsWithWorkout(): LiveData<List<SessionWithWorkout>> =
        database.sessionWithWorkoutDao().getCompleted()

    fun getNonCompletedSessionsWithWorkout(): LiveData<List<SessionWithWorkout>> =
        database.sessionWithWorkoutDao().getNonCompleted()

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

    fun deleteAllSessions() =
        executor.execute {
            database.sessionDao().deleteAll()
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