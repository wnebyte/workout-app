package com.github.wnebyte.workoutapp

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
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditFragment
import com.google.android.material.navigation.NavigationView
import com.github.wnebyte.workoutapp.ui.exerciseimport.ExerciseImportFragment
import com.github.wnebyte.workoutapp.ui.exerciselist.ExerciseListFragment
import com.github.wnebyte.workoutapp.ui.exerciselist.ExerciseListFragmentDirections
import com.github.wnebyte.workoutapp.ui.progress.ProgressFragment
import com.github.wnebyte.workoutapp.ui.progress.ProgressFragmentDirections
import com.github.wnebyte.workoutapp.ui.workout.ViewPagerFragment
import com.github.wnebyte.workoutapp.ui.workout.ViewPagerFragmentDirections
import com.github.wnebyte.workoutapp.ui.workout.session.SessionFragment
import com.github.wnebyte.workoutapp.ui.workoutcreate.WorkoutCreateFragment
import com.github.wnebyte.workoutapp.ui.workoutcreate.WorkoutCreateFragmentDirections
import com.github.wnebyte.workoutapp.ui.workoutdetails.WorkoutDetailsFragment
import com.github.wnebyte.workoutapp.ui.workoutdetails.WorkoutDetailsFragmentDirections
import com.github.wnebyte.workoutapp.ui.workoutlist.WorkoutListFragment
import com.github.wnebyte.workoutapp.ui.workoutlist.WorkoutListFragmentDirections

private const val TAG = "MainActivity"

class MainActivity: AppCompatActivity(),
    ExerciseListFragment.Callbacks,
    ExerciseImportFragment.Callbacks,
    AbstractExerciseEditFragment.Callbacks,
    WorkoutListFragment.Callbacks,
    WorkoutDetailsFragment.Callbacks,
    WorkoutCreateFragment.Callbacks,
    SessionFragment.Callbacks,
    ProgressFragment.Callbacks
{
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.i(TAG, "navigating to dest: $destination")
            if (destination.id == R.id.nav_workout_list && navView.checkedItem == null) {
                val menuItem = navView.menu.findItem(R.id.workout_navigation)
                navView.setCheckedItem(menuItem)
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_progress, R.id.nav_exercise_list, R.id.nav_workout_list),
            drawerLayout)
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
            SessionFragment::class.java -> {
                ViewPagerFragmentDirections
                    .actionNavViewPagerToNavWorkoutDetails(workoutId)
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
            SessionFragment::class.java -> {
                ViewPagerFragmentDirections
                    .actionNavViewPagerToNavWorkoutDetailsFinal(workoutId)
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
        // Todo: should route the user to their dest via WorkoutListFragment.
        fun newPendingWorkoutIntent(context: Context, workoutId: UUID): PendingIntent =
            NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.nav_workout_view_pager)
                .setArguments(Bundle().apply {
                    putSerializable(ViewPagerFragment.WORKOUT_ID_KEY, workoutId as Serializable)
                    putBoolean(ViewPagerFragment.PENDING_INTENT_KEY, true)
                })
                .createPendingIntent()
    }

    /*
    private fun delete() {
        val mainHandler = Handler(Looper.getMainLooper())
        val handlerThread = HandlerThread("HandlerThread").apply {
            start()
        }

        val backgroundHandler = Handler(handlerThread.looper) {
            val repo = Repository.get()
            repo.deleteAllSets()
            repo.deleteAllExercises()
            repo.deleteAllWorkouts()

            mainHandler.post {

            }
        }

        backgroundHandler.sendEmptyMessage(1)
    }

    private fun observe() {
        val mainHandler = Handler(Looper.getMainLooper())
        val handlerThread = HandlerThread("HandlerThread").apply {
            start()
        }

        val backgroundHandler = Handler(handlerThread.looper) {
            val repo = Repository.get()
            val all: LiveData<List<WorkoutWithExercises>> = repo.getWorkoutsWithExercises()

            mainHandler.post {
                all.observe(
                    this,
                    Observer { workouts ->
                        workouts?.let {
                            Log.i(TAG, workouts.joinToString("\n"))
                        }
                    }
                )
            }
        }

        backgroundHandler.sendEmptyMessage(1)
    }

    private fun insert() {
        val mainHandler = Handler(Looper.getMainLooper())
        val handlerThread = HandlerThread("HandlerThread").apply {
            start()
        }

        val backgroundHandler = Handler(handlerThread.looper) {
            val all: LiveData<List<WorkoutWithExercises>> = save()

            mainHandler.post {
                all.observe(
                    this,
                    Observer { workouts ->
                        workouts?.let {
                            Log.i(TAG, "$workouts")
                        }
                    }
                )
            }
        }

        backgroundHandler.sendEmptyMessage(1)
    }

    private fun save() : LiveData<List<WorkoutWithExercises>> {
        val workout = Workout(name = "My Workout", template = true)

        val exercise1 = Exercise(name = "Bench Press", timer = 180, template = true,  workout = workout.id)

        val set1 = Set(reps = 5, weights = 77.5, exercise = exercise1.id)
        val set2 = Set(reps = 5, weights = 80.0, exercise = exercise1.id)
        val set3 = Set(reps = 5, weights = 82.5, exercise = exercise1.id)
        val set4 = Set(reps = 5, weights = 85.0, exercise = exercise1.id)
        val set5 = Set(reps = 5, weights = 87.5, exercise = exercise1.id)

        val exercise2 = Exercise(name = "Barbell Row", timer = 180, template = true, workout = workout.id)

        val set6 = Set(reps = 7, weights = 55.0, exercise = exercise2.id)
        val set7 = Set(reps = 7, weights = 55.0, exercise = exercise2.id)
        val set8 = Set(reps = 7, weights = 55.0, exercise = exercise2.id)
        val set9 = Set(reps = 7, weights = 55.0, exercise = exercise2.id)
        val set10 = Set(reps = 7, weights = 55.0, exercise = exercise2.id)

        val exercise3 = Exercise(name = "Squat", timer = 150, template = true)

        val set11 = Set(reps = 8, weights = 80.0, exercise = exercise3.id)
        val set12 = Set(reps = 8, weights = 82.5, exercise = exercise3.id)
        val set13 = Set(reps = 8, weights = 85.0, exercise = exercise3.id)
        val set14 = Set(reps = 8, weights = 87.5, exercise = exercise3.id)
        val set15 = Set(reps = 8, weights = 88.0, exercise = exercise3.id)

        val repo = Repository.get()
        repo.saveWorkout(workout)
        repo.saveExercise(exercise1, exercise2)
        repo.saveSet(set1, set2, set3, set4, set5, set6, set7, set8, set9, set10)

        repo.saveExercise(exercise3)
        repo.saveSet(set11, set12, set13, set14, set15)

        return repo.getWorkoutsWithExercises()
    }
     */
}