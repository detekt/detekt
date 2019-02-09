package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.assertj.core.api.AbstractAssert
import org.assertj.core.internal.Objects

fun assertThat(thresholdedCodeSmell: ThresholdedCodeSmell) = ThresholdedCodeSmellAssert(thresholdedCodeSmell)

@Suppress("UnsafeCast") // False positive, see issue #1137
fun FindingAssert.isThresholded(): ThresholdedCodeSmellAssert {
    isNotNull
    assert(actual is ThresholdedCodeSmell) { "The finding '$actual' is not a ThresholdedCodeSmell" }
    return ThresholdedCodeSmellAssert(actual as ThresholdedCodeSmell?)
}

class ThresholdedCodeSmellAssert(actual: ThresholdedCodeSmell?) :
        AbstractAssert<ThresholdedCodeSmellAssert, ThresholdedCodeSmell>(
                actual, ThresholdedCodeSmellAssert::class.java) {

    fun withValue(expected: Int) = hasValue(expected).let { this }

    private val objects = Objects.instance()

    @Suppress("UnsafeCast") // False positive, see issue #1137
    fun hasValue(expected: Int) {
        isNotNull

        val smell = actual as ThresholdedCodeSmell
        objects.assertEqual(writableAssertionInfo, smell.value, expected)
    }

    fun withThreshold(expected: Int) = hasThreshold(expected).let { this }

    @Suppress("UnsafeCast") // False positive, see issue #1137
    fun hasThreshold(expected: Int) {
        isNotNull

        val smell = actual as ThresholdedCodeSmell
        objects.assertEqual(writableAssertionInfo, smell.threshold, expected)
    }
}
