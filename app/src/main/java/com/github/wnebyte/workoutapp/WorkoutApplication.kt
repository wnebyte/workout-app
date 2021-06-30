package com.github.wnebyte.workoutapp

import android.app.Application

class WorkoutApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
    }
}