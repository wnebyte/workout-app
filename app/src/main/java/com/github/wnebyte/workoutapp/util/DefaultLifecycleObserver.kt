package com.github.wnebyte.workoutapp.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

interface DefaultLifecycleObserver: LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreated(source: LifecycleOwner)
}