package com.github.wnebyte.workoutapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

abstract class AbstractWorkoutOverviewViewModel: ViewModel() {

    abstract val workoutLiveData: LiveData<WorkoutWithExercises?>
}