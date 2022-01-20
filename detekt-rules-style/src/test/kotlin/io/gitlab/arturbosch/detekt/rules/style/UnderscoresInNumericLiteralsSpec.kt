package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val ACCEPTABLE_DECIMAL_LENGTH = "acceptableDecimalLength"
private const val ACCEPTABLE_LENGTH = "acceptableLength"
private const val ALLOW_NON_STANDARD_GROUPING = "allowNonStandardGrouping"

class UnderscoresInNumericLiteralsSpec : Spek({

    describe("an Int of 1000") {
        val code = "val myInt = 1000"

        it("should not be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("should be reported if acceptableLength is 3") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "3"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should be reported if deprecated acceptableDecimalLength is 4") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_DECIMAL_LENGTH to "4"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("an Int of 1_000_000") {
        val code = "val myInt = 1_000_000"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a const Int of 1000000") {
        val code = "val myInt = 1000000"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableLength is 7") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "7"))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Float of 1000f") {
        val code = "val myFloat = 1000f"

        it("should not be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("should be reported if acceptableLength is 3") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "3"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("a Float of -1000f") {
        val code = "val myFloat = -1000f"

        it("should not be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("should be reported if acceptableLength is 3") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "3"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("a Float of -1_000f") {
        val code = "val myFloat = -1_000f"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Long of 1000000L") {
        val code = "val myLong = 1000000L"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if ignored acceptableLength is 7") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "7"))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Double of 1_000_000.00_000_000") {
        val code = "val myDouble = 1_000_000.00_000_000"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a function with default Int parameter value 1000") {
        val code = "fun testFunction(testParam: Int = 1000) {}"

        it("should not be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("should be reported if acceptableLength is 3") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "3"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("an annotation with numeric literals 0 and 10") {
        val code = "fun setCustomDimension(@IntRange(from = 0, to = 10) index: Int, value: String?) {}"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().lint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("an annotation with numeric literals 0 and 1000000") {
        val code = "fun setCustomDimension(@IntRange(from = 0, to = 1000000) index: Int, value: String?) {}"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().lint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("an Int of 1000_00_00") {
        val code = "val myInt = 1000_00_00"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should still be reported even if acceptableLength is 99") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "99"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if allowNonStandardGrouping is true") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ALLOW_NON_STANDARD_GROUPING to true))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a binary Int of 0b1011") {
        val code = "val myBinInt = 0b1011"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a hexadecimal Int of 0x1facdf") {
        val code = "val myHexInt = 0x1facdf"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a hexadecimal Int of 0xFFFFFF") {
        val code = "val myHexInt = 0xFFFFFF"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a property named serialVersionUID in an object that implements Serializable") {
        val code = """
            import java.io.Serializable
            
            object TestSerializable : Serializable {
                private val serialVersionUID = 314159L
            }
        """

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a property named serialVersionUID in an object that does not implement Serializable") {
        val code = """
            object TestSerializable {
                private val serialVersionUID = 314159L
            }
        """

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("a property named serialVersionUID in an object that does not implement Serializable") {
        val code = """
            object TestSerializable {
                private val serialVersionUID = 314_159L
            }
        """

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a property named serialVersionUID in a companion object inside a serializable class") {

        it("does not report a negative serialVersionUID number") {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    companion object {
                        private const val serialVersionUID = -43857148126114372L
                    }
                }"""
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("does not report a positive serialVersionUID number") {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    companion object {
                        private const val serialVersionUID = 43857148126114372L
                    }
                }"""
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).hasSize(0)
        }
    }

    describe("a property named serialVersionUID number in a serializable class") {

        it("does not report a negative serialVersionUID number") {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    private val serialVersionUID = -43857148126114372L
                }
            """
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("does not report a positive serialVersionUID number") {
            val code = """
                import java.io.Serializable
                
                class Test : Serializable {
                    private val serialVersionUID = 43857148126114372L
                }
            """
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).hasSize(0)
        }
    }

    describe("an Int of 10000") {
        val code = "val myInt = 10000"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableLength is 5") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "5"))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Float of 30_000_000.1415926535897932385") {
        val code = "val myFloat = 30_000_000.1415926535897932385f"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Double of 3.1415926535897932385") {
        val code = "val myDouble = 3.1415926535897932385"

        it("should not be reported") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a Double of 3000.1415926535897932385") {
        val code = "val myDouble = 3000.1415926535897932385"

        it("should be reported if acceptableLength is 3") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "3"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty
        }
    }

    describe("a Float of 1000000.31415926535f") {
        val code = "val myFloat = 1000000.31415926535f"

        it("should be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isNotEmpty
        }

        it("should not be reported if acceptableLength is 7") {
            val findings = UnderscoresInNumericLiterals(
                TestConfig(mapOf(ACCEPTABLE_LENGTH to "7"))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    describe("a String of 1000000.3141592") {
        val code = """val myString = "1000000.3141592""""

        it("should not be reported by default") {
            val findings = UnderscoresInNumericLiterals().compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
})
