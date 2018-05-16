package io.gitlab.arturbosch.detekt.formatting

import java.util.ArrayList

/**
 * Extracted and adapted from KtLint.
 *
 * @author Artur Bosch
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
	val segmentTree = SegmentTree(arr.toTypedArray())
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

internal fun calculateLineBreakOffset(fileContent: String): (offset: Int) -> Int {
	val arr = ArrayList<Int>()
	var i = 0
	do {
		arr.add(i)
		i = fileContent.indexOf("\r\n", i + 1)
	} while (i != -1)
	arr.add(fileContent.length)
	return if (arr.size != 2) {
		SegmentTree(arr.toTypedArray()).let { return { offset -> it.indexOf(offset) } }
	} else { _ ->
		0
	}
}

internal class SegmentTree(sortedArray: Array<Int>) {

	private val segments: List<Segment>

	fun get(i: Int): Segment = segments[i]
	fun indexOf(v: Int): Int = binarySearch(v, 0, this.segments.size - 1)

	private fun binarySearch(v: Int, l: Int, r: Int): Int = when {
		l > r -> -1
		else -> {
			val i = l + (r - l) / 2
			val s = segments[i]
			if (v < s.left) binarySearch(v, l, i - 1)
			else (if (s.right < v) binarySearch(v, i + 1, r) else i)
		}
	}

	init {
		require(sortedArray.size > 1) { "At least two data points are required" }
		sortedArray.reduce { r, v -> require(r <= v) { "Data points are not sorted (ASC)" }; v }
		segments = sortedArray.take(sortedArray.size - 1)
				.mapIndexed { i: Int, v: Int -> Segment(v, sortedArray[i + 1] - 1) }
	}
}

internal data class Segment(val left: Int, val right: Int)
