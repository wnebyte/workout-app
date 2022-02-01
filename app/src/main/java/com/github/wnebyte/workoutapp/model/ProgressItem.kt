package com.github.wnebyte.workoutapp.model

import java.util.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgressItem(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val data: List<DataPoint>,
    val avgWeights: Float,
    val avgReps: Float,
    val unit: String,
    val change: Float
) : Parcelable