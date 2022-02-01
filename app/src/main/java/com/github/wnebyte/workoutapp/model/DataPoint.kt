package com.github.wnebyte.workoutapp.model

import java.util.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPoint(
    val id: UUID,
    val x: Long,
    val y: Float
): Parcelable