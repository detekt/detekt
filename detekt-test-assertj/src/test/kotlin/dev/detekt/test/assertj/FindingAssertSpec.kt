package dev.detekt.test.assertj

import dev.detekt.api.testfixtures.createFinding
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FindingAssertSpec {
    @Nested
    inner class Suppress {
        @Test
        fun `noSuppress with null value`() {
            assertThatThrownBy { FindingAssert(null).noSuppress() }
                .isExactlyInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting actual not to be null")
        }

        @Test
        fun `noSuppress without Suppression`() {
            val finding = createFinding(suppressReasons = emptyList())
            FindingAssert(finding).noSuppress()
        }

        @Test
        fun `noSuppress with 1 Suppression`() {
            val finding = createFinding(suppressReasons = listOf("reason1"))
            assertThatThrownBy { FindingAssert(finding).noSuppress() }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expect no suppressions but [reason1] was found")
                .hasActual(listOf("reason1"))
                .hasExpected(emptyList<String>())
        }

        @Test
        fun `noSuppress with 2 Suppression`() {
            val finding = createFinding(suppressReasons = listOf("reason1", "reason2"))
            assertThatThrownBy { FindingAssert(finding).noSuppress() }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expect no suppressions but [reason1, reason2] was found")
                .hasActual(listOf("reason1", "reason2"))
                .hasExpected(emptyList<String>())
        }
    }
}
