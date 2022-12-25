package com.github.wnebyte.workoutapp

import android.annotation.SuppressLint
import android.app.ActionBar
import java.util.*
import java.io.Serializable
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.gui.workout.host.WorkoutHostFragment
import com.google.android.material.navigation.NavigationView
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditFragment
import com.github.wnebyte.workoutapp.ui.exerciseimport.ExerciseImportFragment
import com.github.wnebyte.workoutapp.ui.exerciselist.ExerciseListFragment
import com.github.wnebyte.workoutapp.ui.exerciselist.ExerciseListFragmentDirections
import com.github.wnebyte.workoutapp.ui.progress.ProgressFragment
import com.github.wnebyte.workoutapp.ui.progress.ProgressFragmentDirections
import com.github.wnebyte.workoutapp.ui.workout.HostFragment
import com.github.wnebyte.workoutapp.ui.workout.session.SessionFragment
import com.github.wnebyte.workoutapp.ui.workoutcreate.WorkoutCreateFragment
import com.github.wnebyte.workoutapp.ui.workoutcreate.WorkoutCreateFragmentDirections
import com.github.wnebyte.workoutapp.ui.workoutdetails.WorkoutDetailsFragment
import com.github.wnebyte.workoutapp.ui.workoutdetails.WorkoutDetailsFragmentDirections
import com.github.wnebyte.workoutapp.ui.workoutlist.WorkoutListFragment
import com.github.wnebyte.workoutapp.ui.workoutlist.WorkoutListFragmentDirections

private const val TAG = "MainActivity"

open class MainActivity : AppCompatActivity(),
    ExerciseListFragment.Callbacks,
    ExerciseImportFragment.Callbacks,
    AbstractExerciseEditFragment.Callbacks,
    WorkoutListFragment.Callbacks,
    WorkoutDetailsFragment.Callbacks,
    WorkoutCreateFragment.Callbacks,
    SessionFragment.Callbacks,
    ProgressFragment.Callbacks,
    com.github.wnebyte.workoutapp.gui.workout.list.WorkoutListFragment.Callbacks,
    com.github.wnebyte.workoutapp.gui.workout.host.WorkoutHostFragment.Callbacks,
    com.github.wnebyte.workoutapp.gui.exercise.list.ExerciseListFragment.Callbacks,
    com.github.wnebyte.workoutapp.gui.exercise.host.ExerciseHostFragment.Callbacks {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_workout_list
            ),
            drawerLayout
        )
    }

    @SuppressLint("RestrictedApi")
    fun setupActionBar(toolbar: Toolbar) {
        Log.i(TAG, "setupActionBar()")
        setSupportActionBar(toolbar)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val home = menu.findItem(android.R.id.home)
        Log.i(TAG, "home: $home")
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onFinished() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.popBackStack()
    }

    /*
    * ExerciseListFragment
    */
    override fun onEditExercise(exerciseId: UUID, currentFragment: Class<out Fragment>) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = when (currentFragment) {
            ExerciseListFragment::class.java -> {
                ExerciseListFragmentDirections
                    .actionNavExerciseListToNavExerciseDetails(exerciseId)
            }
            WorkoutDetailsFragment::class.java -> {
                WorkoutDetailsFragmentDirections
                    .actionNavWorkoutDetailsToNavExerciseDetails(exerciseId)
            }
            WorkoutCreateFragment::class.java -> {
                WorkoutCreateFragmentDirections
                    .actionNavWorkoutCreateToNavExerciseDetails(exerciseId)
            }
            com.github.wnebyte.workoutapp.gui.exercise.list.ExerciseListFragment::class.java -> {
                com.github.wnebyte.workoutapp.gui.exercise.list.ExerciseListFragmentDirections
                    .actionNavExerciseListToNavExerciseHost(exerciseId)
            }
            else -> {
                throw IllegalStateException(
                    "The specified currentFragment is not supported."
                )
            }
        }
        navController.navigate(action)
    }

    override fun onCreateExercise() {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = ExerciseListFragmentDirections
            .actionNavExerciseListToNavExerciseCreate(null)
        navController.navigate(action)
    }

    /*
    * WorkoutCreateFragment / WorkoutDetailsFragment
    */
    override fun onImportExercise(workoutId: UUID, currentFragment: Class<out Fragment>) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = when (currentFragment) {
            WorkoutCreateFragment::class.java -> {
                WorkoutCreateFragmentDirections
                    .actionNavWorkoutCreateToNavExerciseImport(workoutId)
            }
            WorkoutDetailsFragment::class.java -> {
                WorkoutDetailsFragmentDirections
                    .actionNavWorkoutDetailsToNavExerciseImport(workoutId)
            }
            else -> {
                throw IllegalStateException(
                    "The specified currentFragment is not supported."
                )
            }
        }
        navController.navigate(action)
    }

    override fun onCreateExercise(workoutId: UUID, currentFragment: Class<out Fragment>) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = when (currentFragment) {
            WorkoutCreateFragment::class.java -> {
                WorkoutCreateFragmentDirections
                    .actionNavWorkoutCreateToNavExerciseCreate(workoutId)
            }
            WorkoutDetailsFragment::class.java -> {
                WorkoutDetailsFragmentDirections
                    .actionNavWorkoutDetailsToNavExerciseCreate(workoutId)
            }
            else -> {
                throw IllegalStateException(
                    "The specified currentFragment is not supported."
                )
            }
        }
        navController.navigate(action)
    }

    /*
    * WorkoutListFragment
    */
    override fun onEditWorkout(workoutId: UUID, currentFragment: Class<out Fragment>) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = when (currentFragment) {
            WorkoutListFragment::class.java -> {
                WorkoutListFragmentDirections
                    .actionNavWorkoutListToNavWorkoutDetails(workoutId)
            }
            com.github.wnebyte.workoutapp.gui.workout.list.WorkoutListFragment::class.java -> {
                com.github.wnebyte.workoutapp.gui.workout.list.WorkoutListFragmentDirections
                    .actionNavWorkoutListToNavWorkoutHost(workoutId, false)
            }
            else -> {
                throw IllegalStateException(
                    "The specified currentFragment is not supported"
                )
            }
        }
        navController.navigate(action)
    }

    override fun onEditCompletedWorkout(workoutId: UUID, currentFragment: Class<out Fragment>) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = when (currentFragment) {
            WorkoutListFragment::class.java -> {
                WorkoutListFragmentDirections
                    .actionNavWorkoutListToNavWorkoutDetailsFinal(workoutId)
            }
            else -> {
                throw IllegalStateException(
                    "The specified currentFragment is not supported"
                )
            }
        }
        navController.navigate(action)
    }

    override fun onCreateWorkout() {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = WorkoutListFragmentDirections
            .actionNavWorkoutListToNavWorkoutCreate()
        navController.navigate(action)
    }

    override fun onWorkout(workoutId: UUID) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = WorkoutListFragmentDirections
            .actionNavWorkoutListToNavWorkoutViewPager(workoutId)
        navController.navigate(action)
    }

    override fun onProgressDetails(progressItem: ProgressItem) {
        val navController = findNavController(R.id.nav_host_fragment)
        val action = ProgressFragmentDirections
            .actionNavProgressToNavProgressDetails(progressItem)
        navController.navigate(action)
    }

    companion object {
        fun newPendingWorkoutIntent(context: Context, workoutId: UUID): PendingIntent =
            NavDeepLinkBuilder(context)
                // .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.nav_workout_view_pager)
                .setArguments(Bundle().apply {
                    putSerializable(HostFragment.WORKOUT_ID_KEY, workoutId as Serializable)
                    putBoolean(HostFragment.PENDING_INTENT_KEY, true)
                })
                .createPendingIntent()
    }
}