package io.github.kabirnayeem99.materialgraphlibrary

import android.graphics.Path
import android.graphics.Region

class LinePoint {
    var x = 0f
    var y = 0f
    var path: Path? = null
    var region: Region? = null
    var labelString: String? = null

    constructor(x: Float, y: Float) : super() {
        this.x = x
        this.y = y
    }

    constructor() {}
}
