package com.github.wnebyte.workoutapp.ui.exercisedetails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditFragment

class ExerciseDetailsFragment: AbstractExerciseEditFragment() {

    override val TAG = "ExerciseDetailsFragment"

    override val vm: ExerciseDetailsViewModel by viewModels()

    private val args: ExerciseDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = ExerciseWithSets.newInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBar.root.visibility = View.GONE
        vm.loadExercise(args.exerciseId)
    }

    override fun onStop() {
        super.onStop()
        if (exercise.hashCode() != hashCode) {
            vm.saveExercise(exercise)
        }
        if (removedItems.isNotEmpty()) {
            vm.deleteSets(removedItems)
        }
    }
}