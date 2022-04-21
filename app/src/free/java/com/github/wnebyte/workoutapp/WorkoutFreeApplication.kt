package com.github.wnebyte.workoutapp

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.ads.MobileAds

class WorkoutFreeApplication : Application() {

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}