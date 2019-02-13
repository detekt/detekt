package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class DebtSpec : Spek({

    describe("creating issues with custom debt values") {
        it("should fail on negative values") {
            assertThatIllegalArgumentException().isThrownBy { Debt(-1, -1, -1) }
        }

        it("should fail if all values are less than zero ") {
            assertThatIllegalArgumentException().isThrownBy { Debt(0, 0, 0) }
        }

        it("should print 20min, 10min and 5min") {
            assertThat(Debt.TWENTY_MINS.toString()).isEqualTo("20min")
            assertThat(Debt.TEN_MINS.toString()).isEqualTo("10min")
            assertThat(Debt.FIVE_MINS.toString()).isEqualTo("5min")
        }

        it("day, hours and min combinations should work") {
            assertThat(Debt(1, 20, 20).toString()).isEqualTo("1d 20h 20min")
            assertThat(Debt(1, 20, 0).toString()).isEqualTo("1d 20h")
            assertThat(Debt(0, 20, 0).toString()).isEqualTo("20h")
            assertThat(Debt(1, 0, 20).toString()).isEqualTo("1d 20min")
        }
    }
})
