package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class MetricSpec : Spek({

	it("should convert double values to int") {
		val metric = Metric("LOC", 0.33, 0.10, 100)
		assertThat(metric.doubleValue()).isEqualTo(0.33)
		assertThat(metric.doubleThreshold()).isEqualTo(0.10)
	}

	it("should throw error if double value is asked for int metric") {
		assertThatIllegalStateException().isThrownBy {
			Metric("LOC", 100, 50).doubleValue()
		}
	}
})
