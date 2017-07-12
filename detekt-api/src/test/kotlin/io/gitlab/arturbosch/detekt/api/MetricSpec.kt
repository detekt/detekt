package io.gitlab.arturbosch.detekt.api

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * @author Artur Bosch
 */
class MetricSpec : Spek({

	it("should convert double values to int") {
		val metric = Metric("LOC", 0.33, 0.10, 100)
		assertEquals(0.33, metric.doubleValue())
		assertEquals(0.10, metric.doubleThreshold())
	}

	it("should throw error if double value is asked for int metric") {
		assertFails {
			Metric("LOC", 100, 50).doubleValue()
		}
	}

})
