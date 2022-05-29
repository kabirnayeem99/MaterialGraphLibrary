package io.github.kabirnayeem99.materialgraphlibrary

import android.graphics.Color
import android.graphics.Path
import android.graphics.Region

data class MultiSeriesDonutSlice(
    var color: Int = Color.BLACK,
    var value: Float = 0f,
    var title: String? = null,
    var path: Path? = null,
    var region: Region? = null,
    var isSelectable: Boolean = true
)
