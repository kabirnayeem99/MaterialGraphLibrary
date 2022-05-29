package io.github.kabirnayeem99.materialgraphlibrary


class Line {
    private var points: ArrayList<LinePoint> = ArrayList<LinePoint>()
    var color = 0
    var isShowingPoints = true

    fun getPoints(): ArrayList<LinePoint> {
        return points
    }

    fun setPoints(points: ArrayList<LinePoint>) {
        this.points = points
    }

    fun addPoint(point: LinePoint) {
        points.add(point)
    }

    fun getPoint(index: Int): LinePoint {
        return points[index]
    }

    val size: Int
        get() = points.size
}
