package io.github.kabirnayeem99.materialgraphlibrary

/**
 * Created by Aaron on 19/10/2014.
 */
class BarStackSegment(`val`: Int, color: Int) : Cloneable {
    var Value: Float
    var Color: Int

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }

    init {
        Value = `val`.toFloat()
        Color = color
    }
}