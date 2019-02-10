package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class UnderscoresInNumericLiteralsSpec : Spek({

    given("an Int of 1000") {
        val ktFile = compileContentForTest("val myInt = 1000")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableDecimalLength is 5") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "5"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("an Int of 1_000_000") {
        val ktFile = compileContentForTest("val myInt = 1_000_000")

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a const Int of 1000000") {
        val ktFile = compileContentForTest("const val myInt = 1000000")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableDecimalLength is 8") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "8"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a Float of 1000f") {
        val ktFile = compileContentForTest("val myFloat = 1000f")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableDecimalLength is 5") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "5"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a Float of -1000f") {
        val ktFile = compileContentForTest("val myFloat = -1000f")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableDecimalLength is 5") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "5"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a Float of -1_000f") {
        val ktFile = compileContentForTest("const val myFloat = -1_000f")

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a Long of 1000000L") {
        val ktFile = compileContentForTest("const val myLong = 1000000L")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if ignored acceptableDecimalLength is 8") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "8"))
            ).lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a Double of 1_000_000.00_000_000") {
        val ktFile = compileContentForTest("val myDouble = 1_000_000.00_000_000")

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a function with default Int parameter value 1000") {
        val ktFile = compileContentForTest("fun testFunction(testParam: Int = 1000) {}")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }
    }

    given("an annotation with numeric literals 0 and 10") {
        val ktFile = compileContentForTest(
                "fun setCustomDimension(@IntRange(from = 0, to = 10) index: Int, value: String?) {}"
        )

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("an annotation with numeric literals 0 and 1000000") {
        val ktFile = compileContentForTest(
                "fun setCustomDimension(@IntRange(from = 0, to = 1000000) index: Int, value: String?) {}"
        )

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }
    }

    given("an Int of 10_00_00") {
        val ktFile = compileContentForTest("const val myInt = 10_00_00")

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }

        it("should still be reported even if acceptableDecimalLength is 7") {
            val findings = UnderscoresInNumericLiterals(
                    TestConfig(mapOf(UnderscoresInNumericLiterals.ACCEPTABLE_DECIMAL_LENGTH to "7"))
            ).lint(ktFile)
            assertThat(findings).isNotEmpty
        }
    }

    given("a binary Int of 0b1011") {
        val ktFile = compileContentForTest("const val myBinInt = 0b1011")

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a hexadecimal Int of 0x1facdf") {
        val ktFile = compileContentForTest("const val myHexInt = 0x1facdf")

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a property named serialVersionUID in an object that implements Serializable") {
        val ktFile = compileContentForTest("""
            object TestSerializable : Serializable {
                private val serialVersionUID = 314159L
            }
        """.trimIndent())

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isEmpty()
        }
    }

    given("a property named serialVersionUID in an object that does not implement Serializable") {
        val ktFile = compileContentForTest("""
            object TestSerializable {
                private val serialVersionUID = 314159L
            }
        """.trimIndent())

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(ktFile)
            assertThat(findings).isNotEmpty
        }
    }
})
