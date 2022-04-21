package com.github.wnebyte.workoutapp

import java.util.*
import java.util.concurrent.Executors
import java.lang.Exception
import android.net.Uri
import android.util.Log
import android.database.Cursor
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.preference.PreferenceManager
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toBoolean
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.Exercise
import com.github.wnebyte.workoutapp.model.Workout

private const val TAG = "WorkoutProApplication"
private const val PREF_FRESH_INSTALL = "FreshInstall"

class WorkoutProApplication : WorkoutApplication() {

    override fun onCreate() {
        super.onCreate()
        if (isFreshInstall()) {
            val executor = Executors.newSingleThreadExecutor()
            executor.submit {
                try {
                    val baseUri = Uri.Builder()
                        .scheme("content")
                        .authority("com.github.wnebyte.workoutapp.free.provider")
                        .build()
                    val repository = Repository.get()
                    // workouts
                    var uri = Uri.withAppendedPath(baseUri, "workout")
                    var cursor = contentResolver.query(uri, null,
                        null, null, null)
                    cursor?.let {
                        if (it.count > 0) {
                            val workouts = toWorkouts(it)
                            repository.saveWorkout(workouts)
                            Log.i(TAG, "saved: ${it.count} workouts")
                        }
                    }
                    cursor?.close()
                    // exercises
                    uri = Uri.withAppendedPath(baseUri, "exercise")
                    cursor = contentResolver.query(uri, null,
                        null, null, null)
                    cursor?.let {
                        if (it.count > 0) {
                            val exercises = toExercises(it)
                            repository.saveExercise(exercises)
                            Log.i(TAG, "saved: ${it.count} exercises")
                        }
                    }
                    cursor?.close()
                    // sets
                    uri = Uri.withAppendedPath(baseUri, "set")
                    cursor = contentResolver.query(uri, null,
                        null, null, null)
                    cursor?.let {
                        if (it.count > 0) {
                            val sets = toSets(it)
                            repository.saveSet(sets)
                            Log.i(TAG, "saved: ${it.count} sets")
                        }
                    }
                    cursor?.close()

                } catch (e: Exception) {
                    Log.i(TAG, "${e.message}")
                }
            }
            write()
        }
    }

    private fun toSets(c: Cursor): List<Set> {
        val sets: MutableList<Set> = mutableListOf()

        while (c.moveToNext()) {
            // id
            var index = c.getColumnIndexOrThrow("id")
            val id = UUID.fromString(c.getString(index))
            // weights
            index = c.getColumnIndexOrThrow("weights")
            val weights = c.getDouble(index)
            // reps
            index = c.getColumnIndexOrThrow("reps")
            val reps = c.getInt(index)
            // completed
            index = c.getColumnIndexOrThrow("completed")
            val completed = c.getInt(index).toBoolean()
            // exercise
            index = c.getColumnIndexOrThrow("exercise")
            val exercise = UUID.fromString(c.getString(index))
            val set = Set(id, weights, reps, completed, exercise)
            sets.add(set)
        }

        return sets
    }

    private fun toExercises(c: Cursor): List<Exercise> {
        val exercises: MutableList<Exercise> = mutableListOf()

        while (c.moveToNext()) {
            // id
            var index = c.getColumnIndexOrThrow("id")
            val id = UUID.fromString(c.getString(index))
            // name
            index = c.getColumnIndexOrThrow("name")
            val name = c.getString(index)
            // completed
            index = c.getColumnIndexOrThrow("completed")
            val completed = c.getInt(index).toBoolean()
            // workout
            index = c.getColumnIndexOrThrow("workout")
            val workout = c.getStringOrNull(index)?.let {
                return@let UUID.fromString(it)
            }
            val exercise = Exercise(id, name, completed, workout)
            exercises.add(exercise)
        }

        return exercises
    }

    private fun toWorkouts(c: Cursor): List<Workout> {
        val workouts: MutableList<Workout> = mutableListOf()

        while (c.moveToNext()) {
            // id
            var index = c.getColumnIndexOrThrow("id")
            val id = UUID.fromString(c.getString(index))
            // name
            index = c.getColumnIndexOrThrow("name")
            val name = c.getString(index)
            // date
            index = c.getColumnIndexOrThrow("date")
            val date = c.getLongOrNull(index)?.let {
                return@let Date(it)
            }
            // reminder
            index = c.getColumnIndexOrThrow("reminder")
            val reminder = c.getLongOrNull(index)
            // completed
            index = c.getColumnIndexOrThrow("completed")
            val completed = c.getInt(index).toBoolean()
            val workout = Workout(id, name, date, reminder, completed)
            workouts.add(workout)
        }

        return workouts
    }

    private fun isFreshInstall(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val b = prefs.getBoolean(PREF_FRESH_INSTALL, true)
        return b
    }

    private fun write() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(PREF_FRESH_INSTALL, false)
            .apply()
    }
}