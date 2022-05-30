package io.github.kabirnayeem99.materialgraphlibrary

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class LineGraph(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var lines: ArrayList<Line> = ArrayList<Line>()
    private val paint = Paint()
    private val txtPaint = Paint()
    private var minY = 0f
    val minX: Float
        get() {
            var max: Float = lines[0].getPoint(0).x
            for (line in lines) {
                for (point in line.points) {
                    if (point.x < max) max = point.x
                }
            }
            maxX = max
            return maxX
        }
    private var maxY = 0f
    private var maxX = 0f
    private var isMaxYUserSet = false
    private var lineToFill = -1
    private var indexSelected = -1
    private var listener: OnPointClickedListener? = null
    private var fullImage: Bitmap? = null
    private var shouldUpdate = false
    private var showMinAndMax = false
    private var showHorizontalGrid = false
    private var gridColor = -0x1
    var labelSize = 10

    fun setGridColor(color: Int) {
        gridColor = color
    }

    fun showHorizontalGrid(show: Boolean) {
        showHorizontalGrid = show
    }

    fun showMinAndMaxValues(show: Boolean) {
        showMinAndMax = show
    }

    fun setTextColor(color: Int) {
        txtPaint.color = color
    }

    fun setTextSize(s: Float) {
        txtPaint.textSize = s
    }

    fun setMinY(minY: Float) {
        this.minY = minY
    }

    fun update() {
        shouldUpdate = true
        postInvalidate()
    }

    fun removeAllLines() {
        while (lines.size > 0) {
            lines.removeAt(0)
        }
        shouldUpdate = true
        postInvalidate()
    }

    fun addLine(line: Line) {
        lines.add(line)
        shouldUpdate = true
        postInvalidate()
    }

    fun getLines(): ArrayList<Line> {
        return lines
    }

    fun setLineToFill(indexOfLine: Int) {
        lineToFill = indexOfLine
        shouldUpdate = true
        postInvalidate()
    }

    fun getLineToFill(): Int {
        return lineToFill
    }

    fun setLines(lines: ArrayList<Line>) {
        this.lines = lines
    }

    fun getLine(index: Int): Line {
        return lines[index]
    }

    val size: Int
        get() = lines.size

    fun setRangeY(min: Float, max: Float) {
        minY = min
        maxY = max
        isMaxYUserSet = true
    }

    fun getMaxY(): Float {
        return if (isMaxYUserSet) {
            maxY
        } else {
            maxY = lines[0].getPoint(0).y
            for (line in lines) {
                for (point in line.points) {
                    if (point.y > maxY) {
                        maxY = point.y
                    }
                }
            }
            maxY
        }
    }

    private fun getMinY(): Float {
        return if (isMaxYUserSet) {
            minY
        } else {
            var min: Float = lines[0].getPoint(0).y
            for (line in lines) {
                for (point in line.points) {
                    if (point.y < min) min = point.y
                }
            }
            minY = min
            minY
        }
    }

    private fun getMaxX(): Float {
        var max: Float = lines[0].getPoint(0).x
        for (line in lines) {
            for (point in line.points) {
                if (point.x > max) max = point.x
            }
        }
        maxX = max
        return maxX
    }

    public override fun onDraw(ca: Canvas) {
        if (fullImage == null || shouldUpdate) {
            fullImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(fullImage!!)
            val max = maxY.toInt().toString() + "" // used to display max
            val min = minY.toInt().toString() + "" // used to display min
            paint.reset()
            val path = Path()
            var bottomPadding = 10f
            val topPadding = 10f
            var sidePadding = 10f
            if (showMinAndMax) sidePadding = txtPaint.measureText(max)
            if (labelSize > bottomPadding) {
                bottomPadding = labelSize.toFloat()
            }
            val usableHeight = height - bottomPadding - topPadding
            val usableWidth = width - sidePadding * 2
            val lineSpace = usableHeight / 10
            var lineCount = 0
            for (line in lines) {
                var count = 0
                var lastXPixels = 0f
                var newYPixels: Float
                var lastYPixels = 0f
                var newXPixels: Float
                val maxY = getMaxY()
                val minY = getMinY()
                val maxX = getMaxX()
                val minX = minX
                if (lineCount == lineToFill) {
                    paint.color = Color.BLACK
                    paint.alpha = 30
                    paint.strokeWidth = 2f
                    var i = 10
                    while (i - width < height) {
                        canvas.drawLine(
                            i.toFloat(),
                            height - bottomPadding,
                            0f,
                            height - bottomPadding - i,
                            paint
                        )
                        i += 20
                    }
                    paint.reset()
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    for (p in line.points) {
                        val yPercent: Float = (p.y - minY) / (maxY - minY)
                        val xPercent: Float = (p.x - minX) / (maxX - minX)
                        if (count == 0) {
                            lastXPixels = sidePadding + xPercent * usableWidth
                            lastYPixels = height - bottomPadding - usableHeight * yPercent
                            path.moveTo(lastXPixels, lastYPixels)
                        } else {
                            newXPixels = sidePadding + xPercent * usableWidth
                            newYPixels = height - bottomPadding - usableHeight * yPercent
                            path.lineTo(newXPixels, newYPixels)
                            val pa = Path()
                            pa.moveTo(lastXPixels, lastYPixels)
                            pa.lineTo(newXPixels, newYPixels)
                            pa.lineTo(newXPixels, 0f)
                            pa.lineTo(lastXPixels, 0f)
                            pa.close()
                            canvas.drawPath(pa, paint)
                            lastXPixels = newXPixels
                            lastYPixels = newYPixels
                        }
                        count++
                    }
                    path.reset()
                    path.moveTo(0f, height - bottomPadding)
                    path.lineTo(sidePadding, height - bottomPadding)
                    path.lineTo(sidePadding, 0f)
                    path.lineTo(0f, 0f)
                    path.close()
                    canvas.drawPath(path, paint)
                    path.reset()
                    path.moveTo(width.toFloat(), height - bottomPadding)
                    path.lineTo(width - sidePadding, height - bottomPadding)
                    path.lineTo(width - sidePadding, 0f)
                    path.lineTo(width.toFloat(), 0f)
                    path.close()
                    canvas.drawPath(path, paint)
                }
                lineCount++
            }
            paint.reset()
            paint.color = gridColor
            paint.alpha = 50
            paint.isAntiAlias = true
            canvas.drawLine(
                sidePadding,
                height - bottomPadding,
                width.toFloat(),
                height - bottomPadding,
                paint
            )
            if (showHorizontalGrid) for (i in 1..10) {
                canvas.drawLine(
                    sidePadding,
                    height - bottomPadding - i * lineSpace,
                    width.toFloat(),
                    height - bottomPadding - i * lineSpace,
                    paint
                )
            }
            paint.alpha = 255
            paint.textAlign = Align.CENTER
            paint.textSize = labelSize.toFloat()
            for (line in lines) {
                var count = 0
                var lastXPixels = 0f
                var newYPixels: Float
                var lastYPixels = 0f
                var newXPixels: Float
                val maxY = getMaxY()
                val minY = getMinY()
                val maxX = getMaxX()
                val minX = minX
                paint.color = line.color
                paint.strokeWidth = 6f
                for (p in line.points) {
                    val yPercent: Float = (p.y - minY) / (maxY - minY)
                    val xPercent: Float = (p.x - minX) / (maxX - minX)
                    if (count == 0) {
                        lastXPixels = sidePadding + xPercent * usableWidth
                        lastYPixels = height - bottomPadding - usableHeight * yPercent
                    } else {
                        newXPixels = sidePadding + xPercent * usableWidth
                        newYPixels = height - bottomPadding - usableHeight * yPercent
                        canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, paint)
                        lastXPixels = newXPixels
                        lastYPixels = newYPixels
                    }
                    if (p.labelString != null) {
                        canvas.drawText(
                            p.labelString!!,
                            lastXPixels,
                            usableHeight + bottomPadding,
                            paint
                        )
                    }
                    count++
                }
            }
            var pointCount = 0
            for (line in lines) {
                val maxY = getMaxY()
                val minY = getMinY()
                val maxX = getMaxX()
                val minX = minX
                paint.color = line.color
                paint.strokeWidth = 6f
                paint.strokeCap = Paint.Cap.ROUND
                if (line.isShowingPoints) {
                    for (p in line.points) {
                        val yPercent: Float = (p.y - minY) / (maxY - minY)
                        val xPercent: Float = (p.x - minX) / (maxX - minX)
                        val xPixels = sidePadding + xPercent * usableWidth
                        val yPixels = height - bottomPadding - usableHeight * yPercent
                        paint.color = Color.GRAY
                        canvas.drawCircle(xPixels, yPixels, 10f, paint)
                        paint.color = Color.WHITE
                        canvas.drawCircle(xPixels, yPixels, 5f, paint)
                        val path2 = Path()
                        path2.addCircle(xPixels, yPixels, 30f, Path.Direction.CW)
                        p.path = path2
                        p.region = (
                                Region(
                                    (xPixels - 30).toInt(),
                                    (yPixels - 30).toInt(),
                                    (xPixels + 30).toInt(),
                                    (yPixels + 30).toInt()
                                )
                                )
                        if (indexSelected == pointCount && listener != null) {
                            paint.color = Color.parseColor("#33B5E5")
                            paint.alpha = 100
                            canvas.drawPath(p.path!!, paint)
                            paint.alpha = 255
                        }
                        pointCount++
                    }
                }
            }
            shouldUpdate = false
            if (showMinAndMax) {
                ca.drawText(max, 0f, txtPaint.textSize, txtPaint)
                ca.drawText(min, 0f, this.height.toFloat(), txtPaint)
            }
        }
        ca.drawBitmap(fullImage!!, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val point = Point()
        point.x = event.x.toInt()
        point.y = event.y.toInt()
        var count = 0
        var lineCount = 0
        var pointCount: Int
        val r = Region()
        for (line in lines) {
            pointCount = 0
            for (p in line.points) {
                if (p.path != null && p.region != null) {
                    r.setPath(p.path!!, p.region!!)
                    if (r.contains(point.x, point.y) && event.action == MotionEvent.ACTION_DOWN) {
                        indexSelected = count
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (r.contains(point.x, point.y) && listener != null) {
                            listener!!.onClick(lineCount, pointCount)
                        }
                        indexSelected = -1
                    }
                }
                pointCount++
                count++
            }
            lineCount++
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            shouldUpdate = true
            postInvalidate()
        }
        return true
    }

    fun setOnPointClickedListener(listener: OnPointClickedListener?) {
        this.listener = listener
    }

    interface OnPointClickedListener {
        fun onClick(lineIndex: Int, pointIndex: Int)
    }

    init {
        txtPaint.color = -0x1
        txtPaint.textSize = 20f
        txtPaint.isAntiAlias = true
    }
}
