package com.github.wnebyte.workoutapp.ui.workout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutBinding
import kotlin.math.hypot

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btn.setOnClickListener {
            if (clockViewIsVisible()) {
                hideClockView()
            } else {
                showClockView()
            }
        }
    }

    private fun clockViewIsVisible(): Boolean =
        binding.clockView.visibility == View.VISIBLE

    private fun showClockView() {
        val cx = binding.clockView.width / 2
        val cy = binding.clockView.height / 2
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils
            .createCircularReveal(binding.clockView, cx, cy, 0f, finalRadius)
        binding.clockView.visibility = View.VISIBLE
        anim.start()
    }

    private fun hideClockView() {
        val cx = binding.clockView.width / 2
        val cy = binding.clockView.height / 2
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils
            .createCircularReveal(binding.clockView, cx, cy, initialRadius, 0f)
        anim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.clockView.visibility = View.GONE
            }
        })
        anim.start()
    }
}