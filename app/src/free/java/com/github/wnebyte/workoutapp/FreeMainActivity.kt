package com.github.wnebyte.workoutapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.util.concurrent.Executors

private const val TAG = "FreeMainActivity"

class FreeMainActivity : MainActivity() {

    private val executor = Executors.newSingleThreadExecutor()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val uri = Uri.parse("content://com.github.wnebyte.workoutapp.free.provider/set")
        executor.submit {
            val cursor = contentResolver.query(
                uri,
                null, null, null, null
            )
            Log.i(TAG, "count: ${cursor?.count ?: "null"}")
            cursor?.close()
        }
    }
}