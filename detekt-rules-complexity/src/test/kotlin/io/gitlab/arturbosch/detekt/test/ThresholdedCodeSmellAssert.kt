package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.assertj.core.api.AbstractAssert

fun assertThat(thresholdedCodeSmell: ThresholdedCodeSmell) = ThresholdedCodeSmellAssert(thresholdedCodeSmell)

fun FindingAssert.isThresholded(): ThresholdedCodeSmellAssert {
    isNotNull
    assert(actual is ThresholdedCodeSmell) { "The finding '$actual' is not a ThresholdedCodeSmell" }
    return ThresholdedCodeSmellAssert(actual as ThresholdedCodeSmell?)
}

class ThresholdedCodeSmellAssert(actual: ThresholdedCodeSmell?) :
    AbstractAssert<ThresholdedCodeSmellAssert, ThresholdedCodeSmell>(
        actual,
        ThresholdedCodeSmellAssert::class.java
    ) {

    fun withValue(expected: Int) = apply { hasValue(expected) }

    fun hasValue(expected: Int) {
        isNotNull

        val smell = actual as ThresholdedCodeSmell
        if (expected != smell.metric.value) {
            failWithMessage("Expected value to be <%s> but was <%s>", expected, smell.metric.value)
        }
    }

    fun withThreshold(expected: Int) = apply { hasThreshold(expected) }

    fun hasThreshold(expected: Int) {
        isNotNull

        val smell = actual as ThresholdedCodeSmell
        if (expected != smell.metric.threshold) {
            failWithMessage("Expected threshold to be <%s> but was <%s>", expected, smell.metric.threshold)
        }
    }
}
