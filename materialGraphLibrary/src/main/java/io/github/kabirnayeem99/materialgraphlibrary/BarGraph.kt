package io.github.kabirnayeem99.materialgraphlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

/**
 * A View to help to draw a bar graph.
 *
 * A bar graph is a chart or graph that presents categorical data with rectangular
 * bars with heights or lengths proportional to the values that they represent. The bars can be
 * plotted vertically or horizontally. A vertical bar chart is sometimes called a column chart.
 */
class BarGraph(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {

    private var points = ArrayList<Bar>()

    private val secondRect by lazy { Rect() }
    private val thirdRect by lazy { Rect() }

    private var showBarText = true
    private var indexSelected = -1

    private var listener: (Int) -> Unit = {}

    private var shouldUpdate = false

    var unit = "$"
    var isAppended = false
        private set


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

    private val paint by lazy { Paint() }
    private val path by lazy { Path() }

    private val rect: Rect by lazy { Rect() }

    private val pointRectF by lazy {
        RectF(
            (rect.left - selectPadding).toFloat(),
            (rect.top - selectPadding).toFloat(),
            (rect.right + selectPadding).toFloat(),
            (rect.bottom + selectPadding).toFloat()
        )
    }

    private val pointRegion by lazy {
        Region(
            rect.left - selectPadding,
            rect.top - selectPadding,
            rect.right + selectPadding,
            rect.bottom + selectPadding
        )
    }

    private val values = ArrayList<BarStackSegment?>()


    /**
     * A bitmap with the width and height of the view.
     */
    private val fullImage: Bitmap by lazy {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    private val tempCanvasForDrawing by lazy { Canvas(fullImage) }

    private val pointPadding = 7f
    private val pointBottomPadding = 40f

    /**
     * If the image should be updated, update it, then draw it
     *
     * @param canvas The canvas to draw on.
     */
    public override fun onDraw(canvas: Canvas) {
        if (shouldUpdate) onDrawWithUpdate()
        canvas.drawBitmap(fullImage, 0f, 0f, null)
    }

    /**
     * Draws the bars, the text, and the popup
     */
    private fun onDrawWithUpdate() {
        tempCanvasForDrawing.drawColor(Color.TRANSPARENT)

        val popup =
            ContextCompat.getDrawable(context, R.drawable.popup_black) as NinePatchDrawable

        var maxValue = 0f
        val padding = 7f
        val bottomPadding = 40f
        val usableHeight: Float = if (showBarText) fetchUsableHeightIfTextShown()
        else height - bottomPadding

        paint.color = Color.BLACK
        paint.strokeWidth = 2f
        paint.alpha = 50
        paint.isAntiAlias = true

        tempCanvasForDrawing.drawLine(
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
        path.reset()
        var count = 0
        for (point in points) {
            if (drawEachBar(point, count, barWidth, usableHeight, maxValue, popup)) continue
            count++
        }
        shouldUpdate = false
    }

    /**
     * If the text is shown, return the usable height of the canvas
     *
     * @return The usable height of the canvas.
     */
    private fun fetchUsableHeightIfTextShown(): Float {
        paint.textSize = 40f
        val bottomPadding = 40F
        paint.getTextBounds(unit, 0, 1, thirdRect)
        return height - bottomPadding - abs(thirdRect.top - thirdRect.bottom) - 26
    }

    /**
     * Draws each bar, and returns true if the bar is a stacked bar and it failed to draw it
     *
     * @param bar Bar - The bar object that contains the data for the bar
     * @param count The index of the bar
     * @param barWidth The width of each bar.
     * @param usableHeight The height of the chart minus the padding.
     * @param maxValue The maximum value of the bar chart.
     * @param popup NinePatchDrawable
     * @return A boolean value.
     */
    private fun drawEachBar(
        bar: Bar,
        count: Int,
        barWidth: Float,
        usableHeight: Float,
        maxValue: Float,
        popup: NinePatchDrawable
    ): Boolean {
        val isTheBarAStackedBar = bar.stackedBar

        if (isTheBarAStackedBar) {
            val isStackedBarDrawnFailed =
                onDrawStackedBar(bar, count, barWidth, usableHeight, maxValue)
            if (isStackedBarDrawnFailed) return true
        } else onDrawNonStackedBar(count, barWidth, usableHeight, bar, maxValue)

        drawBarName(bar)

        if (showBarText) onShowBarTextOnEachBar(bar, popup)

        val isTheBarSelectedAndClickable = indexSelected == count

        if (isTheBarSelectedAndClickable) onBarSelected(bar)

        return false
    }

    /**
     * Draws the name of the bar in the middle of the bar
     *
     * @param bar Bar - the bar object that we want to draw
     */
    private fun drawBarName(bar: Bar) {
        this.paint.textSize = 20f
        tempCanvasForDrawing.drawText(
            bar.name,
            ((rect.left + rect.right) / 2 - this.paint.measureText(bar.name) / 2).toInt()
                .toFloat(),
            (height - 5).toFloat(),
            this.paint
        )
    }

    /**
     * When a bar is selected, draw the bar's path on the tempCanvasForDrawing with a color of
     * #33B5E5 and an alpha of 100
     *
     * @param bar The bar that was selected
     */
    private fun onBarSelected(bar: Bar) {
        this.paint.color = Color.parseColor("#33B5E5")
        this.paint.alpha = 100
        tempCanvasForDrawing.drawPath(bar.path, this.paint)
        this.paint.alpha = 255
    }

    /**
     * Draws the text on the top of each bar
     *
     * @param bar Bar,
     * @param popup NinePatchDrawable - The popup that will be drawn on the bar.
     */
    private fun onShowBarTextOnEachBar(
        bar: Bar,
        popup: NinePatchDrawable
    ) {
        this.paint.textSize = 40f
        this.paint.color = Color.WHITE
        this.paint.getTextBounds(unit + bar.value, 0, 1, secondRect)
        popup.setBounds(
            ((rect.left + rect.right) / 2 - this.paint.measureText(unit + bar.value) / 2).toInt() - 14,
            rect.top + (secondRect.top - secondRect.bottom) - 26,
            ((rect.left + rect.right) / 2 + this.paint.measureText(unit + bar.value) / 2).toInt() + 14,
            rect.top
        )
        popup.draw(tempCanvasForDrawing)
        if (isAppended) drawUnitIfItIsAppended(bar) else drawUnitIfItIsNotAppended(bar)
    }

    /**
     * If the unit is appended, draw the unit
     *
     * @param bar Bar - the bar object that is being drawn
     */
    private fun drawUnitIfItIsAppended(bar: Bar) {
        tempCanvasForDrawing.drawText(
            bar.value.toString() + unit,
            ((rect.left + rect.right) / 2 - this.paint.measureText(unit + bar.value) / 2).toInt()
                .toFloat(),
            (rect.top - 20).toFloat(),
            this.paint
        )
    }

    /**
     * If the bar is not appended, draw the unit and the value of the bar
     *
     * @param bar Bar - the bar object that is being drawn
     */
    private fun drawUnitIfItIsNotAppended(bar: Bar) {
        tempCanvasForDrawing.drawText(
            unit + bar.value,
            ((rect.left + rect.right) / 2 - this.paint.measureText(unit + bar.value) / 2).toInt()
                .toFloat(),
            (rect.top - 20).toFloat(),
            this.paint
        )
    }

    /**
     * Draws a non-stacked bar on the canvas
     *
     * @param count The index of the bar in the bar chart.
     * @param barWidth The width of the bar.
     * @param usableHeight The height of the chart minus the bottom padding.
     * @param point Bar - The bar object that contains the value, color, and path of the bar.
     * @param maxValue The maximum value of the data set.
     */
    private fun onDrawNonStackedBar(
        count: Int,
        barWidth: Float,
        usableHeight: Float,
        point: Bar,
        maxValue: Float
    ) {
        rect[(pointPadding * 2 * count + pointPadding + barWidth * count).toInt(), (height - pointBottomPadding - usableHeight * (point.value / maxValue)).toInt(), (pointPadding * 2 * count + pointPadding + barWidth * (count + 1)).toInt()] =
            (height - pointBottomPadding).toInt()
        path.addRect(pointRectF, Path.Direction.CW)
        point.path = path
        point.region = pointRegion
        this.paint.color = point.color
        this.paint.alpha = 255
        tempCanvasForDrawing.drawRect(rect, this.paint)
    }

    /**
     * Draws a stacked bar chart on the canvas
     *
     * @param point Bar - The bar object that is being drawn
     * @param count The index of the bar in the bar chart.
     * @param barWidth The width of the bar.
     * @param usableHeight The height of the chart minus the padding.
     * @param maxValue The maximum value of the bar chart.
     * @return A boolean value.
     */
    private fun onDrawStackedBar(
        point: Bar,
        count: Int,
        barWidth: Float,
        usableHeight: Float,
        maxValue: Float
    ): Boolean {
        try {
            for (value in point.stackedValues) {
                values.add(value.clone() as BarStackSegment)
            }
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            return true
        }
        var prevValue = 0f
        for (value in values) {
            if (value != null) {
                value.stackValue += prevValue.toInt()
                prevValue += value.stackValue
            }
        }
        values.reverse()
        for (value in values) {
            rect[(pointPadding * 2 * count + pointPadding + barWidth * count).toInt(), (height - pointBottomPadding - usableHeight * (value!!.stackValue / maxValue)).toInt(), (pointPadding * 2 * count + pointPadding + barWidth * (count + 1)).toInt()] =
                (height - pointBottomPadding).toInt()
            path.addRect(pointRectF, Path.Direction.CW)
            point.path = path
            point.region = pointRegion
            this.paint.color = value.stackColor
            this.paint.alpha = 255
            tempCanvasForDrawing.drawRect(rect, this.paint)
        }
        return false
    }


    @SuppressLint("ClickableViewAccessibility")
    /**
     * If the user touches the screen, check if the touch is within the bounds of a bar. If it is, set
     * the indexSelected variable to the index of the bar. If the user lifts their finger, check if the
     * touch is within the bounds of a bar. If it is, call the onClick function of the listener
     *
     * @param event MotionEvent
     * @return A boolean value.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val point = Point()
        point.x = event.x.toInt()
        point.y = event.y.toInt()
        for ((count, bar) in points.withIndex()) {
            val r = Region()
            r.setPath(bar.path, bar.region)
            if (r.contains(point.x, point.y) && event.action == MotionEvent.ACTION_DOWN) {
                indexSelected = count
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (r.contains(point.x, point.y)) listener(indexSelected)
                indexSelected = -1
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            shouldUpdate = true
            postInvalidate()
        }
        return true
    }

    /**
     * Takes a function as a parameter and assigns it to a variable
     *
     * @param onBarClick (Int) -> Unit
     */
    fun setOnBarClickedListener(onBarClick: (Int) -> Unit) {
        this.listener = onBarClick
    }

}