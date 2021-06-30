package com.github.wnebyte.workoutapp.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.ExerciseSetItemBinding
import com.github.wnebyte.workoutapp.model.Set

class ExerciseSetItem(private val inflater: LayoutInflater,
                      private val clickListener: View.OnClickListener? = null) {

    companion object {

        fun newAdapter(
            inflater: LayoutInflater,
            clickListener: View.OnClickListener? = null
        ): Adapter {
            return ExerciseSetItem(inflater, clickListener).Adapter()
        }
    }

    inner class Holder(private val binding: ExerciseSetItemBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var set: Set

        init {
            binding.button.setOnClickListener {
                clickListener?.onClick(this.itemView)
            }
            binding.weights.doOnTextChanged { text, _, _, count ->
                if ((text != null) && (0 < count)) {
                    set.weights = text.toString().toDouble()
                }
            }
            binding.reps.doOnTextChanged { text, _, _, count ->
                if ((text != null) && (0 < count)) {
                    set.reps = text.toString().toInt()
                }
            }
        }
        fun bind(set: Set) {
            this.set = set
            binding.weights.setText(set.weights.toString(), TextView.BufferType.EDITABLE)
            binding.reps.setText(set.reps.toString(), TextView.BufferType.EDITABLE)
        }
    }

     inner class Adapter: ListAdapter<Set, Holder>(AdapterUtil.DIFF_UTIL_SET_CALLBACK) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = ExerciseSetItemBinding.inflate(inflater, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}