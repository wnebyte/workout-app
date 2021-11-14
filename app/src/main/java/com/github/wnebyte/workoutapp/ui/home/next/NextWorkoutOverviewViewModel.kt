package com.github.wnebyte.workoutapp.ui.home.next

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.home.AbstractWorkoutOverviewViewModel

class NextWorkoutOverviewViewModel: AbstractWorkoutOverviewViewModel() {

    private val repository = Repository.get()

    override val workoutLiveData: LiveData<WorkoutWithExercises?> = (
            repository.getNextWorkoutWithExercises()
            )
        .distinctUntilChanged()
}