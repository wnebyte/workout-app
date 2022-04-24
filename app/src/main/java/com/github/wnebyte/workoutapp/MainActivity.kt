package com.github.wnebyte.workoutapp

import java.util.*
import java.io.Serializable
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.navigation.NavController
import androidx.navigation.NavHost
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
    ProgressFragment.Callbacks {
    lateinit var appBarConfiguration: AppBarConfiguration

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        Log.i(TAG, "navController: $navController")
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.i(TAG, "navigating to dest: $destination")
            if (destination.id == R.id.nav_workout_list && navView.checkedItem == null) {
                val menuItem = navView.menu.findItem(R.id.workout_navigation)
                navView.setCheckedItem(menuItem)
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_progress, R.id.nav_exercise_list, R.id.nav_workout_list
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_progress, R.id.nav_exercise_list, R.id.nav_workout_list
            ),
            drawerLayout
        )
    }

    fun setupActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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