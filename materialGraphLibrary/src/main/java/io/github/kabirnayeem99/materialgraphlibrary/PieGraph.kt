package io.github.kabirnayeem99.materialgraphlibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class PieGraph : View {
    private var slices: ArrayList<PieSlice> = ArrayList<PieSlice>()
    private val paint = Paint()
    private val path = Path()
    private var indexSelected = -1
    private var thickness = 50
    private var listener: OnSliceClickedListener? = null

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
        var radius: Float
        val innerRadius: Float
        path.reset()
        var currentAngle = 270f
        var currentSweep: Float
        var totalValue = 0
        val padding = 2f
        midX = (width / 2).toFloat()
        midY = (height / 2).toFloat()
        radius = if (midX < midY) {
            midX
        } else {
            midY
        }
        radius -= padding
        innerRadius = radius - thickness
        for (slice in slices) {
            totalValue += slice.value.toInt()
        }
        var count = 0
        for (slice in slices) {
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
            slice.path = (p)
            slice.region = (
                    Region(
                        (midX - radius).toInt(),
                        (midY - radius).toInt(),
                        (midX + radius).toInt(),
                        (midY + radius).toInt()
                    )
                    )
            canvas.drawPath(p, paint)
            if (indexSelected == count && listener != null) {
                path.reset()
                paint.color = slice.color
                paint.color = Color.parseColor("#33B5E5")
                paint.alpha = 100
                if (slices.size > 1) {
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
            val icon: Bitmap? = slice.icon
            if (icon != null) {
                val rad = 2 * Math.PI * (currentAngle + currentSweep / 2) / 360.0
                val left = Math.cos(rad) * (innerRadius + radius) / 2
                val top = Math.sin(rad) * (innerRadius + radius) / 2
                canvas.drawBitmap(
                    icon,
                    midX - icon.width / 2 + left.toFloat(),
                    midY - icon.height / 2 + top.toFloat(),
                    null
                )
            }
            currentAngle += currentSweep
            count++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val point = Point()
        point.x = event.x.toInt()
        point.y = event.y.toInt()
        var count = 0
        for (slice in slices) {
            val r = Region()
            r.setPath(slice.path!!, slice.region!!)
            if (r.contains(point.x, point.y) && event.action == MotionEvent.ACTION_DOWN) {
                indexSelected = count
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (r.contains(point.x, point.y) && listener != null) {
                    if (indexSelected > -1) {
                        listener!!.onClick(indexSelected)
                    }
                    indexSelected = -1
                }
            }
            count++
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            postInvalidate()
        }
        return true
    }

    fun getSlices(): ArrayList<PieSlice> {
        return slices
    }

    fun setSlices(slices: ArrayList<PieSlice>) {
        this.slices = slices
        postInvalidate()
    }

    fun getSlice(index: Int): PieSlice {
        return slices[index]
    }

    fun addSlice(slice: PieSlice) {
        slices.add(slice)
        postInvalidate()
    }

    fun setOnSliceClickedListener(listener: OnSliceClickedListener?) {
        this.listener = listener
    }

    fun getThickness(): Int {
        return thickness
    }

    fun setThickness(thickness: Int) {
        this.thickness = thickness
        postInvalidate()
    }

    fun removeSlices() {
        for (i in slices.indices.reversed()) {
            slices.removeAt(i)
        }
        postInvalidate()
    }

    interface OnSliceClickedListener {
        fun onClick(index: Int)
    }
}
