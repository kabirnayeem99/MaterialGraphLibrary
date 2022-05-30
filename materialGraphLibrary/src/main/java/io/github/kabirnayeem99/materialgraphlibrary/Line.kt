package io.github.kabirnayeem99.materialgraphlibrary


data class Line(
     var points: ArrayList<LinePoint> = ArrayList(),
    var color: Int = 0,
    var isShowingPoints: Boolean = true,
) {

    fun addPoint(point: LinePoint) {
        points.add(point)
    }

    fun getPoint(index: Int): LinePoint {
        return points[index]
    }

    val size: Int
        get() = points.size
}


