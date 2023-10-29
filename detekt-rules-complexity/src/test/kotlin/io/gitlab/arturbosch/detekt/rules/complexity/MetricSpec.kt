package io.gitlab.arturbosch.detekt.rules.complexity

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test

class MetricSpec {

    @Test
    fun `should convert double values to int`() {
        val metric = Metric(0.33, 0.10, 100)
        assertThat(metric.doubleValue()).isEqualTo(0.33)
        assertThat(metric.doubleThreshold()).isEqualTo(0.10)
    }

    @Test
    fun `should throw error if double value is asked for int metric`() {
        assertThatIllegalStateException().isThrownBy {
            Metric(100, 50).doubleValue()
        }
    }
}
