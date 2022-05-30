package io.github.kabirnayeem99.materialgraphlibrary

import android.graphics.Path
import android.graphics.Region

/**
 * Data class that contains a bunch of properties that are used to draw a bar on the chart.
 *
 * It is used to store the data for a single bar on the chart
 *
 * @property {Int} color - The color of the bar.
 * @property {String} name - The name of the bar.
 * @property {Float} value - The value of the bar.
 * @property {Path} path - The path of the bar.
 * @property {Region} region - This is the region of the bar. It is used to determine if the bar is
 * clicked.
 * @property {Boolean} stackedBar - This is a boolean value that indicates whether the bar is a stacked
 * bar or not.
 * @property {ArrayList<BarStackSegment>} segments - This is an array list of BarStackSegment objects.
 */
data class Bar(
    var color: Int = 0,
    var name: String = "",
    var value: Float = 0f,
    var path: Path = Path(),
    var region: Region = Region(),
    var stackedBar: Boolean = false,
    private val segments: ArrayList<BarStackSegment> = ArrayList()
) {

    /**
     * Adds a new segment to the stack
     *
     * @param segment BarStackSegment - The segment to add to the stack.
     */
    fun addStack(segment: BarStackSegment) {
        segments.add(segment)
    }

    val stackedValues: ArrayList<BarStackSegment>
        get() = segments
}
