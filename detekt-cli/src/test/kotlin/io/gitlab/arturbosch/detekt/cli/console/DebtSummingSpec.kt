package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Debt
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DebtSummingSpec : Spek({

    describe("debt minutes, hours and days") {

        it("outputs correct minutes, hours and days") {
            assertThat(createDebtSumming(1, 23, 62).toString()).isEqualTo("2d 2min")
        }

        it("outputs correct minutes and hours") {
            assertThat(createDebtSumming(hours = 0, mins = 62).toString()).isEqualTo("1h 2min")
        }

        it("outputs correct minutes") {
            assertThat(createDebtSumming(mins = 42).toString()).isEqualTo("42min")
        }
    }
})

private fun createDebtSumming(days: Int = 0, hours: Int = 0, mins: Int): Debt {
    val debt = Debt(days, hours, mins)
    val debtReport = DebtSumming()
    debtReport.add(debt)
    return debtReport.calculateDebt()
}
