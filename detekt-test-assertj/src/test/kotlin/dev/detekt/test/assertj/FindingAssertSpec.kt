package dev.detekt.test.assertj

import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.api.testfixtures.createEntity
import dev.detekt.api.testfixtures.createFinding
import dev.detekt.api.testfixtures.createLocation
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FindingAssertSpec {
    private val finding = createFinding(
        entity = createEntity(createLocation(source = 1 to 1, endSource = 1 to 11, text = 0..10)),
        message = "TestMessage",
    )

    @Nested
    inner class Message {
        @Test
        fun `hasMessage with null value`() {
            assertThatThrownBy { FindingAssert(null).hasMessage("") }
                .isExactlyInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting actual not to be null")
        }

        @Test
        fun hasMessage() {
            FindingAssert(finding).hasMessage("TestMessage")
        }

        @Test
        fun `hasMessage failing`() {
            assertThatThrownBy { FindingAssert(finding).hasMessage("Other Message") }
                .isInstanceOfAssertionFailedError()
                .hasMessage("""Expected message "Other Message" but actual message was "TestMessage"""")
                .hasActual("TestMessage")
                .hasExpected("Other Message")
        }
    }

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

    @Nested
    inner class SourceLocationChecks {
        @Nested
        inner class Start {
            @Test
            fun `hasStartSourceLocation with null value`() {
                assertThatThrownBy { FindingAssert(null).hasStartSourceLocation(SourceLocation(1, 1)) }
                    .isExactlyInstanceOf(AssertionError::class.java)
                    .hasMessage("\nExpecting actual not to be null")
            }

            @Test
            fun `hasStartSourceLocationInt with null value`() {
                assertThatThrownBy { FindingAssert(null).hasStartSourceLocation(1, 1) }
                    .isExactlyInstanceOf(AssertionError::class.java)
                    .hasMessage("\nExpecting actual not to be null")
            }

            @Test
            fun hasStartSourceLocation() {
                FindingAssert(finding).hasStartSourceLocation(SourceLocation(1, 1))
            }

            @Test
            fun hasStartSourceLocationInt() {
                FindingAssert(finding).hasStartSourceLocation(1, 1)
            }

            @Test
            fun `hasStartSourceLocation failing`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(SourceLocation(2, 5)) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected start source location to be 2:5 but was 1:1")
                    .hasActual(SourceLocation(1, 1))
                    .hasExpected(SourceLocation(2, 5))
            }

            @Test
            fun `hasStartSourceLocationInt failing`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(2, 5) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected start source location to be 2:5 but was 1:1")
                    .hasActual(SourceLocation(1, 1))
                    .hasExpected(SourceLocation(2, 5))
            }
        }

        @Nested
        inner class End {
            @Test
            fun `hasEndSourceLocation with null value`() {
                assertThatThrownBy { FindingAssert(null).hasEndSourceLocation(SourceLocation(1, 1)) }
                    .isExactlyInstanceOf(AssertionError::class.java)
                    .hasMessage("\nExpecting actual not to be null")
            }

            @Test
            fun `hasEndSourceLocationInt with null value`() {
                assertThatThrownBy { FindingAssert(null).hasEndSourceLocation(1, 1) }
                    .isExactlyInstanceOf(AssertionError::class.java)
                    .hasMessage("\nExpecting actual not to be null")
            }

            @Test
            fun hasEndSourceLocation() {
                FindingAssert(finding).hasEndSourceLocation(SourceLocation(1, 11))
            }

            @Test
            fun hasEndSourceLocationInt() {
                FindingAssert(finding).hasEndSourceLocation(1, 11)
            }

            @Test
            fun `hasEndSourceLocation failing`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(SourceLocation(2, 14)) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected end source location to be 2:14 but was 1:11")
                    .hasActual(SourceLocation(1, 11))
                    .hasExpected(SourceLocation(2, 14))
            }

            @Test
            fun `hasEndSourceLocationInt failing`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(2, 14) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected end source location to be 2:14 but was 1:11")
                    .hasActual(SourceLocation(1, 11))
                    .hasExpected(SourceLocation(2, 14))
            }
        }
    }

    @Nested
    inner class TextLocationChecks {
        @Test
        fun `hasTextLocation with null value`() {
            assertThatThrownBy { FindingAssert(null).hasTextLocation(TextLocation(1, 1)) }
                .isExactlyInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting actual not to be null")
        }

        @Test
        fun `hasTextLocationPair with null value`() {
            assertThatThrownBy { FindingAssert(null).hasTextLocation(1 to 1) }
                .isExactlyInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting actual not to be null")
        }

        @Test
        fun hasTextLocation() {
            FindingAssert(finding).hasTextLocation(TextLocation(0, 10))
        }

        @Test
        fun hasTextLocationPair() {
            FindingAssert(finding).hasTextLocation(0 to 10)
        }

        @Test
        fun `hasTextLocation failing`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation(TextLocation(17, 26)) }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expected text location to be 17:26 but was 0:10")
                .hasActual(TextLocation(0, 10))
                .hasExpected(TextLocation(17, 26))
        }

        @Test
        fun `hasTextLocationPair failing`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation(17 to 26) }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expected text location to be 17:26 but was 0:10")
                .hasActual(TextLocation(0, 10))
                .hasExpected(TextLocation(17, 26))
        }
    }
}
