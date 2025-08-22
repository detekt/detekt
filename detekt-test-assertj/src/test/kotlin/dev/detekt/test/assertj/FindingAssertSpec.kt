package dev.detekt.test.assertj

import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.api.testfixtures.createEntity
import dev.detekt.api.testfixtures.createFinding
import dev.detekt.api.testfixtures.createLocation
import dev.detekt.test.utils.internal.FakeKtElement
import dev.detekt.test.utils.internal.FakePsiFile
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FindingAssertSpec {
    private val finding = createFinding(
        entity = createEntity(
            location = createLocation(source = 1 to 1, endSource = 1 to 11, text = 0..10),
            ktElement = FakeKtElement(
                FakePsiFile(
                    """
                        fun test() {
                            val a = 1
                            val b = 2
                            fun foo() {
                                val b = 2
                            }
                        }
                    """.trimIndent()
                )
            ),
        ),
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
                    .hasActual(
                        """
                            📍fun test() {
                                val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
                    .hasExpected(
                        """
                            fun test() {
                                📍val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
            }

            @Test
            fun `hasStartSourceLocationInt failing`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(2, 5) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected start source location to be 2:5 but was 1:1")
                    .hasActual(
                        """
                            📍fun test() {
                                val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
                    .hasExpected(
                        """
                            fun test() {
                                📍val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
            }

            @Test
            fun `hasStartSourceLocation failing when expected line doesn't even exist`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(SourceLocation(20, 1)) }
                    .isInstanceOf(IndexOutOfBoundsException::class.java)
                    .hasMessage("The line 20 doesn't exist in the file. The file has 7 lines")
            }

            @Test
            fun `hasStartSourceLocationInt failing when expected column doesn't even exist`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(1, 20) }
                    .isInstanceOf(IndexOutOfBoundsException::class.java)
                    .hasMessage("The column 20 doesn't exist in the line 1. The line has 13 columns")
            }

            @Test
            fun `hasStartSourceLocation failing when expected line is the last one`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(SourceLocation(7, 1)) }
                    .isInstanceOf(AssertionError::class.java)
            }

            @Test
            fun `hasStartSourceLocationInt failing when expected column is the last one`() {
                assertThatThrownBy { FindingAssert(finding).hasStartSourceLocation(1, 13) }
                    .isInstanceOf(AssertionError::class.java)
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
                    .hasActual(
                        """
                            fun test()📍 {
                                val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
                    .hasExpected(
                        """
                            fun test() {
                                val a = 1📍
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
            }

            @Test
            fun `hasEndSourceLocationInt failing`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(2, 14) }
                    .isInstanceOfAssertionFailedError()
                    .hasMessage("Expected end source location to be 2:14 but was 1:11")
                    .hasActual(
                        """
                            fun test()📍 {
                                val a = 1
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
                    .hasExpected(
                        """
                            fun test() {
                                val a = 1📍
                                val b = 2
                                fun foo() {
                                    val b = 2
                                }
                            }
                        """.trimIndent()
                    )
            }

            @Test
            fun `hasEndSourceLocation failing when expected line doesn't even exist`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(SourceLocation(20, 1)) }
                    .isInstanceOf(IndexOutOfBoundsException::class.java)
                    .hasMessage("The line 20 doesn't exist in the file. The file has 7 lines")
            }

            @Test
            fun `hasEndSourceLocationInt failing when expected column doesn't even exist`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(1, 20) }
                    .isInstanceOf(IndexOutOfBoundsException::class.java)
                    .hasMessage("The column 20 doesn't exist in the line 1. The line has 13 columns")
            }

            @Test
            fun `hasEndSourceLocation failing when expected line is the last one`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(SourceLocation(7, 1)) }
                    .isInstanceOf(AssertionError::class.java)
            }

            @Test
            fun `hasEndSourceLocationInt failing when expected column is the last one`() {
                assertThatThrownBy { FindingAssert(finding).hasEndSourceLocation(1, 13) }
                    .isInstanceOf(AssertionError::class.java)
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
        fun `hasTextLocationString with null value`() {
            assertThatThrownBy { FindingAssert(null).hasTextLocation("val a = 1") }
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
        fun hasTextLocationString() {
            FindingAssert(finding).hasTextLocation("fun test()")
        }

        @Test
        fun `hasTextLocation failing`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation(TextLocation(17, 26)) }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expected text location to be 17:26 but was 0:10")
                .hasActual("fun test()")
                .hasExpected("val a = 1")
        }

        @Test
        fun `hasTextLocationPair failing`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation(17 to 26) }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expected text location to be 17:26 but was 0:10")
                .hasActual("fun test()")
                .hasExpected("val a = 1")
        }

        @Test
        fun `hasTextLocationString failing`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation("val a = 1") }
                .isInstanceOfAssertionFailedError()
                .hasMessage("Expected text location to be 17:26 but was 0:10")
                .hasActual("fun test()")
                .hasExpected("val a = 1")
        }

        @Test
        fun `hasTextLocationString no occurrences`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation("val c = 3") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("""The snippet "val c = 3" doesn't exist in the code""")
        }

        @Test
        fun `hasTextLocationString multiple occurrences`() {
            assertThatThrownBy { FindingAssert(finding).hasTextLocation("val b = 2") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("""The snippet "val b = 2" appears multiple times in the code""")
        }
    }
}
