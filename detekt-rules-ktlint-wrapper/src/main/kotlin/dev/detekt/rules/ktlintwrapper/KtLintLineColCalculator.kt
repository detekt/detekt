package dev.detekt.rules.ktlintwrapper

/**
 * This object provides helper methods for calculation line and column number based on provided offset.
 *
 * This code duplicates [KtLint.normalizeText] and [KtLint.calculateLineColByOffset] methods,
 * since they are deprecated and probably will be removed in next releases.
 */
object KtLintLineColCalculator {
    private const val UTF8_BOM = "\uFEFF"

    fun calculateLineColByOffset(text: String): (offset: Int) -> Pair<Int, Int> =
        buildPositionInTextLocator(normalizeText(text))

    private fun normalizeText(text: String): String =
        text
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replaceFirst(UTF8_BOM, "")

    private fun buildPositionInTextLocator(text: String): (offset: Int) -> Pair<Int, Int> {
        val textLength = text.length
        val arr = ArrayList<Int>()

        var endOfLineIndex = -1
        do {
            arr.add(endOfLineIndex + 1)
            endOfLineIndex = text.indexOf('\n', endOfLineIndex + 1)
        } while (endOfLineIndex != -1)

        arr.add(textLength + if (arr.last() == textLength) 1 else 0)

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

    private class SegmentTree(sortedArray: IntArray) {

        init {
            require(sortedArray.size > 1) { "At least two data points are required" }
            sortedArray.reduce { current, next ->
                require(current <= next) { "Data points are not sorted (ASC)" }
                next
            }
        }

        private val segments: List<Segment> = sortedArray
            .dropLast(1)
            .mapIndexed { index: Int, element: Int ->
                Segment(element, sortedArray[index + 1] - 1)
            }

        fun get(i: Int): Segment = segments[i]
        fun indexOf(v: Int): Int = binarySearch(v, 0, segments.size - 1)

        private fun binarySearch(v: Int, l: Int, r: Int): Int =
            when {
                l > r -> -1

                else -> {
                    val i = l + (r - l) / 2
                    val s = segments[i]
                    if (v < s.left) {
                        binarySearch(v, l, i - 1)
                    } else {
                        if (s.right < v) binarySearch(v, i + 1, r) else i
                    }
                }
            }
    }

    private data class Segment(val left: Int, val right: Int)
}
