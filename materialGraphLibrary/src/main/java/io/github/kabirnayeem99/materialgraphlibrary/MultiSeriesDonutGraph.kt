package io.github.kabirnayeem99.materialgraphlibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import android.view.View

class MultiSeriesDonutGraph : View {
    private var seriesList: MutableList<MutableList<MultiSeriesDonutSlice>> =
        ArrayList<MutableList<MultiSeriesDonutSlice>>()
    private val paint = Paint()
    private val path = Path()
    private var indexSelected = Pair.create(-1, -1)

    /**
     * Either the thickness or the innerRadius can be specified. This determines which will be used to calculate the size of the hole in the middle of the chart.
     */
    private var useThickness = true
    private var thickness = 200f
    private var innerRadius = 0f
    private var listener: OnSeriesSliceClickedListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun update() {
        postInvalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT)
        paint.reset()
        paint.isAntiAlias = true
        val midX: Float
        val midY: Float
        var chartRadius: Float
        val chartInnerRadius: Float
        path.reset()
        val padding = 2f
        midX = (width / 2).toFloat()
        midY = (height / 2).toFloat()
        chartRadius = if (midX < midY) {
            midX
        } else {
            midY
        }
        chartRadius -= padding
        chartInnerRadius = if (useThickness) {
            chartRadius - thickness
        } else {
            innerRadius
        }
        val radialPadding = 2 * padding
        val totalRadialPadding = (seriesList.size - 1) * radialPadding
        val sliceRadialThickness =
            (chartRadius - chartInnerRadius - totalRadialPadding) / seriesList.size
        for (seriesIndex in seriesList.indices) {
            val series: List<MultiSeriesDonutSlice> = seriesList[seriesIndex]
            val radius = chartRadius - (sliceRadialThickness + radialPadding) * seriesIndex
            val innerRadius = radius - sliceRadialThickness
            var currentAngle = 270f
            var currentSweep: Float
            var totalValue = 0
            for (slice in series) {
                totalValue += slice.value.toInt()
            }
            var count = 0
            for (slice in series) {
                val p = Path()
                paint.color = slice.color
                currentSweep = slice.value / totalValue * 360
                p.arcTo(
                    RectF(midX - radius, midY - radius, midX + radius, midY + radius),
                    currentAngle + padding,
                    currentSweep - padding
                )
                p.arcTo(
                    RectF(
                        midX - innerRadius,
                        midY - innerRadius,
                        midX + innerRadius,
                        midY + innerRadius
                    ), currentAngle + padding + (currentSweep - padding), -(currentSweep - padding)
                )
                p.close()
                slice.path = p
                slice.region = (
                        Region(
                            (midX - radius).toInt(),
                            (midY - radius).toInt(),
                            (midX + radius).toInt(),
                            (midY + radius).toInt()
                        )
                        )
                canvas.drawPath(p, paint)
                if (indexSelected.first == seriesIndex && indexSelected.second == count && listener != null) {
                    path.reset()
                    paint.color = slice.color
                    paint.color = Color.parseColor("#33B5E5")
                    paint.alpha = 100
                    if (seriesList.size > 1) {
                        path.arcTo(
                            RectF(
                                midX - radius - padding * 2,
                                midY - radius - padding * 2,
                                midX + radius + padding * 2,
                                midY + radius + padding * 2
                            ), currentAngle, currentSweep + padding
                        )
                        path.arcTo(
                            RectF(
                                midX - innerRadius + padding * 2,
                                midY - innerRadius + padding * 2,
                                midX + innerRadius - padding * 2,
                                midY + innerRadius - padding * 2
                            ), currentAngle + currentSweep + padding, -(currentSweep + padding)
                        )
                        path.close()
                    } else {
                        path.addCircle(midX, midY, radius + padding, Path.Direction.CW)
                    }
                    canvas.drawPath(path, paint)
                    paint.alpha = 255
                }
                currentAngle = currentAngle + currentSweep
                count++
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val point = Point()
        point.x = event.x.toInt()
        point.y = event.y.toInt()
        var seriesCount = 0
        for (series in seriesList) {
            var sliceCount = 0
            for (slice in series) {
                if (slice.isSelectable) {
                    val r = Region()
                    r.setPath(slice.path!!, slice.region!!)
                    if (r.contains(point.x, point.y) && event.action == MotionEvent.ACTION_DOWN) {
                        indexSelected = Pair.create(seriesCount, sliceCount)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (r.contains(point.x, point.y) && listener != null) {
                            if (indexSelected.first > -1) {
                                listener!!.onClick(indexSelected.first, indexSelected.second)
                            }
                            indexSelected = Pair.create(-1, -1)
                        }
                    }
                }
                sliceCount++
            }
            seriesCount++
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            postInvalidate()
        }
        return true
    }

    fun getSeriesList(): List<MutableList<MultiSeriesDonutSlice>> {
        return seriesList
    }

    fun setSeriesList(seriesList: MutableList<MutableList<MultiSeriesDonutSlice>>) {
        this.seriesList = seriesList
        postInvalidate()
    }

    fun getSlice(series: Int, index: Int): MultiSeriesDonutSlice {
        return seriesList[series][index]
    }

    fun addSlice(series: Int, slice: MultiSeriesDonutSlice) {
        while (seriesList.size < series + 1) {
            seriesList.add(ArrayList<MultiSeriesDonutSlice>())
        }
        seriesList[series].add(slice)
        postInvalidate()
    }

    fun setOnSliceClickedListener(listener: OnSeriesSliceClickedListener?) {
        this.listener = listener
    }

    fun setThickness(thickness: Float) {
        this.thickness = thickness
        useThickness = true
        postInvalidate()
    }

    fun setInnerRadius(innerRadius: Float) {
        this.innerRadius = innerRadius
        useThickness = false
        postInvalidate()
    }

    fun removeSlices() {
        for (i in seriesList.indices.reversed()) {
            for (j in seriesList[i].indices.reversed()) {
                seriesList[i].removeAt(j)
            }
            seriesList.removeAt(i)
        }
        postInvalidate()
    }

    interface OnSeriesSliceClickedListener {
        fun onClick(series: Int, index: Int)
    }
}
