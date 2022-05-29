package io.github.kabirnayeem99.materialgraphlibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

class BarGraph : View {

    private var points = ArrayList<Bar>()

    private val paint = Paint()
    private val path = Path()

    private var rect: Rect? = null
    private val secondRect = Rect()
    private val thirdRect = Rect()

    private var showBarText = true
    private var indexSelected = -1

    private var listener: OnBarClickedListener? = null
    private var fullImage: Bitmap? = null
    private var shouldUpdate = false
    var unit = "$"
    var isAppended = false
        private set


    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setShowBarText(show: Boolean) {
        showBarText = show
    }

    fun update() {
        shouldUpdate = true
        postInvalidate()
    }

    fun appendUnit(doAppend: Boolean) {
        isAppended = doAppend
    }

    var bars: ArrayList<Bar>
        get() = points
        set(points) {
            this.points = points
            shouldUpdate = true
            postInvalidate()
        }

    private val selectPadding = 4

    private val pointRectF by lazy {
        RectF(
            (rect!!.left - selectPadding).toFloat(),
            (rect!!.top - selectPadding).toFloat(),
            (rect!!.right + selectPadding).toFloat(),
            (rect!!.bottom + selectPadding).toFloat()
        )
    }


    private val pointRegion by lazy {
        Region(
            rect!!.left - selectPadding,
            rect!!.top - selectPadding,
            rect!!.right + selectPadding,
            rect!!.bottom + selectPadding
        )
    }

    private val values = ArrayList<BarStackSegment?>()

    public override fun onDraw(ca: Canvas) {
        if (fullImage == null || shouldUpdate) {
            fullImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(fullImage!!)
            canvas.drawColor(Color.TRANSPARENT)

            val popup =
                ContextCompat.getDrawable(context, R.drawable.popup_black) as NinePatchDrawable
            var maxValue = 0f
            val padding = 7f
            val bottomPadding = 40f
            val usableHeight: Float
            if (showBarText) {
                paint.textSize = 40f
                paint.getTextBounds(unit, 0, 1, thirdRect)
                usableHeight =
                    height - bottomPadding - abs(thirdRect.top - thirdRect.bottom) - 26
            } else {
                usableHeight = height - bottomPadding
            }
            paint.color = Color.BLACK
            paint.strokeWidth = 2f
            paint.alpha = 50
            paint.isAntiAlias = true
            canvas.drawLine(
                0f,
                height - bottomPadding + 10,
                width.toFloat(),
                height - bottomPadding + 10,
                paint
            )
            val barWidth = (width - padding * 2 * points.size) / points.size
            for (p in points) {
                maxValue += p.value
            }
            rect = Rect()
            path.reset()
            var count = 0
            for (point in points) {
                if (point.stackedBar) {
                    // deep copy of StackedValues

                    try {
                        for (value in point.stackedValues) {
                            values.add(value.clone() as BarStackSegment)
                        }
                    } catch (e: CloneNotSupportedException) {
                        e.printStackTrace()
                        continue
                    }
                    var prevValue = 0f
                    for (value in values) {
                        value!!.Value += prevValue
                        prevValue += value!!.Value
                    }
                    values.reverse()
                    for (value in values) {
                        rect!![(padding * 2 * count + padding + barWidth * count).toInt(), (height - bottomPadding - usableHeight * (value!!.Value / maxValue)).toInt(), (padding * 2 * count + padding + barWidth * (count + 1)).toInt()] =
                            (height - bottomPadding).toInt()
                        path.addRect(pointRectF, Path.Direction.CW)
                        point.path = path
                        point.region = pointRegion
                        this.paint.color = value.Color
                        this.paint.alpha = 255
                        canvas.drawRect(rect!!, this.paint)
                    }
                } else {
                    rect!![(padding * 2 * count + padding + barWidth * count).toInt(), (height - bottomPadding - usableHeight * (point.value / maxValue)).toInt(), (padding * 2 * count + padding + barWidth * (count + 1)).toInt()] =
                        (height - bottomPadding).toInt()
                    path.addRect(pointRectF, Path.Direction.CW)
                    point.path = path
                    point.region = pointRegion
                    this.paint.color = point.color
                    this.paint.alpha = 255
                    canvas.drawRect(rect!!, this.paint)
                }
                this.paint.textSize = 20f
                canvas.drawText(
                    point.name!!,
                    ((rect!!.left + rect!!.right) / 2 - this.paint.measureText(point.name) / 2).toInt()
                        .toFloat(),
                    (height - 5).toFloat(),
                    this.paint
                )
                if (showBarText) {
                    this.paint.textSize = 40f
                    this.paint.color = Color.WHITE
                    this.paint.getTextBounds(unit + point.value, 0, 1, secondRect)
                    popup.setBounds(
                        ((rect!!.left + rect!!.right) / 2 - this.paint.measureText(unit + point.value) / 2).toInt() - 14,
                        rect!!.top + (secondRect.top - secondRect.bottom) - 26,
                        ((rect!!.left + rect!!.right) / 2 + this.paint.measureText(unit + point.value) / 2).toInt() + 14,
                        rect!!.top
                    )
                    popup.draw(canvas)
                    if (isAppended) canvas.drawText(
                        point.value.toString() + unit,
                        ((rect!!.left + rect!!.right) / 2 - this.paint.measureText(unit + point.value) / 2).toInt()
                            .toFloat(),
                        (rect!!.top - 20).toFloat(),
                        this.paint
                    ) else canvas.drawText(
                        unit + point.value,
                        ((rect!!.left + rect!!.right) / 2 - this.paint.measureText(unit + point.value) / 2).toInt()
                            .toFloat(),
                        (rect!!.top - 20).toFloat(),
                        this.paint
                    )
                }
                if (indexSelected == count && listener != null) {
                    this.paint.color = Color.parseColor("#33B5E5")
                    this.paint.alpha = 100
                    canvas.drawPath(point.path!!, this.paint)
                    this.paint.alpha = 255
                }
                count++
            }
            shouldUpdate = false
        }
        ca.drawBitmap(fullImage!!, 0f, 0f, null)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val point = Point()
        point.x = event.x.toInt()
        point.y = event.y.toInt()
        for ((count, bar) in points.withIndex()) {
            val r = Region()
            r.setPath(bar.path!!, bar.region!!)
            if (r.contains(point.x, point.y) && event.action == MotionEvent.ACTION_DOWN) {
                indexSelected = count
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (r.contains(point.x, point.y) && listener != null) {
                    listener!!.onClick(indexSelected)
                }
                indexSelected = -1
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            shouldUpdate = true
            postInvalidate()
        }
        return true
    }

    fun setOnBarClickedListener(listener: OnBarClickedListener?) {
        this.listener = listener
    }

    interface OnBarClickedListener {
        fun onClick(index: Int)
    }
}