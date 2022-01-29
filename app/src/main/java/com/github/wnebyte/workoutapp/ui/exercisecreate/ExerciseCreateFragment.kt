package com.github.wnebyte.workoutapp.ui.exercisecreate

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditFragment

class ExerciseCreateFragment: AbstractExerciseEditFragment() {

    override val TAG = "ExerciseCreateFragment"

    override val vm: ExerciseCreateViewModel by viewModels()

    private val args: ExerciseCreateFragmentArgs by navArgs()

    private var saveExercise = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = ExerciseWithSets.newInstance(args.workoutId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBar.save.setOnClickListener {
            saveExercise = true
            callbacks?.onFinished()
        }
        binding.buttonBar.cancel.setOnClickListener {
            saveExercise = false
            callbacks?.onFinished()
        }
        vm.loadExercise(args.workoutId)
    }

    override fun onStop() {
        super.onStop()
        if (saveExercise) {
            if (exercise.hashCode() != hashCode) {
                vm.saveExercise(exercise)
            }
        } else {
            vm.deleteExercise(exercise)
        }
        if (removedItems.isNotEmpty()) {
            vm.deleteSets(removedItems)
        }
    }
}