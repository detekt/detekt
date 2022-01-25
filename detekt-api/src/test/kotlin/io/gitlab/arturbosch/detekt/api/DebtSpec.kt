package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DebtSpec {

    @Nested
    inner class `creating issues with custom debt values` {

        @Test
        fun `should fail on negative values`() {
            assertThatIllegalArgumentException().isThrownBy { Debt(-1, 5, 5) }
            assertThatIllegalArgumentException().isThrownBy { Debt(5, -1, 5) }
            assertThatIllegalArgumentException().isThrownBy { Debt(5, 5, -1) }
        }

        @Test
        fun `should fail if all values are less than zero `() {
            assertThatIllegalArgumentException().isThrownBy { Debt(0, 0, 0) }
        }

        @Test
        fun `should print 20min, 10min and 5min`() {
            assertThat(Debt.TWENTY_MINS.toString()).isEqualTo("20min")
            assertThat(Debt.TEN_MINS.toString()).isEqualTo("10min")
            assertThat(Debt.FIVE_MINS.toString()).isEqualTo("5min")
        }

        @Test
        fun `day, hours and min combinations should work`() {
            assertThat(Debt(1, 20, 20).toString()).isEqualTo("1d 20h 20min")
            assertThat(Debt(1, 20, 0).toString()).isEqualTo("1d 20h")
            assertThat(Debt(0, 20, 0).toString()).isEqualTo("20h")
            assertThat(Debt(1, 0, 20).toString()).isEqualTo("1d 20min")
        }
    }

    @Nested
    inner class `add minutes, hours and days to debt` {

        private val debt = Debt(0, 22, 59)

        @Test
        fun `adds 1 min`() {
            val result = debt + Debt(mins = 1)
            assertThat(result.toString()).isEqualTo("23h")
        }

        @Test
        fun `adds 1 h and 1 min`() {
            val result = debt + Debt(hours = 1, mins = 1)
            assertThat(result.toString()).isEqualTo("1d")
        }

        @Test
        fun `doubles the debt`() {
            val result = debt + debt
            assertThat(result.toString()).isEqualTo("1d 21h 58min")
        }
    }
}
