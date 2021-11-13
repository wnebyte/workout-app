package com.github.wnebyte.workoutapp.model

import java.util.*

data class ProgressItem(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val avg: Double,
    val unit: String,
    val monthlyChange: Float
)