package io.gitlab.arturbosch.detekt.formatting

import java.util.ArrayList

/**
 * Extracted and adapted from KtLint, see
 * https://github.com/pinterest/ktlint/blob/a86d1c7/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt#L51
 */
internal const val UTF8_BOM = "\uFEFF"

/**
 * Extracted and adapted from KtLint, see
 * https://github.com/pinterest/ktlint/blob/a86d1c7/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt#L173
 */
internal fun normalizeText(text: String): String {
    return text
        .replace("\r\n", "\n")
        .replace("\r", "\n")
        .replaceFirst(UTF8_BOM, "")
}

/**
 * Extracted and adapted from KtLint, see
 * https://github.com/pinterest/ktlint/blob/a86d1c7/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt#L320
 */
internal fun calculateLineColByOffset(text: String): (offset: Int) -> Pair<Int, Int> {
    var i = -1
    val e = text.length
    val arr = ArrayList<Int>()
    do {
        arr.add(i + 1)
        i = text.indexOf('\n', i + 1)
    } while (i != -1)
    arr.add(e + if (arr.last() == e) 1 else 0)
    val segmentTree = SegmentTree(arr.toIntArray())
    return { offset ->
        val line = segmentTree.indexOf(offset)
        if (line != -1) {
            val col = offset - segmentTree.get(line).left
            line + 1 to col + 1
        } else {
            1 to 1
        }
    }
}

/**
 * Extracted and adapted from KtLint, see
 * https://github.com/pinterest/ktlint/blob/a86d1c7/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt#L559
 */
internal class SegmentTree(sortedArray: IntArray) {

    private val segments: List<Segment>

    fun get(i: Int): Segment = segments[i]
    fun indexOf(v: Int): Int = binarySearch(v, 0, this.segments.size - 1)

    private fun binarySearch(v: Int, l: Int, r: Int): Int = if (l > r) -1 else {
        val i = l + (r - l) / 2
        val s = segments[i]
        if (v < s.left) binarySearch(v, l, i - 1)
        else (if (s.right < v) binarySearch(v, i + 1, r) else i)
    }

    init {
        require(sortedArray.size > 1) { "At least two data points are required" }
        sortedArray.reduce { r, v -> require(r <= v) { "Data points are not sorted (ASC)" }; v }
        segments = sortedArray.take(sortedArray.size - 1)
            .mapIndexed { i: Int, v: Int -> Segment(v, sortedArray[i + 1] - 1) }
    }
}

/**
 * Extracted and adapted from KtLint, see
 * https://github.com/pinterest/ktlint/blob/a86d1c7/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt#L583
 */
internal data class Segment(val left: Int, val right: Int)
