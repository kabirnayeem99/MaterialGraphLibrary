package io.github.kabirnayeem99.materialgraphlibrary

/* It's a  */
/**
 * Data class that holding the stack values and the stack color
 *
 * @property stackValue Int
 * @property stackColor Int
 */
class BarStackSegment(var stackValue: Int, val stackColor: Int) : Cloneable {

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}