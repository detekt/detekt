package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DebtSpec : Spek({

    describe("creating issues with custom debt values") {
        it("should fail on negative values") {
            assertThatIllegalArgumentException().isThrownBy { Debt(-1, 5, 5) }
            assertThatIllegalArgumentException().isThrownBy { Debt(5, -1, 5) }
            assertThatIllegalArgumentException().isThrownBy { Debt(5, 5, -1) }
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

    describe("debt minutes, hours and days") {

        it("outputs correct minutes, hours and days") {
            val debt = createFormattedDebtTime(1, 23, 62)
            assertThat(debt).isEqualTo("2d 2min")
        }

        it("outputs correct minutes and hours") {
            val debt = createFormattedDebtTime(hours = 0, mins = 62)
            assertThat(debt).isEqualTo("1h 2min")
        }

        it("outputs correct minutes") {
            val debt = createFormattedDebtTime(mins = 42)
            assertThat(debt).isEqualTo("42min")
        }
    }
})

private fun createFormattedDebtTime(days: Int = 0, hours: Int = 0, mins: Int): String {
    return Debt(days, hours, mins)
        .formatDebtTime()
        .toString()
}
