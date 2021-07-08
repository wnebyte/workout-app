package com.github.wnebyte.workoutapp.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.ExerciseBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

class ModelBinder(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val onClickListener: View.OnClickListener? = null
)
{
    inner class ExerciseHolder(private val binding: ExerciseBinding):
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets

            fun bind(exercise: ExerciseWithSets) {
                this.exercise = exercise
            }
        }

    
}