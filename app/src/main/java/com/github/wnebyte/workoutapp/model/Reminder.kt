package com.github.wnebyte.workoutapp.model

data class Reminder(
    val value: Long
)
{
    val text get() = toText(value)

    override fun toString(): String = text

    companion object {
        const val ZERO = 0L
        const val ONE = 300L
        const val TWO = 600L
        const val THREE = 900L
        const val FOUR = 1800L
        const val FIVE = 2700L
        const val SIX = 3600L
        const val SEVEN = 5400L
        const val EIGHT = 7200L

        val CONSTANTS: Array<Long> = arrayOf(
            ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
        )

        private fun toText(value: Long): String {
            return when (value) {
                0L -> {
                    "None"
                }
                else -> {
                    val s = value % 60
                    val m = (value / 60) % 60
                    val h = (value / (60 * 60)) % 24
                    String.format("%02d:%02d:%02d %s", h, m, s, "before")
                }
            }
        }
    }
}