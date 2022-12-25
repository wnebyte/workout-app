package com.github.wnebyte.workoutapp.gui.workout

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.util.DefaultLifecycleObserver

abstract class BaseFragment : Fragment() {

    protected abstract val toolbar: Toolbar

    protected abstract val menuResId: Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(menuResId)
        val activity = requireActivity() as MainActivity
        val created = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        if (!created) {
            activity.lifecycle.addObserver(object: DefaultLifecycleObserver {
                override fun onCreated(source: LifecycleOwner) {
                    activity.setupActionBar(toolbar)
                    source.lifecycle.removeObserver(this)
                }

            })
        } else {
            activity.setupActionBar(toolbar)
        }
    }
}