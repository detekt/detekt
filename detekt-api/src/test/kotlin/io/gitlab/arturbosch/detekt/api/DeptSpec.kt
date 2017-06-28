package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFails

/**
 * @author Artur Bosch
 */
class DeptSpec : Spek({

	describe("creating issues with custom dept values") {
		it("should fail on negative values") {
			assertFails { Dept(-1, -1, -1) }
		}

		it("should fail if all values are less than zero ") {
			assertFails { Dept(0, 0, 0) }
		}

		it("should print 20min, 10min and 5min") {
			assertThat(Dept.TWENTY_MINS.toString()).isEqualTo("20min")
			assertThat(Dept.TEN_MINS.toString()).isEqualTo("10min")
			assertThat(Dept.FIVE_MINS.toString()).isEqualTo("5min")
		}

		it("day, hours and min combinations should work") {
			assertThat(Dept(1, 20, 20).toString()).isEqualTo("1d 20h 20min")
			assertThat(Dept(1, 20, 0).toString()).isEqualTo("1d 20h")
			assertThat(Dept(0, 20, 0).toString()).isEqualTo("20h")
			assertThat(Dept(1, 0, 20).toString()).isEqualTo("1d 20min")
		}
	}
})